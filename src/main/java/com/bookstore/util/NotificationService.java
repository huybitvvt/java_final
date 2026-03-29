package com.bookstore.util;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.stage.Window;
import org.controlsfx.control.Notifications;

/**
 * Toast notifications powered by ControlsFX.
 */
public final class NotificationService {

    private NotificationService() {
    }

    public static void showSuccess(Node owner, String message) {
        show(owner, message, "toast-success");
    }

    public static void showInfo(Node owner, String message) {
        show(owner, message, "toast-info");
    }

    public static void showWarning(Node owner, String message) {
        show(owner, message, "toast-warning");
    }

    public static void showError(Node owner, String message) {
        show(owner, message, "toast-error");
    }

    private static void show(Node owner, String message, String styleClass) {
        if (owner == null || owner.getScene() == null || owner.getScene().getWindow() == null) {
            return;
        }

        Window window = owner.getScene().getWindow();
        Notifications notifications = Notifications.create()
                .owner(window)
                .position(Pos.BOTTOM_RIGHT)
                .hideAfter(javafx.util.Duration.seconds(2.6))
                .title(resolveTitle(styleClass))
                .text(message);

        if (ThemeManager.getCurrentTheme() == ThemeManager.Theme.DARK) {
            notifications = notifications.darkStyle();
        }

        switch (styleClass) {
            case "toast-success" -> notifications.showInformation();
            case "toast-warning" -> notifications.showWarning();
            case "toast-error" -> notifications.showError();
            default -> notifications.showInformation();
        }
    }

    private static String resolveTitle(String styleClass) {
        return switch (styleClass) {
            case "toast-success" -> "Thành công";
            case "toast-warning" -> "Cảnh báo";
            case "toast-error" -> "Lỗi";
            default -> "Thông báo";
        };
    }
}
