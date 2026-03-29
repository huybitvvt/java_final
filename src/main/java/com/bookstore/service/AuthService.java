package com.bookstore.service;

import com.bookstore.model.User;
import com.bookstore.session.SessionContext;

/**
 * Authentication facade used by UI controllers.
 */
public class AuthService {

    private final UserService userService;

    public AuthService() {
        this.userService = new UserService();
    }

    public User login(String username, String password) {
        User user = userService.login(username, password);
        if (user != null) {
            SessionContext.setCurrentUser(user);
        }
        return user;
    }

    public void logout() {
        userService.logout();
        SessionContext.clear();
    }

    public User getCurrentUser() {
        return SessionContext.getCurrentUser();
    }
}
