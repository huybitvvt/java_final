package com.bookstore.view;

import com.bookstore.controller.MainController;
import com.bookstore.util.ResponsiveLayoutUtil;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Handcrafted JavaFX main shell used instead of generated builder code.
 */
public final class MainView {

    private MainView() {
    }

    public static ViewBundle create() {
        MainController controller = new MainController();

        BorderPane root = new BorderPane();
        root.setPrefSize(1366, 768);
        root.getStyleClass().add("app-shell");

        VBox sidebar = new VBox();
        sidebar.setPrefWidth(292);
        sidebar.setMinWidth(292);
        sidebar.setMaxWidth(292);
        sidebar.setFillWidth(true);
        sidebar.getStyleClass().add("sidebar");

        VBox brandPanel = new VBox(8);
        brandPanel.setMaxWidth(Double.MAX_VALUE);
        brandPanel.getStyleClass().add("brand-panel");
        brandPanel.getChildren().addAll(
                label("BOOKSTORE OS", "brand-badge"),
                wrappingLabel("Quản lý cửa hàng sách", "brand-title"),
                wrappingLabel("Theo dõi bán hàng, kho và vận hành trong một không gian làm việc thống nhất.", "brand-subtitle")
        );

        HBox userPanel = new HBox(12);
        userPanel.setAlignment(Pos.CENTER_LEFT);
        userPanel.getStyleClass().add("user-panel");

        StackPane avatarPane = new StackPane();
        avatarPane.setPrefSize(46, 46);
        avatarPane.getStyleClass().add("avatar-circle");
        Label lblAvatarInitial = label("A", "avatar-text");
        GeneratedViewSupport.bind(controller, "lblAvatarInitial", lblAvatarInitial);
        avatarPane.getChildren().add(lblAvatarInitial);

        VBox userBox = new VBox(2);
        Label lblUserName = label("Xin chào, Admin", "user-name");
        GeneratedViewSupport.bind(controller, "lblUserName", lblUserName);
        Label lblUserRole = label("Quản trị viên", "user-role");
        GeneratedViewSupport.bind(controller, "lblUserRole", lblUserRole);
        userBox.getChildren().addAll(lblUserName, lblUserRole);
        userPanel.getChildren().addAll(avatarPane, userBox);

        VBox navGroups = new VBox(18);
        navGroups.getStyleClass().add("nav-groups");

        VBox grpSystem = section(controller, "grpSystem", "Hệ thống",
                navButton(controller, "btnDashboard", "Tổng quan", "showDashboard"));
        VBox grpManagement = section(controller, "grpManagement", "Quản lý",
                navButton(controller, "btnBooks", "Sách", "showBookManagement"),
                navButton(controller, "btnCustomers", "Khách hàng", "showCustomerManagement"),
                navButton(controller, "btnEmployees", "Nhân viên", "showEmployeeManagement"),
                navButton(controller, "btnSuppliers", "Nhà cung cấp", "showSupplierManagement"),
                navButton(controller, "btnPromotions", "Khuyến mãi", "showPromotionManagement"));
        VBox grpSales = section(controller, "grpSales", "Bán hàng",
                navButton(controller, "btnPOS", "Quầy thanh toán", "showPOS"),
                navButton(controller, "btnInvoices", "Hóa đơn", "showInvoiceManagement"));
        VBox grpImports = section(controller, "grpImports", "Nhập hàng",
                navButton(controller, "btnImports", "Phiếu nhập", "showImportManagement"));
        VBox grpReports = section(controller, "grpReports", "Báo cáo",
                navButton(controller, "btnStatistics", "Thống kê", "showRevenueStatistics"));
        VBox grpHelp = section(controller, "grpHelp", "Trợ giúp",
                navButton(controller, "btnHelp", "Giới thiệu", "showAbout"));

        navGroups.getChildren().addAll(grpSystem, grpManagement, grpSales, grpImports, grpReports, grpHelp);

        ScrollPane navScroll = new ScrollPane(navGroups);
        navScroll.setFitToWidth(true);
        navScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        navScroll.getStyleClass().add("nav-scroll");
        VBox.setVgrow(navScroll, Priority.ALWAYS);

        sidebar.getChildren().addAll(brandPanel, userPanel, navScroll);
        root.setLeft(sidebar);

        VBox workspace = new VBox(18);
        workspace.getStyleClass().add("workspace");

        HBox topbar = new HBox();
        topbar.setAlignment(Pos.CENTER_LEFT);
        topbar.getStyleClass().add("topbar");

        VBox titleBox = new VBox(2);
        Label lblBreadcrumb = label("Hệ thống", "breadcrumb-label");
        GeneratedViewSupport.bind(controller, "lblBreadcrumb", lblBreadcrumb);
        Label lblPageTitle = label("Tổng quan", "page-title");
        GeneratedViewSupport.bind(controller, "lblPageTitle", lblPageTitle);
        titleBox.getChildren().addAll(lblBreadcrumb, lblPageTitle);

        Region topbarSpacer = new Region();
        HBox.setHgrow(topbarSpacer, Priority.ALWAYS);

        Label lblDateTime = label("10/03/2026 08:30:00", "topbar-time");
        GeneratedViewSupport.bind(controller, "lblDateTime", lblDateTime);

        Button btnThemeToggle = new Button("Chế độ tối");
        btnThemeToggle.setPrefHeight(38);
        btnThemeToggle.getStyleClass().add("chip-button");
        GeneratedViewSupport.bindAction(btnThemeToggle, controller, "toggleTheme");
        GeneratedViewSupport.bind(controller, "btnThemeToggle", btnThemeToggle);

        MenuButton menuReports = new MenuButton("Báo cáo");
        menuReports.getStyleClass().add("chip-menu");
        GeneratedViewSupport.bind(controller, "menuReports", menuReports);
        menuReports.getItems().addAll(
                menuItem(controller, "showRevenueStatistics", "Thống kê doanh thu"),
                menuItem(controller, "showInventoryStatistics", "Thống kê tồn kho")
        );

        MenuButton menuSystem = new MenuButton("Hệ thống");
        menuSystem.getStyleClass().add("chip-menu");
        menuSystem.getItems().addAll(
                menuItem(controller, "showChangePasswordDialog", "Đổi mật khẩu"),
                menuItem(controller, "handleLogout", "Đăng xuất"),
                menuItem(controller, "handleExit", "Thoát")
        );

        topbar.getChildren().addAll(titleBox, topbarSpacer, lblDateTime, btnThemeToggle, menuReports, menuSystem);

        StackPane contentShell = new StackPane();
        contentShell.getStyleClass().add("content-shell");
        VBox.setVgrow(contentShell, Priority.ALWAYS);

        AnchorPane contentArea = new AnchorPane();
        GeneratedViewSupport.bind(controller, "contentArea", contentArea);

        StackPane loadingOverlay = new StackPane();
        loadingOverlay.setManaged(false);
        loadingOverlay.setVisible(false);
        loadingOverlay.getStyleClass().add("loading-overlay");
        GeneratedViewSupport.bind(controller, "loadingOverlay", loadingOverlay);

        VBox loadingBox = new VBox(12);
        loadingBox.setAlignment(Pos.CENTER);
        loadingBox.getChildren().addAll(
                progressIndicator(),
                label("Đang tải giao diện...", "loading-label")
        );
        loadingOverlay.getChildren().add(loadingBox);
        contentShell.getChildren().addAll(contentArea, loadingOverlay);

        workspace.getChildren().addAll(topbar, contentShell);
        root.setCenter(workspace);

        ResponsiveLayoutUtil.install(root);
        GeneratedViewSupport.initialize(controller);
        return new ViewBundle(root, controller);
    }

