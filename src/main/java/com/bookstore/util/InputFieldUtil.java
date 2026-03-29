package com.bookstore.util;

import javafx.animation.PauseTransition;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.util.Duration;

/**
 * Utility cho các trường nhập liệu và tìm kiếm.
 */
public final class InputFieldUtil {

    private InputFieldUtil() {
    }

    public static void bindKeywordSearch(TextField textField, Runnable searchAction) {
        PauseTransition pause = new PauseTransition(Duration.millis(250));
        pause.setOnFinished(event -> searchAction.run());
        textField.textProperty().addListener((obs, oldText, newText) -> pause.playFromStart());
        textField.setOnAction(event -> searchAction.run());
    }

    /**
     * Workaround cho lỗi IME trên macOS khi text của ô trước bị dính sang ô hiện tại.
     */
    public static void installCarryOverWorkaround(TextInputControl... controls) {
        final String[] previousFieldValue = {""};
        final long[] previousFieldUpdatedAt = {0L};

        for (TextInputControl control : controls) {
            final boolean[] adjusting = {false};
            final String[] carryOverSnapshot = {""};
            final boolean[] emptyWhenFocused = {true};

            control.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
                if (isFocused) {
                    carryOverSnapshot[0] = previousFieldValue[0];
                    emptyWhenFocused[0] = control.getText() == null || control.getText().isEmpty();
                } else {
                    previousFieldValue[0] = control.getText() == null ? "" : control.getText();
                    previousFieldUpdatedAt[0] = System.currentTimeMillis();
                    carryOverSnapshot[0] = "";
                }
            });

            control.textProperty().addListener((obs, oldText, newText) -> {
                if (adjusting[0] || !control.isFocused()) {
                    return;
                }

                String previous = carryOverSnapshot[0];
                long carryAgeMs = System.currentTimeMillis() - previousFieldUpdatedAt[0];
                if (previous == null || previous.isBlank() || carryAgeMs > 4000 || !emptyWhenFocused[0]) {
                    if (newText != null && !newText.isEmpty()) {
                        emptyWhenFocused[0] = false;
                    }
                    return;
                }

                String sanitized = null;
                if (newText != null && newText.equals(previous)) {
                    sanitized = "";
                } else if (newText != null
                        && newText.length() > previous.length()
                        && newText.startsWith(previous)) {
                    sanitized = newText.substring(previous.length());
                }

                if (sanitized != null) {
                    adjusting[0] = true;
                    control.setText(sanitized);
                    control.positionCaret(sanitized.length());
                    adjusting[0] = false;
                    emptyWhenFocused[0] = sanitized.isEmpty();
                    return;
                }

                if (control.isFocused()
                        && oldText.isEmpty()
                        && newText != null
                        && !newText.isEmpty()) {
                    emptyWhenFocused[0] = false;
                }
            });
        }
    }
}
