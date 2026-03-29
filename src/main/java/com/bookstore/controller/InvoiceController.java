package com.bookstore.controller;

import com.bookstore.model.Invoice;
import com.bookstore.service.InvoiceService;
import com.bookstore.util.ActionButtonFactory;
import com.bookstore.util.AnimationUtil;
import com.bookstore.util.AppLog;
import com.bookstore.util.DialogService;
import com.bookstore.util.ExceptionMessageUtil;
import com.bookstore.util.FormatUtil;
import com.bookstore.util.ExportPDFUtil;
import com.bookstore.util.ExportExcelUtil;
import com.bookstore.util.IconUtil;
import com.bookstore.util.InputFieldUtil;
import com.bookstore.util.NotificationService;
import com.bookstore.util.ResponsiveLayoutUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Invoice Controller
 */
public class InvoiceController {
    private VBox pageRoot;
    private HBox headerBar;
    private VBox contentCard;
    private TextField txtSearch;
    private Button btnSearch;
    private Button btnRefresh;
    private Button btnExport;
    private TableView<Invoice> tableInvoices;
    private TableColumn<Invoice, String> colMaHD;
    private TableColumn<Invoice, Date> colNgayLap;
    private TableColumn<Invoice, String> colKhachHang;
    private TableColumn<Invoice, String> colNhanVien;
    private TableColumn<Invoice, Double> colTongTien;
    private TableColumn<Invoice, Double> colGiamGia;
    private TableColumn<Invoice, Double> colThanhToan;
    private TableColumn<Invoice, String> colTrangThai;
    private TableColumn<Invoice, String> colHanhDong;

    private InvoiceService invoiceService;
    private ObservableList<Invoice> invoiceList;
    private final boolean compactMode;
    private static final SimpleDateFormat COMPACT_DATE_TIME = new SimpleDateFormat("dd/MM/yy HH:mm");

    public InvoiceController() {
        this.invoiceService = new InvoiceService();
        this.invoiceList = FXCollections.observableArrayList();
        this.compactMode = ResponsiveLayoutUtil.isCompactScreen();
    }
    public void initialize() {
        setupTableColumns();
        setupLayout();
        decorateToolbar();
        if (compactMode) {
            applyCompactLayout();
        }
        tableInvoices.getSortOrder().clear();
        tableInvoices.getSortOrder().add(colNgayLap);
        colNgayLap.setSortType(TableColumn.SortType.DESCENDING);
        loadInvoices();
        AnimationUtil.playStagger(List.of(headerBar, contentCard));
        InputFieldUtil.bindKeywordSearch(txtSearch, this::handleSearch);
        InputFieldUtil.installCarryOverWorkaround(txtSearch);
    }

