package com.bookstore.controller;

import com.bookstore.model.Invoice;
import com.bookstore.service.BookService;
import com.bookstore.service.CustomerService;
import com.bookstore.service.InvoiceService;
import com.bookstore.util.AppLog;
import com.bookstore.util.ChartUtil;
import com.bookstore.util.DialogService;
import com.bookstore.util.ExceptionMessageUtil;
import com.bookstore.util.ExportPDFUtil;
import com.bookstore.util.FormatUtil;
import com.bookstore.util.NotificationService;
import javafx.scene.chart.Chart;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Statistics Controller
 */
public class StatisticsController {

    private static final DateTimeFormatter CHART_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM");
    private static final DateTimeFormatter REPORT_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter REPORT_TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private Label lblTotalRevenue;
    private Label lblTotalInvoices;
    private Label lblAvgInvoice;
    private Label lblReportBadge;
    private Label lblReportStatus;
    private DatePicker dpStartDate;
    private DatePicker dpEndDate;
    private ComboBox<String> cboPeriod;
    private AnchorPane chartRevenuePane;
    private AnchorPane chartCategoryPane;

    private InvoiceService invoiceService;
    private BookService bookService;
    private CustomerService customerService;

    public StatisticsController() {
        this.invoiceService = new InvoiceService();
        this.bookService = new BookService();
        this.customerService = new CustomerService();
    }
    public void initialize() {
        // Initialize period ComboBox
        if (cboPeriod != null) {
            cboPeriod.getItems().addAll("7 ngày gần nhất", "30 ngày gần nhất", "90 ngày gần nhất", "Tùy chọn");
            cboPeriod.setValue("7 ngày gần nhất");
            cboPeriod.valueProperty().addListener((obs, oldVal, newVal) -> applyPeriodSelection(newVal));
        }

        initializeDateRange();
        refreshStatistics(false);
    }
    private void handleGenerate() {
        refreshStatistics(true);
    }

    private void refreshStatistics(boolean showFeedback) {
        LocalDate startLocal = dpStartDate.getValue();
        LocalDate endLocal = dpEndDate.getValue();

        if (startLocal == null || endLocal == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn ngày bắt đầu và kết thúc!");
            return;
        }
        if (endLocal.isBefore(startLocal)) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Ngày kết thúc không được trước ngày bắt đầu!");
            return;
        }

        Date startDate = Date.from(startLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endLocal.plusDays(1).atStartOfDay(ZoneId.systemDefault()).minusNanos(1).toInstant());

        List<Invoice> invoices = invoiceService.getInvoicesByDateRange(startDate, endDate);
        List<Invoice> paidInvoices = filterPaidInvoices(invoices);
        double revenue = paidInvoices.stream().mapToDouble(Invoice::getThanhToan).sum();
        int count = paidInvoices.size();
        double avg = count > 0 ? revenue / count : 0;

        // Update labels
        lblTotalRevenue.setText(FormatUtil.formatCurrency(revenue));
        lblTotalInvoices.setText(String.valueOf(count));
        lblAvgInvoice.setText(FormatUtil.formatCurrency(avg));
        updateReportSummary(startLocal, endLocal, count, revenue);

        // Generate charts
        generateRevenueChart(startLocal, endLocal, paidInvoices);
        generateCategoryChart();

