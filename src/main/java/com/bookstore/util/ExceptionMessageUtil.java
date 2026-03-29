package com.bookstore.util;

/**
 * Resolves concise user-facing messages from exceptions.
 */
public final class ExceptionMessageUtil {

    private ExceptionMessageUtil() {
    }

    public static String resolve(Throwable throwable, String fallback) {
        if (throwable == null) {
            return fallback;
        }

        String message = firstMeaningfulMessage(throwable);
        return message == null || message.isBlank() ? fallback : message;
    }

    private static String firstMeaningfulMessage(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            String message = current.getMessage();
            if (message != null && !message.isBlank()) {
                return message;
            }
            current = current.getCause();
        }
        return null;
    }
}
