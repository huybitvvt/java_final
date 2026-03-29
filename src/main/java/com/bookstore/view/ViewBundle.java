package com.bookstore.view;

import javafx.scene.Parent;

public final class ViewBundle {

    private final Parent root;
    private final Object controller;

    public ViewBundle(Parent root, Object controller) {
        this.root = root;
        this.controller = controller;
    }

    public Parent getRoot() {
        return root;
    }

    public Object getController() {
        return controller;
    }

    public <T> T getController(Class<T> type) {
        if (controller == null) {
            throw new IllegalStateException("View does not declare a controller.");
        }
        return type.cast(controller);
    }
}
