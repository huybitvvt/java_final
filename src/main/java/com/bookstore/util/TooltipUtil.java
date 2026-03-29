package com.bookstore.util;

import javafx.scene.control.Control;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

/**
 * Tooltip helper with consistent timing.
 */
public final class TooltipUtil {

    private TooltipUtil() {
    }

    public static void install(Control control, String text) {
        if (control == null || text == null || text.isBlank()) {
            return;
        }

        Tooltip tooltip = new Tooltip(text);
        tooltip.setShowDelay(Duration.millis(180));
        tooltip.setHideDelay(Duration.millis(80));
        tooltip.setShowDuration(Duration.seconds(8));
        control.setTooltip(tooltip);
    }
}
