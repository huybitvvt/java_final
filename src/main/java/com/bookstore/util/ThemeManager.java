package com.bookstore.util;

import javafx.collections.ObservableList;
import javafx.scene.Scene;

import java.util.prefs.Preferences;

/**
 * Theme manager for light/dark mode switching.
 */
public final class ThemeManager {

    public enum Theme {
        LIGHT,
        DARK
    }

    private static final String BASE_THEME = ThemeManager.class.getResource("/com/bookstore/css/base.css").toExternalForm();
    private static final String LIGHT_THEME = ThemeManager.class.getResource("/com/bookstore/css/light.css").toExternalForm();
    private static final String DARK_THEME = ThemeManager.class.getResource("/com/bookstore/css/dark.css").toExternalForm();
    private static final Preferences PREFERENCES = Preferences.userNodeForPackage(ThemeManager.class);
    private static final String THEME_KEY = "theme";

    private static Theme currentTheme = Theme.valueOf(PREFERENCES.get(THEME_KEY, Theme.LIGHT.name()));

    private ThemeManager() {
    }

    public static void applyTheme(Scene scene) {
        applyTheme(scene, currentTheme);
    }

    public static void toggleTheme(Scene scene) {
        currentTheme = currentTheme == Theme.DARK ? Theme.LIGHT : Theme.DARK;
        PREFERENCES.put(THEME_KEY, currentTheme.name());
        applyTheme(scene, currentTheme);
    }

    public static Theme getCurrentTheme() {
        return currentTheme;
    }

    private static void applyTheme(Scene scene, Theme theme) {
        if (scene == null) {
            return;
        }

        ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.removeIf(stylesheet ->
                stylesheet.endsWith("/style.css")
                        || stylesheet.endsWith("/base.css")
                        || stylesheet.endsWith("/light.css")
                        || stylesheet.endsWith("/dark.css"));

        stylesheets.add(BASE_THEME);
        stylesheets.add(theme == Theme.DARK ? DARK_THEME : LIGHT_THEME);
    }
}
