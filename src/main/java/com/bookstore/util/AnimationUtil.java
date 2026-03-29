package com.bookstore.util;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

import java.util.List;

/**
 * Reusable motion helpers.
 */
public final class AnimationUtil {

    private AnimationUtil() {
    }

    public static void playPageEnter(Node node) {
        if (node == null) {
            return;
        }

        node.setOpacity(0);
        node.setTranslateY(14);

        FadeTransition fade = new FadeTransition(Duration.millis(220), node);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(220), node);
        slide.setFromY(14);
        slide.setToY(0);

        new ParallelTransition(fade, slide).play();
    }

    public static void playStagger(List<? extends Node> nodes) {
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            if (node == null) {
                continue;
            }
            node.setOpacity(0);
            node.setTranslateY(12);

            FadeTransition fade = new FadeTransition(Duration.millis(240), node);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.setDelay(Duration.millis(i * 60L));

            TranslateTransition slide = new TranslateTransition(Duration.millis(240), node);
            slide.setFromY(12);
            slide.setToY(0);
            slide.setDelay(Duration.millis(i * 60L));

            new ParallelTransition(fade, slide).play();
        }
    }

    public static void pulse(Node node) {
        if (node == null) {
            return;
        }
        ScaleTransition scale = new ScaleTransition(Duration.millis(140), node);
        scale.setFromX(1);
        scale.setFromY(1);
        scale.setToX(1.03);
        scale.setToY(1.03);
        scale.setCycleCount(2);
        scale.setAutoReverse(true);
        scale.play();
    }
}
