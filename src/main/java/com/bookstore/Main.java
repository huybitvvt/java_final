package com.bookstore;

import com.bookstore.util.ThemeManager;
import com.bookstore.util.ViewLoaderUtil;
import com.bookstore.view.ViewId;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main Application Class - JavaFX Entry Point
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = ViewLoaderUtil.load(ViewId.LOGIN);

        Scene scene = new Scene(root);
        ThemeManager.applyTheme(scene);

        primaryStage.setTitle("Quản Lý Cửa Hàng Sách");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