        if (showFeedback) {
            NotificationService.showSuccess(chartRevenuePane, "Đã cập nhật báo cáo thống kê.");
        }
    }

    private void generateRevenueChart(LocalDate startDate, LocalDate endDate, List<Invoice> invoices) {
        if (chartRevenuePane == null) {
            return;
        }

        if (invoices == null || invoices.isEmpty()) {
            showChartPlaceholder(chartRevenuePane, "Không có doanh thu trong khoảng thời gian đã chọn.");
            return;
        }

        Map<LocalDate, Double> revenueByDate = new LinkedHashMap<>();
        LocalDate cursor = startDate;
        while (!cursor.isAfter(endDate)) {
            revenueByDate.put(cursor, 0.0);
            cursor = cursor.plusDays(1);
        }

        for (Invoice invoice : invoices) {
            if (invoice == null || invoice.getNgayLap() == null) {
                continue;
            }
            LocalDate invoiceDate = invoice.getNgayLap().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            revenueByDate.computeIfPresent(invoiceDate, (date, total) -> total + invoice.getThanhToan());
        }

        List<String> dates = revenueByDate.keySet().stream()
                .map(CHART_DATE_FORMATTER::format)
                .toList();
        List<Double> revenues = new ArrayList<>(revenueByDate.values());

        Chart chart = ChartUtil.createRevenueLineChart(dates, revenues);

        chartRevenuePane.getChildren().clear();
        AnchorPane.setTopAnchor(chart, 0.0);
        AnchorPane.setBottomAnchor(chart, 0.0);
        AnchorPane.setLeftAnchor(chart, 0.0);
        AnchorPane.setRightAnchor(chart, 0.0);
        chartRevenuePane.getChildren().add(chart);
    }

    private void generateCategoryChart() {
        // Get books grouped by category
        List<com.bookstore.model.Book> books = bookService.getAllBooks();
        if (books == null || books.isEmpty()) {
            showChartPlaceholder(chartCategoryPane, "Không có dữ liệu thể loại để hiển thị.");
            return;
        }

        Map<String, Integer> categoryCount = books.stream()
                .collect(Collectors.groupingBy(
                        book -> book.getTheLoai() != null ? book.getTheLoai() : "Chưa phân loại",
                        Collectors.summingInt(book -> 1)
                ));

        Chart chart = ChartUtil.createCategoryPieChart(categoryCount);

        chartCategoryPane.getChildren().clear();
        AnchorPane.setTopAnchor(chart, 0.0);
        AnchorPane.setBottomAnchor(chart, 0.0);
        AnchorPane.setLeftAnchor(chart, 0.0);
        AnchorPane.setRightAnchor(chart, 0.0);
        chartCategoryPane.getChildren().add(chart);
    }
    private void handleExport() {
        LocalDate startLocal = dpStartDate.getValue();
        LocalDate endLocal = dpEndDate.getValue();

        if (startLocal == null || endLocal == null) {
            startLocal = LocalDate.now().minusDays(7);
            endLocal = LocalDate.now();
        }

        Date startDate = Date.from(startLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endLocal.plusDays(1).atStartOfDay(ZoneId.systemDefault()).minusNanos(1).toInstant());

        List<Invoice> invoices = filterPaidInvoices(invoiceService.getInvoicesByDateRange(startDate, endDate));
        if (invoices == null || invoices.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thông báo", "Không có dữ liệu để xuất!");
            return;
        }

        // Create report data
        List<Object[]> data = new ArrayList<>();
        double totalRevenue = 0;
        for (Invoice inv : invoices) {
            Object[] row = new Object[3];
            row[0] = inv.getMaHoaDonString();
            row[1] = FormatUtil.formatDate(inv.getNgayLap());
            row[2] = inv.getThanhToan();
            data.add(row);
            totalRevenue += inv.getThanhToan();
        }

        // Show save dialog
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Lưu file PDF");
        fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName("BaoCaoDoanhThu_" + System.currentTimeMillis() + ".pdf");

        java.io.File file = fileChooser.showSaveDialog(chartRevenuePane.getScene().getWindow());
        if (file != null) {
            try {
                boolean success = ExportPDFUtil.exportRevenueReport(data, totalRevenue, file.getAbsolutePath());
                if (success) {
                    NotificationService.showSuccess(chartRevenuePane, "Xuất PDF báo cáo thành công");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Xuất PDF thất bại!");
                }
            } catch (RuntimeException e) {
                AppLog.error(StatisticsController.class, "Khong the xuat bao cao PDF", e);
                showAlert(Alert.AlertType.ERROR, "Lỗi",
                        "Lỗi khi xuất PDF: " + ExceptionMessageUtil.resolve(e, "Không thể xuất báo cáo"));
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        switch (type) {
            case ERROR -> DialogService.showError(chartRevenuePane, title, message);
            case WARNING -> DialogService.showWarning(chartRevenuePane, title, message);
            default -> DialogService.showInfo(chartRevenuePane, title, message);
        }
    }

    private void updateReportSummary(LocalDate startDate, LocalDate endDate, int invoiceCount, double revenue) {
        if (lblReportBadge != null) {
            lblReportBadge.setText("Cập nhật " + LocalDateTime.now().format(REPORT_TIMESTAMP_FORMATTER));
        }
        if (lblReportStatus != null) {
            lblReportStatus.setText(String.format(
                    "Đang hiển thị báo cáo từ %s đến %s: %d hóa đơn đã thanh toán, tổng doanh thu %s.",
                    startDate.format(REPORT_DATE_FORMATTER),
                    endDate.format(REPORT_DATE_FORMATTER),
                    invoiceCount,
                    FormatUtil.formatCurrency(revenue)
            ));
        }
    }

    private void applyPeriodSelection(String period) {
        if (period == null) {
            return;
        }
        LocalDate anchorDate = dpEndDate.getValue() != null ? dpEndDate.getValue() : getLatestInvoiceDate();
        switch (period) {
            case "7 ngày gần nhất" -> {
                dpStartDate.setValue(anchorDate.minusDays(6));
                dpEndDate.setValue(anchorDate);
            }
            case "30 ngày gần nhất" -> {
                dpStartDate.setValue(anchorDate.minusDays(29));
                dpEndDate.setValue(anchorDate);
            }
            case "90 ngày gần nhất" -> {
                dpStartDate.setValue(anchorDate.minusDays(89));
                dpEndDate.setValue(anchorDate);
            }
            default -> {
                return;
            }
        }
        refreshStatistics(false);
    }

    private void initializeDateRange() {
        LocalDate latestInvoiceDate = getLatestInvoiceDate();
        dpEndDate.setValue(latestInvoiceDate);
        dpStartDate.setValue(latestInvoiceDate.minusDays(6));
    }

    private LocalDate getLatestInvoiceDate() {
        List<Invoice> invoices = invoiceService.getAllInvoices();
        if (invoices == null || invoices.isEmpty()) {
            return LocalDate.now();
        }
        return invoices.stream()
                .map(Invoice::getNgayLap)
                .filter(Objects::nonNull)
                .max(Date::compareTo)
                .map(date -> date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                .orElse(LocalDate.now());
    }

    private List<Invoice> filterPaidInvoices(List<Invoice> invoices) {
        if (invoices == null || invoices.isEmpty()) {
            return Collections.emptyList();
        }
        return invoices.stream()
                .filter(Objects::nonNull)
                .filter(invoice -> "Đã thanh toán".equalsIgnoreCase(invoice.getTrangThai()))
                .toList();
    }

    private void showChartPlaceholder(AnchorPane pane, String message) {
        if (pane == null) {
            return;
        }
        Label placeholder = new Label(message);
        placeholder.getStyleClass().add("empty-state-label");
        pane.getChildren().setAll(placeholder);
        AnchorPane.setTopAnchor(placeholder, 24.0);
        AnchorPane.setLeftAnchor(placeholder, 24.0);
    }
}

