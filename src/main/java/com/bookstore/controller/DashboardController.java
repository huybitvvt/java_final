package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.model.Customer;
import com.bookstore.model.DashboardPoint;
import com.bookstore.model.TopBookSale;
import com.bookstore.service.DashboardService;
import com.bookstore.service.LoyaltyService;
import com.bookstore.util.FormatUtil;
import javafx.collections.FXCollections;
import com.bookstore.service.BookService;
import com.bookstore.service.CustomerService;
import com.bookstore.service.InvoiceService;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;

/**
 * Dashboard Controller - Hiển thị tổng quan
 */
public class DashboardController {
    private Label lblTotalBooks;
    private Label lblTotalCustomers;
    private Label lblTotalInvoicesToday;
    private Label lblRevenueToday;
    private Label lblAnalyticsContext;
    private Label lblAlertCount;
    private LineChart<String, Number> lineRevenueChart;
    private BarChart<String, Number> barTopBooksChart;
    private PieChart pieRevenueCategoryChart;
    private VBox lowStockList;
    private VBox topCustomerList;

    private final DashboardService dashboardService;
    private final LoyaltyService loyaltyService;

    public DashboardController() {
        this.dashboardService = new DashboardService();
        this.loyaltyService = new LoyaltyService();
    }
    public void initialize() {
        configureCharts();
        loadData();
    }

    private void loadData() {
        lblTotalBooks.setText(String.valueOf(dashboardService.getTotalBooks()));
        lblTotalCustomers.setText(String.valueOf(dashboardService.getTotalCustomers()));
        lblTotalInvoicesToday.setText(String.valueOf(dashboardService.getTodayInvoices()));
        lblRevenueToday.setText(FormatUtil.formatCurrency(dashboardService.getTodayRevenue()));
        lblAnalyticsContext.setText("Dữ liệu dashboard được neo theo ngày hóa đơn mới nhất: "
                + dashboardService.getLatestInvoiceDateLabel() + " • " + dashboardService.getCurrentMonthLabel());

        loadRevenueChart();
        loadTopBooksChart();
        loadCategoryChart();
        renderLowStockAlerts();
        renderTopCustomers();
    }

    private void configureCharts() {
        lineRevenueChart.setCreateSymbols(true);
        lineRevenueChart.setAnimated(false);
        barTopBooksChart.setAnimated(false);
        pieRevenueCategoryChart.setAnimated(false);

        ((NumberAxis) lineRevenueChart.getYAxis()).setForceZeroInRange(false);
        ((NumberAxis) barTopBooksChart.getYAxis()).setForceZeroInRange(true);
    }

    private void loadRevenueChart() {
        lineRevenueChart.getData().clear();
        List<DashboardPoint> points = dashboardService.getRevenueByDay(7);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doanh thu");

        for (DashboardPoint point : points) {
            series.getData().add(new XYChart.Data<>(point.label(), point.value()));
        }

        if (!series.getData().isEmpty()) {
            lineRevenueChart.getData().add(series);
        }
    }

    private void loadTopBooksChart() {
        barTopBooksChart.getData().clear();
        List<TopBookSale> topBooks = dashboardService.getTopSellingBooks(5);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Số lượng bán");

        for (TopBookSale item : topBooks) {
            series.getData().add(new XYChart.Data<>(truncate(item.bookName(), 18), item.quantity()));
        }

        if (!series.getData().isEmpty()) {
            barTopBooksChart.getData().add(series);
        }
    }

    private void loadCategoryChart() {
        Map<String, Double> revenueByCategory = dashboardService.getRevenueByCategory();
        pieRevenueCategoryChart.setData(FXCollections.observableArrayList());
        revenueByCategory.forEach((category, revenue) ->
                pieRevenueCategoryChart.getData().add(new PieChart.Data(category, revenue)));
    }

    private void renderLowStockAlerts() {
        List<Book> lowStockBooks = dashboardService.getLowStockBooks();
        lowStockList.getChildren().clear();

        if (lowStockBooks == null || lowStockBooks.isEmpty()) {
            lblAlertCount.setText("Không có mục cần xử lý");
            lowStockList.getChildren().add(createEmptyLabel("Tồn kho đang ở mức an toàn."));
            return;
        }

        lblAlertCount.setText(lowStockBooks.size() + " mục cần xử lý");
        lowStockBooks.stream()
                .limit(5)
                .map(this::createLowStockItem)
                .forEach(lowStockList.getChildren()::add);
    }

    private void renderTopCustomers() {
        List<Customer> customers = dashboardService.getTopCustomers(4);
        topCustomerList.getChildren().clear();

        if (customers == null || customers.isEmpty()) {
            topCustomerList.getChildren().add(createEmptyLabel("Chưa có dữ liệu khách hàng nổi bật."));
            return;
        }

        for (int i = 0; i < customers.size(); i++) {
            topCustomerList.getChildren().add(createTopCustomerItem(customers.get(i), i + 1));
        }
    }

    private VBox createLowStockItem(Book book) {
        VBox card = new VBox(6);
        card.getStyleClass().add("list-item-card");

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label(book.getTenSach());
        title.getStyleClass().addAll("label", "list-item-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label quantityBadge = new Label("Còn " + book.getSoLuongTon());
        quantityBadge.getStyleClass().addAll("label", "status-badge", "status-badge-danger");

        Label meta = new Label(
                (book.getTheLoai() != null ? book.getTheLoai() : "Chưa phân loại")
                        + " • Mã " + book.getMaSach()
                        + " • " + FormatUtil.formatCurrency(book.getGiaBia()));
        meta.getStyleClass().addAll("label", "list-item-meta");

        header.getChildren().addAll(title, spacer, quantityBadge);
        card.getChildren().addAll(header, meta);
        return card;
    }

    private VBox createTopCustomerItem(Customer customer, int rank) {
        VBox card = new VBox(6);
        card.getStyleClass().add("list-item-card");

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label name = new Label(customer.getHoTen());
        name.getStyleClass().addAll("label", "list-item-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label rankBadge = new Label("Top " + rank);
        rankBadge.getStyleClass().addAll("label", "status-badge", "status-badge-info");

        String tier = loyaltyService.resolveTier(customer);
        Label tierBadge = new Label(tier);
        tierBadge.getStyleClass().addAll("label", "tier-badge", getTierStyleClass(tier));

        HBox footer = new HBox(8);
        footer.setAlignment(Pos.CENTER_LEFT);
        Label spendInfo = new Label(FormatUtil.formatCurrency(customer.getTongChiTieu()) + " • " + customer.getDiemTichLuy() + " điểm");
        spendInfo.getStyleClass().addAll("label", "list-item-meta");
        footer.getChildren().addAll(tierBadge, spendInfo);

        header.getChildren().addAll(name, spacer, rankBadge);
        card.getChildren().addAll(header, footer);
        return card;
    }

    private Label createEmptyLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().addAll("label", "empty-state-label");
        label.setWrapText(true);
        return label;
    }

    private String getTierStyleClass(String tier) {
        return switch (tier) {
            case "Diamond" -> "tier-badge-diamond";
            case "Gold" -> "tier-badge-gold";
            case "Silver" -> "tier-badge-silver";
            default -> "tier-badge-standard";
        };
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength - 1) + "…";
    }
}

