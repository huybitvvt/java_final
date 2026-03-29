package com.bookstore.util;

import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Labeled;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * Shared icon helper for buttons, labels and menus.
 */
public final class IconUtil {

    private IconUtil() {
    }

    public static void apply(Labeled control, String iconLiteral, String tooltipText) {
        apply(control, iconLiteral, tooltipText, "app-icon");
    }

    public static void apply(Labeled control, String iconLiteral, String tooltipText, String styleClass) {
        if (control == null) {
            return;
        }

        Node graphic = create(iconLiteral, styleClass);
        control.setGraphic(graphic);
        control.setGraphicTextGap(graphic == null ? 0 : 10);
        if (control instanceof Control) {
            TooltipUtil.install((Control) control, tooltipText);
        }
    }

    public static Node create(String iconLiteral) {
        return create(iconLiteral, "app-icon");
    }

    public static Node create(String iconLiteral, String styleClass) {
        try {
            FontIcon icon = new FontIcon(iconLiteral);
            icon.getStyleClass().add(styleClass);
            return icon;
        } catch (RuntimeException exception) {
            AppLog.warn(IconUtil.class, "Unsupported icon literal: " + iconLiteral, exception);
            return null;
        }
    }
}
