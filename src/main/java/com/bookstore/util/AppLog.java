package com.bookstore.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lightweight logging facade used across the app to avoid direct stdout/stderr usage.
 */
public final class AppLog {

    private AppLog() {
    }

    public static void info(Class<?> source, String message) {
        logger(source).info(message);
    }

    public static void warn(Class<?> source, String message) {
        logger(source).warn(message);
    }

    public static void warn(Class<?> source, String message, Throwable throwable) {
        logger(source).warn(message, throwable);
    }

    public static void error(Class<?> source, String message) {
        logger(source).error(message);
    }

    public static void error(Class<?> source, String message, Throwable throwable) {
        logger(source).error(message, throwable);
    }

    private static Logger logger(Class<?> source) {
        return LoggerFactory.getLogger(source);
    }
}
