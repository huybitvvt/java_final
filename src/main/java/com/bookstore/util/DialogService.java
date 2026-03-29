package com.bookstore.util;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.Optional;

/**
 * Styled dialog helpers.
 */
public final class DialogService {

    private DialogService() {
    }

    public static void showInfo(Node owner, String title, String message) {
        showAlert(owner, Alert.AlertType.INFORMATION, title, message);
    }

    public static void showWarning(Node owner, String title, String message) {
        showAlert(owner, Alert.AlertType.WARNING, title, message);
    }

    public static void showError(Node owner, String title, String message) {
        showAlert(owner, Alert.AlertType.ERROR, title, message);
    }

    public static boolean confirm(Node owner, String title, String message, String confirmText) {
        ButtonType cancelType = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType confirmType = new ButtonType(confirmText, ButtonBar.ButtonData.OK_DONE);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getButtonTypes().setAll(cancelType, confirmType);
        attachToOwner(alert, owner);
        applyDialogTheme(owner, alert.getDialogPane());
        styleButton(alert.getDialogPane(), cancelType, "chip-button");
        styleButton(alert.getDialogPane(), confirmType,
                "Xóa".equalsIgnoreCase(confirmText) ? "danger-soft-button" : "primary-button");
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE;
    }

    private static void showAlert(Node owner, Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        attachToOwner(alert, owner);
        applyDialogTheme(owner, alert.getDialogPane());
        ButtonType okType = ButtonType.OK;
        styleButton(alert.getDialogPane(), okType, switch (type) {
            case ERROR -> "danger-soft-button";
            case WARNING -> "chip-button";
            default -> "primary-button";
        });
        alert.showAndWait();
    }

    private static void applyDialogTheme(Node owner, DialogPane dialogPane) {
        dialogPane.getStyleClass().add("modern-dialog");
        if (owner != null && owner.getScene() != null) {
            dialogPane.getStylesheets().setAll(owner.getScene().getStylesheets());
        }
    }

    public static void attachToOwner(Dialog<?> dialog, Node owner) {
        Window ownerWindow = resolveOwnerWindow(owner);
        if (dialog != null && ownerWindow != null) {
            dialog.initOwner(ownerWindow);
            dialog.initModality(Modality.WINDOW_MODAL);
        }
    }

    public static void attachToOwner(Stage stage, Node owner) {
        Window ownerWindow = resolveOwnerWindow(owner);
        if (stage != null && ownerWindow != null) {
            stage.initOwner(ownerWindow);
            stage.initModality(Modality.WINDOW_MODAL);
        }
    }

    private static Window resolveOwnerWindow(Node owner) {
        if (owner == null || owner.getScene() == null) {
            return null;
        }
        return owner.getScene().getWindow();
    }

    private static void styleButton(DialogPane dialogPane, ButtonType buttonType, String styleClass) {
        if (dialogPane.lookupButton(buttonType) instanceof Button button) {
            button.getStyleClass().add(styleClass);
        }
    }
}
