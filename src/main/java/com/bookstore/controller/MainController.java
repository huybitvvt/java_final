package com.bookstore.controller;

import com.bookstore.model.User;
import com.bookstore.service.UserService;
import com.bookstore.session.SessionContext;
import com.bookstore.util.AppLog;
import com.bookstore.util.AnimationUtil;
import com.bookstore.util.DialogService;
import com.bookstore.util.ExceptionMessageUtil;
import com.bookstore.util.IconUtil;
import com.bookstore.util.PasswordUtil;
import com.bookstore.util.ThemeManager;
import com.bookstore.util.TooltipUtil;
import com.bookstore.util.ValidationUtil;
import com.bookstore.util.ViewLoaderUtil;
import com.bookstore.view.ViewBundle;
import com.bookstore.view.ViewId;
import javafx.application.Platform;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Main Controller - Điều hướng chính
 */
public class MainController {
    private Label lblUserName;
    private Label lblUserRole;
    private Label lblAvatarInitial;
    private Label lblDateTime;
    private Label lblBreadcrumb;
    private Label lblPageTitle;
    private AnchorPane contentArea;
    private StackPane loadingOverlay;
    private VBox grpSystem;
    private VBox grpManagement;
    private VBox grpSales;
    private VBox grpImports;
    private VBox grpReports;
    private VBox grpHelp;
    private Button btnDashboard;
    private Button btnBooks;
    private Button btnCustomers;
    private Button btnEmployees;
    private Button btnSuppliers;
    private Button btnPromotions;
    private Button btnPOS;
    private Button btnInvoices;
    private Button btnImports;
    private Button btnStatistics;
    private Button btnHelp;
    private Button btnThemeToggle;
    private MenuButton menuReports;

    private User currentUser;
    private final UserService userService = new UserService();
    private Timeline clockTimeline;
    private PauseTransition navigationDelay;
    private static final String NAV_ACTIVE_CLASS = "nav-button-active";
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", new Locale("vi", "VN"));
    public void initialize() {
        setupChrome();
        startClock();
        updateThemeToggleText();
    }