    private static VBox section(MainController controller, String fieldName, String title, Button... buttons) {
        VBox group = new VBox(8);
        GeneratedViewSupport.bind(controller, fieldName, group);
        group.getChildren().add(label(title, "nav-section-label"));
        group.getChildren().addAll(buttons);
        return group;
    }

    private static Button navButton(MainController controller, String fieldName, String text, String actionMethod) {
        Button button = new Button(text);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPrefHeight(44);
        button.setMaxWidth(Double.MAX_VALUE);
        button.getStyleClass().add("nav-button");
        GeneratedViewSupport.bindAction(button, controller, actionMethod);
        GeneratedViewSupport.bind(controller, fieldName, button);
        return button;
    }

    private static MenuItem menuItem(MainController controller, String actionMethod, String text) {
        MenuItem item = new MenuItem(text);
        GeneratedViewSupport.bindAction(item, controller, actionMethod);
        return item;
    }

    private static ProgressIndicator progressIndicator() {
        ProgressIndicator indicator = new ProgressIndicator();
        indicator.setPrefSize(44, 44);
        return indicator;
    }

    private static Label label(String text, String styleClass) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        return label;
    }

    private static Label wrappingLabel(String text, String styleClass) {
        Label label = label(text, styleClass);
        label.setWrapText(true);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setTextOverrun(OverrunStyle.CLIP);
        return label;
    }
}
