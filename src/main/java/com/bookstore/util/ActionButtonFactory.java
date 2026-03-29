package com.bookstore.util;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

/**
 * Shared action buttons for table cells.
 */
public final class ActionButtonFactory {

    private ActionButtonFactory() {
    }

    public static Button createEditButton(EventHandler<ActionEvent> handler) {
        return create("Sửa", "fas-pen", "chip-button", "Chỉnh sửa bản ghi", handler);
    }

    public static Button createDeleteButton(EventHandler<ActionEvent> handler) {
        return create("Xóa", "fas-trash", "danger-soft-button", "Xóa bản ghi này", handler);
    }

    public static Button createViewButton(EventHandler<ActionEvent> handler) {
        return create("Xem", "fas-eye", "chip-button", "Xem chi tiết", handler);
    }

    public static HBox createGroup(Button... buttons) {
        HBox group = new HBox(6);
        group.getStyleClass().add("table-action-group");
        group.getChildren().addAll(buttons);
        return group;
    }

    private static Button create(String text, String iconLiteral, String styleClass, String tooltipText,
                                 EventHandler<ActionEvent> handler) {
        Button button = new Button(text);
        button.getStyleClass().addAll("table-action-button", styleClass);
        IconUtil.apply(button, iconLiteral, tooltipText, "action-icon");
        if (ResponsiveLayoutUtil.isCompactScreen()) {
            button.getStyleClass().add("table-action-button-compact");
            button.setText("");
            button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            button.setGraphicTextGap(0);
            button.setMinWidth(36);
            button.setPrefWidth(36);
        }
        button.setOnAction(handler);
        return button;
    }
}