    /**
     * Set current user and update UI
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        updateUserInfo();
        applyRoleVisibility();
        updateThemeToggleText();
        if (isAdmin()) {
            showDashboard();
        } else {
            showPOS();
        }
    }

    /**
     * Update user info display
     */
    private void updateUserInfo() {
        if (currentUser != null) {
            String vaiTro = currentUser.getVaiTro();
            String role = ("Admin".equals(vaiTro) || "Quản lý".equals(vaiTro)) ? "Quản trị viên" : "Nhân viên";
            lblUserName.setText("Xin chào, " + currentUser.getTenDangNhap());
            lblUserRole.setText(role);
            String username = currentUser.getTenDangNhap();
            lblAvatarInitial.setText(username != null && !username.isBlank()
                    ? username.substring(0, 1).toUpperCase(Locale.ROOT)
                    : "U");
        }
    }
    public void showDashboard() {
        if (!ensureAdminAccess()) {
            return;
        }
        navigateTo(ViewId.DASHBOARD, "Hệ thống", "Tổng quan", btnDashboard);
    }
    public void showBookManagement() {
        if (!ensureAdminAccess()) {
            return;
        }
        navigateTo(ViewId.BOOK, "Quản lý", "Quản lý sách", btnBooks);
    }
    public void showCustomerManagement() {
        String breadcrumb = isAdmin() ? "Quản lý" : "Tra cứu";
        String pageTitle = isAdmin() ? "Khách hàng" : "Tra cứu khách hàng";
        navigateTo(ViewId.CUSTOMER, breadcrumb, pageTitle, btnCustomers);
    }
    public void showEmployeeManagement() {
        if (!ensureAdminAccess()) {
            return;
        }
        navigateTo(ViewId.EMPLOYEE, "Quản lý", "Nhân viên", btnEmployees);
    }
    public void showSupplierManagement() {
        if (!ensureAdminAccess()) {
            return;
        }
        navigateTo(ViewId.SUPPLIER, "Quản lý", "Nhà cung cấp", btnSuppliers);
    }
    public void showPromotionManagement() {
        if (!ensureAdminAccess()) {
            return;
        }
        navigateTo(ViewId.PROMOTION, "Quản lý", "Khuyến mãi", btnPromotions);
    }
    public void showPOS() {
        navigateTo(ViewId.POS, "Bán hàng", "Quầy thanh toán", btnPOS);
    }
    public void showInvoiceManagement() {
        navigateTo(ViewId.INVOICE, "Bán hàng", "Hóa đơn", btnInvoices);
    }
    public void showImportManagement() {
        if (!ensureAdminAccess()) {
            return;
        }
        navigateTo(ViewId.IMPORT, "Nhập hàng", "Phiếu nhập", btnImports);
    }
    public void showRevenueStatistics() {
        if (!ensureAdminAccess()) {
            return;
        }
        navigateTo(ViewId.STATISTICS, "Báo cáo", "Thống kê doanh thu", btnStatistics);
    }
    public void showInventoryStatistics() {
        if (!ensureAdminAccess()) {
            return;
        }
        navigateTo(ViewId.INVENTORY, "Báo cáo", "Thống kê tồn kho", btnStatistics);
    }
    public void toggleTheme() {
        Scene scene = contentArea != null ? contentArea.getScene() : null;
        ThemeManager.toggleTheme(scene);
        updateThemeToggleText();
        setupThemeToggleIcon();
    }
    public void showChangePasswordDialog() {
        if (currentUser == null) {
            DialogService.showWarning(lblUserName, "Đổi mật khẩu", "Không tìm thấy phiên đăng nhập hiện tại.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Đổi mật khẩu");
        DialogService.attachToOwner(dialog, lblUserName);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStyleClass().add("modern-dialog");
        if (lblUserName != null && lblUserName.getScene() != null) {
            dialogPane.getStylesheets().setAll(lblUserName.getScene().getStylesheets());
        }

        ButtonType cancelType = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType saveType = new ButtonType("Cập nhật", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().setAll(cancelType, saveType);
        dialogPane.setPrefWidth(420);

        PasswordField txtOldPassword = passwordField("Nhập mật khẩu hiện tại");
        PasswordField txtNewPassword = passwordField("Ít nhất 6 ký tự");
        PasswordField txtConfirmPassword = passwordField("Nhập lại mật khẩu mới");

        Label lblError = new Label();
        lblError.getStyleClass().add("error-label");
        lblError.setWrapText(true);
        lblError.setVisible(false);
        lblError.managedProperty().bind(lblError.visibleProperty());

        VBox content = new VBox(12);
        content.getChildren().addAll(
                formBlock("Mật khẩu hiện tại", txtOldPassword),
                formBlock("Mật khẩu mới", txtNewPassword),
                formBlock("Xác nhận mật khẩu mới", txtConfirmPassword),
                lblError
        );
        dialogPane.setContent(content);

        if (dialogPane.lookupButton(cancelType) instanceof Button cancelButton) {
            cancelButton.getStyleClass().add("chip-button");
        }

        final boolean[] changed = {false};
        if (dialogPane.lookupButton(saveType) instanceof Button saveButton) {
            saveButton.getStyleClass().add("primary-button");
            saveButton.addEventFilter(ActionEvent.ACTION, event -> {
                String validationMessage = validatePasswordChange(
                        txtOldPassword.getText(),
                        txtNewPassword.getText(),
                        txtConfirmPassword.getText()
                );

                if (validationMessage != null) {
                    lblError.setText(validationMessage);
                    lblError.setVisible(true);
                    event.consume();
                    return;
                }

                boolean success = userService.changePassword(currentUser,
                        txtOldPassword.getText(),
                        txtNewPassword.getText());
                if (!success) {
                    lblError.setText("Mật khẩu hiện tại không đúng hoặc không thể cập nhật vào hệ thống.");
                    lblError.setVisible(true);
                    event.consume();
                    return;
                }

                currentUser.setMatKhau(PasswordUtil.md5(txtNewPassword.getText()));
                SessionContext.setCurrentUser(currentUser);
                changed[0] = true;
            });
        }

        dialog.showAndWait();
        if (changed[0]) {
            DialogService.showInfo(lblUserName, "Thành công", "Đổi mật khẩu thành công.");
        }
    }
    public void handleLogout() {
        if (DialogService.confirm(lblUserName, "Đăng xuất", "Bạn có chắc chắn muốn đăng xuất?", "Đăng xuất")) {
            try {
                SessionContext.clear();
                ViewBundle view = ViewLoaderUtil.loadView(ViewId.LOGIN);
                Parent root = view.getRoot();

                Stage stage = (Stage) lblUserName.getScene().getWindow();
                Scene scene = new Scene(root);
                ThemeManager.applyTheme(scene);
                stage.setScene(scene);
                stage.setMaximized(true);
                stage.show();
            } catch (RuntimeException e) {
                AppLog.error(MainController.class, "Khong the quay ve man hinh dang nhap", e);
                showAlert(Alert.AlertType.ERROR, "Lỗi",
                        "Không thể chuyển về màn hình đăng nhập: "
                                + ExceptionMessageUtil.resolve(e, "Lỗi không xác định"));
            }
        }
    }
    public void handleExit() {
        if (DialogService.confirm(lblUserName, "Thoát", "Bạn có chắc chắn muốn thoát?", "Thoát")) {
            System.exit(0);
        }
    }
    public void showAbout() {
        DialogService.showInfo(lblUserName, "Giới thiệu",
                "Tên nhóm: 2k6\n"
                        + "Liên hệ: abc@gmail.com\n"
                        + "SĐT: 012345678\n\n"
                        + "Cảm ơn thầy/cô và mọi người đã theo dõi, góp ý cho sản phẩm của nhóm.");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        switch (type) {
            case ERROR -> DialogService.showError(lblUserName, title, message);
            case WARNING -> DialogService.showWarning(lblUserName, title, message);
            default -> DialogService.showInfo(lblUserName, title, message);
        }
    }

    private void startClock() {
        updateDateTimeLabel();
        clockTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, event -> updateDateTimeLabel()),
                new KeyFrame(Duration.seconds(1))
        );
        clockTimeline.setCycleCount(Timeline.INDEFINITE);
        clockTimeline.play();
    }

