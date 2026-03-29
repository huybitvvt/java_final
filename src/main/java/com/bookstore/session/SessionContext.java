package com.bookstore.session;

import com.bookstore.model.User;

/**
 * Holds runtime session state for the logged-in user.
 */
public final class SessionContext {

    private static User currentUser;

    private SessionContext() {
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }

    public static void clear() {
        currentUser = null;
    }
}
