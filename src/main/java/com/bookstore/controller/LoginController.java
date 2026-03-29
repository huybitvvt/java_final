package com.bookstore.controller;

import com.bookstore.model.User;
import com.bookstore.session.SessionContext;
import com.bookstore.util.IconUtil;
import com.bookstore.util.InputFieldUtil;
import com.bookstore.service.UserService;
import com.bookstore.util.AppLog;
import com.bookstore.util.ExceptionMessageUtil;
import com.bookstore.util.ThemeManager;
import com.bookstore.util.ViewLoaderUtil;
import com.bookstore.view.ViewBundle;
import com.bookstore.view.ViewId;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Login Controller - Xử lý đăng nhập
 */
public class LoginController {
    private TextField txtUsername;
    private PasswordField txtPassword;
    private Label lblError;
    private Button btnLogin;

    private UserService userService;

    public LoginController() {
        this.userService = new UserService();
    }
    public void initialize() {
        lblError.managedProperty().bind(lblError.visibleProperty());
        clearError();
        txtUsername.textProperty().addListener((obs, oldValue, newValue) -> clearError());
        txtPassword.textProperty().addListener((obs, oldValue, newValue) -> clearError());
        InputFieldUtil.installCarryOverWorkaround(txtUsername, txtPassword);
        IconUtil.apply(btnLogin, "fas-user-tie", "Đăng nhập vào hệ thống");
    }

    /**
     * Xử lý đăng nhập
     */
    private void handleLogin() {
        clearError();
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        // Validation
        if (username.isEmpty()) {
            showError("Vui lòng nhập tên đăng nhập!");
            return;
        }

        if (password.isEmpty()) {
            showError("Vui lòng nhập mật khẩu!");
            return;
        }

        try {
            User user = userService.login(username, password);

            if (user != null) {
                SessionContext.setCurrentUser(user);
                loadMainScreen(user);
            } else {
                showError("Sai tài khoản hoặc mật khẩu.");
                txtPassword.clear();
                txtPassword.requestFocus();
            }
        } catch (RuntimeException e) {
            AppLog.warn(LoginController.class, "Dang nhap that bai: " + ExceptionMessageUtil.resolve(e, "Lỗi không xác định"), e);
            showError(ExceptionMessageUtil.resolve(e, "Không thể đăng nhập. Vui lòng thử lại."));
        }
    }

    /**
     * Hiển thị thông báo lỗi
     */
    private void showError(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
    }

    private void clearError() {
        lblError.setText("");
        lblError.setVisible(false);
    }

    /**
     * Load màn hình chính
     */
    private void loadMainScreen(User user) {
        try {
            ViewBundle view = ViewLoaderUtil.loadView(ViewId.MAIN);
            Parent root = view.getRoot();

            // Get main controller and set current user
            MainController mainController = view.getController(MainController.class);
            mainController.setCurrentUser(user);

            Stage stage = (Stage) txtUsername.getScene().getWindow();
            Scene scene = new Scene(root);
            ThemeManager.applyTheme(scene);

            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

        } catch (RuntimeException e) {
            AppLog.error(LoginController.class, "Khong the tai man hinh chinh", e);
            showAlert(Alert.AlertType.ERROR, "Lỗi",
                    "Không thể tải màn hình chính: " + ExceptionMessageUtil.resolve(e, "Lỗi không xác định"));
        }
    }

    /**
     * Hiển thị thông báo Alert
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

