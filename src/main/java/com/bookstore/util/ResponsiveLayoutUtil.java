package com.bookstore.util;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;

/**
 * Applies a compact style class automatically for smaller laptop displays.
 */
public final class ResponsiveLayoutUtil {

    private static final String COMPACT_CLASS = "compact-ui";
    private static final double COMPACT_MAX_WIDTH = 1920;
    private static final double COMPACT_MAX_HEIGHT = 1100;

    private ResponsiveLayoutUtil() {
    }

    public static void install(Parent root) {
        if (root == null) {
            return;
        }

        applyCompactClass(root, isCompactScreen());

        root.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene == null) {
                return;
            }

            updateFromScene(root, newScene);
            ChangeListener<Number> resizeListener = (obs, oldValue, newValue) -> updateFromScene(root, newScene);
            newScene.widthProperty().addListener(resizeListener);
            newScene.heightProperty().addListener(resizeListener);
        });
    }

    public static boolean isCompactScreen() {
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        return shouldUseCompactLayout(visualBounds.getWidth(), visualBounds.getHeight());
    }

    private static void updateFromScene(Parent root, Scene scene) {
        double width = scene.getWidth();
        double height = scene.getHeight();

        if (width <= 0 || height <= 0) {
            Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
            width = visualBounds.getWidth();
            height = visualBounds.getHeight();
        }

        applyCompactClass(root, shouldUseCompactLayout(width, height));
    }

    private static boolean shouldUseCompactLayout(double width, double height) {
        return width > 0 && height > 0 && (width <= COMPACT_MAX_WIDTH || height <= COMPACT_MAX_HEIGHT);
    }

    private static void applyCompactClass(Parent root, boolean compact) {
        if (compact) {
            if (!root.getStyleClass().contains(COMPACT_CLASS)) {
                root.getStyleClass().add(COMPACT_CLASS);
            }
            return;
        }

        root.getStyleClass().remove(COMPACT_CLASS);
    }
}