    private void updateDateTimeLabel() {
        if (lblDateTime != null) {
            lblDateTime.setText(LocalDateTime.now().format(DATE_TIME_FORMATTER));
        }
    }

    private void navigateTo(ViewId viewId, String breadcrumb, String pageTitle, Button activeButton) {
        showLoadingOverlay(true);
        if (navigationDelay != null) {
            navigationDelay.stop();
        }

        navigationDelay = new PauseTransition(Duration.millis(80));
        navigationDelay.setOnFinished(event -> loadViewNow(viewId, breadcrumb, pageTitle, activeButton));
        navigationDelay.play();
    }

    private void loadViewNow(ViewId viewId, String breadcrumb, String pageTitle, Button activeButton) {
        try {
            Parent view = ViewLoaderUtil.load(viewId);
            contentArea.getChildren().setAll(view);
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);

            updatePageInfo(breadcrumb, pageTitle);
            setActiveNavButton(activeButton);
            animateContent(view);
        } catch (RuntimeException e) {
            AppLog.error(MainController.class, "Khong the tai giao dien " + viewId, e);
            Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Lỗi",
                    "Không thể tải giao diện: " + ExceptionMessageUtil.resolve(e, "Lỗi không xác định")));
        } finally {
            showLoadingOverlay(false);
        }
    }

    private void updatePageInfo(String breadcrumb, String pageTitle) {
        lblBreadcrumb.setText(breadcrumb);
        lblPageTitle.setText(pageTitle);
    }

    private void animateContent(Parent view) {
        AnimationUtil.playPageEnter(view);
    }

    private void showLoadingOverlay(boolean show) {
        if (loadingOverlay != null) {
            loadingOverlay.setManaged(show);
            loadingOverlay.setMouseTransparent(!show);
            if (show) {
                loadingOverlay.setOpacity(0);
                loadingOverlay.setVisible(true);
                FadeTransition fadeIn = new FadeTransition(Duration.millis(140), loadingOverlay);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                fadeIn.play();
            } else if (loadingOverlay.isVisible()) {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(160), loadingOverlay);
                fadeOut.setFromValue(loadingOverlay.getOpacity());
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(event -> loadingOverlay.setVisible(false));
                fadeOut.play();
            }
        }
    }

    private void updateThemeToggleText() {
        if (btnThemeToggle != null) {
            btnThemeToggle.setText(ThemeManager.getCurrentTheme() == ThemeManager.Theme.DARK
                    ? "Chế độ sáng"
                    : "Chế độ tối");
        }
        setupThemeToggleIcon();
    }

    private void applyRoleVisibility() {
        boolean admin = isAdmin();
        setVisibleManaged(grpSystem, admin);
        setVisibleManaged(grpImports, admin);
        setVisibleManaged(grpReports, admin);
        setVisibleManaged(menuReports, admin);

        setVisibleManaged(btnBooks, admin);
        setVisibleManaged(btnEmployees, admin);
        setVisibleManaged(btnSuppliers, admin);
        setVisibleManaged(btnPromotions, admin);

        if (btnCustomers != null) {
            btnCustomers.setText(admin ? "Khách hàng" : "Tra cứu khách hàng");
        }
    }

    private void setActiveNavButton(Button activeButton) {
        Button[] buttons = {
                btnDashboard, btnBooks, btnCustomers, btnEmployees, btnSuppliers, btnPromotions,
                btnPOS, btnInvoices, btnImports, btnStatistics, btnHelp
        };
        for (Button button : buttons) {
            if (button != null) {
                button.getStyleClass().remove(NAV_ACTIVE_CLASS);
                if (button == activeButton && !button.getStyleClass().contains(NAV_ACTIVE_CLASS)) {
                    button.getStyleClass().add(NAV_ACTIVE_CLASS);
                    AnimationUtil.pulse(button);
                }
            }
        }
    }

    private boolean ensureAdminAccess() {
        if (isAdmin()) {
            return true;
        }
        showAlert(Alert.AlertType.WARNING, "Không có quyền", "Bạn không có quyền truy cập chức năng quản trị này.");
        return false;
    }

    private boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }

    private void setVisibleManaged(Node node, boolean visible) {
        if (node != null) {
            node.setManaged(visible);
            node.setVisible(visible);
        }
    }

    private void setupChrome() {
        setupNavigationIcons();
        setupTooltips();
        setupThemeToggleIcon();
    }

    private void setupNavigationIcons() {
        IconUtil.apply(btnDashboard, "fas-chart-pie", "Xem tổng quan hệ thống", "nav-icon");
        IconUtil.apply(btnBooks, "fas-book-open", "Mở quản lý sách", "nav-icon");
        IconUtil.apply(btnCustomers, "fas-address-book", "Mở quản lý hoặc tra cứu khách hàng", "nav-icon");
        IconUtil.apply(btnEmployees, "fas-user-tie", "Mở quản lý nhân viên", "nav-icon");
        IconUtil.apply(btnSuppliers, "fas-truck", "Mở quản lý nhà cung cấp", "nav-icon");
        IconUtil.apply(btnPromotions, "fas-tags", "Mở trung tâm khuyến mãi", "nav-icon");
        IconUtil.apply(btnPOS, "fas-cash-register", "Mở quầy thanh toán", "nav-icon");
        IconUtil.apply(btnInvoices, "fas-receipt", "Mở danh sách hóa đơn", "nav-icon");
        IconUtil.apply(btnImports, "fas-box-open", "Mở quản lý phiếu nhập", "nav-icon");
        IconUtil.apply(btnStatistics, "fas-chart-line", "Mở các báo cáo thống kê", "nav-icon");
        IconUtil.apply(btnHelp, "fas-question-circle", "Xem thông tin giới thiệu", "nav-icon");
        IconUtil.apply(menuReports, "fas-chart-bar", "Mở nhanh các báo cáo", "app-icon");
    }

    private void setupTooltips() {
        TooltipUtil.install(btnThemeToggle, "Chuyển giữa chế độ sáng và tối");
    }

    private void setupThemeToggleIcon() {
        IconUtil.apply(btnThemeToggle,
                ThemeManager.getCurrentTheme() == ThemeManager.Theme.DARK ? "fas-sun" : "fas-moon",
                "Chuyển giao diện", "app-icon");
    }

    private VBox formBlock(String labelText, PasswordField field) {
        VBox box = new VBox(6);
        box.getChildren().addAll(label(labelText, "form-label"), field);
        return box;
    }

    private PasswordField passwordField(String promptText) {
        PasswordField field = new PasswordField();
        field.setPromptText(promptText);
        field.setPrefHeight(40);
        return field;
    }

    private Label label(String text, String styleClass) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        return label;
    }

    private String validatePasswordChange(String oldPassword, String newPassword, String confirmPassword) {
        if (oldPassword == null || oldPassword.isBlank()) {
            return "Vui lòng nhập mật khẩu hiện tại.";
        }
        if (newPassword == null || newPassword.isBlank()) {
            return "Vui lòng nhập mật khẩu mới.";
        }
        if (!ValidationUtil.isValidPassword(newPassword)) {
            return "Mật khẩu mới phải có ít nhất 6 ký tự.";
        }
        if (confirmPassword == null || confirmPassword.isBlank()) {
            return "Vui lòng xác nhận mật khẩu mới.";
        }
        if (!newPassword.equals(confirmPassword)) {
            return "Mật khẩu xác nhận không khớp.";
        }
        if (newPassword.equals(oldPassword)) {
            return "Mật khẩu mới phải khác mật khẩu hiện tại.";
        }
        return null;
    }
}