    private void setupTableColumns() {
        colMaHD.setCellValueFactory(new PropertyValueFactory<>("maHoaDonString"));
        colNgayLap.setCellValueFactory(new PropertyValueFactory<>("ngayLap"));
        colKhachHang.setCellValueFactory(cellData -> new SimpleStringProperty(resolveCustomerName(cellData.getValue())));
        colNhanVien.setCellValueFactory(cellData -> new SimpleStringProperty(resolveEmployeeName(cellData.getValue())));
        colTongTien.setCellValueFactory(new PropertyValueFactory<>("tongTien"));
        colGiamGia.setCellValueFactory(new PropertyValueFactory<>("giamGia"));
        colThanhToan.setCellValueFactory(new PropertyValueFactory<>("thanhToan"));
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));

        colNgayLap.setCellFactory(column -> new TableCell<Invoice, Date>() {
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    setText(compactMode ? COMPACT_DATE_TIME.format(item) : FormatUtil.formatDateTime(item));
                }
            }
        });

        colTongTien.setCellFactory(column -> new TableCell<Invoice, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText("");
                else setText(FormatUtil.formatCurrency(item));
            }
        });

        colThanhToan.setCellFactory(column -> new TableCell<Invoice, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText("");
                else setText(FormatUtil.formatCurrency(item));
            }
        });

        colGiamGia.setCellFactory(column -> new TableCell<Invoice, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText("");
                else setText(String.format("%.0f%%", item));
            }
        });

        colTrangThai.setCellFactory(column -> new TableCell<Invoice, String>() {
            private final Label badge = new Label();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }

                badge.setText(item);
                badge.getStyleClass().removeAll("status-badge-success", "status-badge-info", "status-badge-warning");
                badge.getStyleClass().add("status-badge");
                if ("Đã thanh toán".equalsIgnoreCase(item) || "Hoàn tất".equalsIgnoreCase(item)) {
                    badge.getStyleClass().add("status-badge-success");
                } else if ("Chờ xử lý".equalsIgnoreCase(item)) {
                    badge.getStyleClass().add("status-badge-warning");
                } else {
                    badge.getStyleClass().add("status-badge-info");
                }
                setGraphic(badge);
            }
        });

        colHanhDong.setSortable(false);
        colHanhDong.setCellFactory(column -> new TableCell<Invoice, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Button btnView = ActionButtonFactory.createViewButton(
                            e -> viewInvoice(getTableView().getItems().get(getIndex())));
                    setGraphic(btnView);
                }
            }
        });
    }

    private void setupLayout() {
        tableInvoices.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tableInvoices.setFixedCellSize(48);
        tableInvoices.setPlaceholder(createPlaceholder("Chưa có hóa đơn nào hoặc chưa có kết quả phù hợp."));
        applyScrollableColumnWidths();
    }

    private Label createPlaceholder(String message) {
        Label placeholder = new Label(message);
        placeholder.getStyleClass().add("empty-state-label");
        return placeholder;
    }

    private void decorateToolbar() {
        IconUtil.apply(btnSearch, "fas-search", "Tìm hóa đơn");
        IconUtil.apply(btnRefresh, "fas-sync-alt", "Nạp lại toàn bộ danh sách hóa đơn");
        IconUtil.apply(btnExport, "fas-file-export", "Xuất PDF hoặc Excel");
    }

    private void applyCompactLayout() {
        btnSearch.setText("Tìm");
        btnRefresh.setText("Mới");
        btnExport.setText("Xuất");
        tableInvoices.setFixedCellSize(42);
        applyScrollableColumnWidths();
    }

    private void applyScrollableColumnWidths() {
        if (compactMode) {
            setColumnWidth(colMaHD, 100);
            setColumnWidth(colNgayLap, 165);
            setColumnWidth(colKhachHang, 190);
            setColumnWidth(colNhanVien, 170);
            setColumnWidth(colTongTien, 140);
            setColumnWidth(colGiamGia, 110);
            setColumnWidth(colThanhToan, 140);
            setColumnWidth(colTrangThai, 140);
            setColumnWidth(colHanhDong, 90);
            return;
        }

        setColumnWidth(colMaHD, 110);
        setColumnWidth(colNgayLap, 180);
        setColumnWidth(colKhachHang, 220);
        setColumnWidth(colNhanVien, 180);
        setColumnWidth(colTongTien, 160);
        setColumnWidth(colGiamGia, 120);
        setColumnWidth(colThanhToan, 160);
        setColumnWidth(colTrangThai, 150);
        setColumnWidth(colHanhDong, 100);
    }

    private void setColumnWidth(TableColumn<?, ?> column, double width) {
        column.prefWidthProperty().unbind();
        column.setMinWidth(width);
        column.setPrefWidth(width);
    }

    private void loadInvoices() {
        List<Invoice> invoices = invoiceService.getAllInvoices();
        if (invoices != null) {
            invoices.sort((left, right) -> right.getNgayLap().compareTo(left.getNgayLap()));
            invoiceList.clear();
            invoiceList.addAll(invoices);
            tableInvoices.setItems(invoiceList);
            tableInvoices.sort();
        }
    }
    private void handleSearch() {
        String keyword = txtSearch.getText().trim();
        if (!keyword.isEmpty()) {
            List<Invoice> invoices = invoiceService.searchInvoices(keyword);
            invoiceList.clear();
            if (invoices != null) invoiceList.addAll(invoices);
        } else {
            loadInvoices();
        }
    }
    private void handleRefresh() {
        txtSearch.clear();
        loadInvoices();
    }
    private void handleExport() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xuất dữ liệu");
        alert.setHeaderText(null);
        alert.setContentText("Chọn định dạng báo cáo muốn xuất.");

        ButtonType btnPDF = new ButtonType("Xuất PDF");
        ButtonType btnExcel = new ButtonType("Xuất Excel");
        ButtonType btnCancel = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(btnPDF, btnExcel, btnCancel);
        alert.getDialogPane().getStyleClass().add("modern-dialog");
        if (tableInvoices.getScene() != null) {
            alert.getDialogPane().getStylesheets().setAll(tableInvoices.getScene().getStylesheets());
        }
        if (alert.getDialogPane().lookupButton(btnPDF) instanceof Button pdfButton) {
            pdfButton.getStyleClass().add("primary-button");
        }
        if (alert.getDialogPane().lookupButton(btnExcel) instanceof Button excelButton) {
            excelButton.getStyleClass().add("chip-button");
        }
        if (alert.getDialogPane().lookupButton(btnCancel) instanceof Button cancelButton) {
            cancelButton.getStyleClass().add("chip-button");
        }

        alert.showAndWait().ifPresent(result -> {
            if (result == btnPDF) {
                exportToPDF();
            } else if (result == btnExcel) {
                exportToExcel();
            }
        });
    }

    private void exportToPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu file PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName("HoaDon_" + System.currentTimeMillis() + ".pdf");

        File file = fileChooser.showSaveDialog(tableInvoices.getScene().getWindow());
        if (file != null) {
            try {
                // Export all invoices to PDF
                List<Invoice> invoices = invoiceService.getAllInvoices();
                if (invoices != null && !invoices.isEmpty()) {
                    // Create report data
                    java.util.List<Object[]> data = new java.util.ArrayList<>();
                    double totalRevenue = 0;
                    for (Invoice inv : invoices) {
                        Object[] row = new Object[3];
                        row[0] = inv.getMaHoaDonString();
                        row[1] = FormatUtil.formatDate(inv.getNgayLap());
                        row[2] = inv.getThanhToan();
                        data.add(row);
                        totalRevenue += inv.getThanhToan();
                    }

                    boolean success = ExportPDFUtil.exportRevenueReport(data, totalRevenue, file.getAbsolutePath());
                    if (success) {
                        NotificationService.showSuccess(tableInvoices, "Xuất PDF hóa đơn thành công");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Lỗi", "Xuất PDF thất bại!");
                    }
                } else {
                    showAlert(Alert.AlertType.WARNING, "Thông báo", "Không có dữ liệu để xuất!");
                }
            } catch (RuntimeException e) {
                AppLog.error(InvoiceController.class, "Khong the xuat hoa don ra PDF", e);
                showAlert(Alert.AlertType.ERROR, "Lỗi",
                        "Lỗi khi xuất PDF: " + ExceptionMessageUtil.resolve(e, "Lỗi không xác định"));
            }
        }
    }

    private void exportToExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu file Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName("DanhSachHoaDon_" + System.currentTimeMillis() + ".xlsx");

        File file = fileChooser.showSaveDialog(tableInvoices.getScene().getWindow());
        if (file != null) {
            try {
                List<Invoice> invoices = invoiceService.getAllInvoices();
                if (invoices != null && !invoices.isEmpty()) {
                    boolean success = ExportExcelUtil.exportInvoices(invoices, file.getAbsolutePath());
                    if (success) {
                        NotificationService.showSuccess(tableInvoices, "Xuất Excel hóa đơn thành công");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Lỗi", "Xuất Excel thất bại!");
                    }
                } else {
                    showAlert(Alert.AlertType.WARNING, "Thông báo", "Không có dữ liệu để xuất!");
                }
            } catch (RuntimeException e) {
                AppLog.error(InvoiceController.class, "Khong the xuat danh sach hoa don ra Excel", e);
                showAlert(Alert.AlertType.ERROR, "Lỗi",
                        "Lỗi khi xuất Excel: " + ExceptionMessageUtil.resolve(e, "Lỗi không xác định"));
            }
        }
    }

    private void viewInvoice(Invoice invoice) {
        showAlert(Alert.AlertType.INFORMATION, "Chi tiết hóa đơn",
                "Mã: " + invoice.getMaHoaDonString() + "\n" +
                "Khách hàng: " + resolveCustomerName(invoice) + "\n" +
                "Nhân viên: " + resolveEmployeeName(invoice) + "\n" +
                "Tổng tiền: " + FormatUtil.formatCurrency(invoice.getTongTien()) + "\n" +
                "Thanh toán: " + FormatUtil.formatCurrency(invoice.getThanhToan()));
    }

    private String resolveCustomerName(Invoice invoice) {
        if (invoice == null) {
            return "";
        }
        if (invoice.getKhachHang() != null && invoice.getKhachHang().getHoTen() != null
                && !invoice.getKhachHang().getHoTen().isBlank()) {
            return invoice.getKhachHang().getHoTen();
        }
        if (invoice.getMaKhachHang() <= 0) {
            return "Khách lẻ";
        }
        return "KH #" + invoice.getMaKhachHang();
    }

    private String resolveEmployeeName(Invoice invoice) {
        if (invoice == null) {
            return "";
        }
        if (invoice.getNhanVien() != null && invoice.getNhanVien().getHoTen() != null
                && !invoice.getNhanVien().getHoTen().isBlank()) {
            return invoice.getNhanVien().getHoTen();
        }
        if (invoice.getMaNhanVien() > 0) {
            return "NV #" + invoice.getMaNhanVien();
        }
        return "";
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        switch (type) {
            case ERROR -> DialogService.showError(tableInvoices, title, message);
            case WARNING -> DialogService.showWarning(tableInvoices, title, message);
            default -> DialogService.showInfo(tableInvoices, title, message);
        }
    }
}

