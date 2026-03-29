package com.bookstore.view;

import com.bookstore.controller.LoginController;
import com.bookstore.util.ResponsiveLayoutUtil;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.InputStream;

/**
 * Handcrafted JavaFX login view used instead of generated builder code.
 */
public final class LoginView {

    private LoginView() {
    }

    public static ViewBundle create() {
        LoginController controller = new LoginController();

        AnchorPane root = new AnchorPane();
        root.setPrefSize(1280, 760);
        root.getStyleClass().add("login-root");

        Region orbPrimary = orb("login-orb-primary", 280, 280);
        AnchorPane.setLeftAnchor(orbPrimary, 64.0);
        AnchorPane.setTopAnchor(orbPrimary, 54.0);

        Region orbSecondary = orb("login-orb-secondary", 360, 360);
        AnchorPane.setRightAnchor(orbSecondary, 78.0);
        AnchorPane.setBottomAnchor(orbSecondary, 42.0);

        HBox shell = new HBox(28);
        shell.setAlignment(Pos.CENTER);
        shell.getStyleClass().add("login-shell");
        AnchorPane.setTopAnchor(shell, 24.0);
        AnchorPane.setRightAnchor(shell, 24.0);
        AnchorPane.setBottomAnchor(shell, 24.0);
        AnchorPane.setLeftAnchor(shell, 24.0);

        VBox brandPanel = new VBox(22);
        brandPanel.setFillWidth(true);
        brandPanel.setMaxWidth(560);
        brandPanel.setPrefWidth(520);
        brandPanel.getStyleClass().add("login-brand-panel");
        HBox.setHgrow(brandPanel, Priority.ALWAYS);
        StackPane logoCard = logoCard();
        if (logoCard != null) {
            brandPanel.getChildren().add(logoCard);
        }
        brandPanel.getChildren().addAll(
                styledLabel("Hệ thống vận hành", "login-badge"),
                wrappingLabel("Quản lý cửa hàng sách", "login-title"),
                wrappingLabel(
                        "Bán hàng, tồn kho, khách hàng, khuyến mãi và báo cáo được gom vào một nhịp làm việc rõ ràng, dễ nhìn và đủ nhanh cho vận hành hằng ngày.",
                        "login-subtitle"
                ),
                statRow()
        );
        Region brandSpacer = new Region();
        VBox.setVgrow(brandSpacer, Priority.ALWAYS);
        brandPanel.getChildren().addAll(brandSpacer, teamCard());

        VBox loginCard = new VBox(18);
        loginCard.setMaxWidth(440);
        loginCard.setPrefWidth(430);
        loginCard.getStyleClass().add("login-card");
        loginCard.getChildren().addAll(
                styledLabel("ĐĂNG NHẬP HỆ THỐNG", "login-form-kicker"),
                styledLabel("Chào mừng trở lại", "login-form-title")
        );

        VBox formSurface = new VBox(14);
        formSurface.getStyleClass().add("login-form-surface");

        TextField txtUsername = new TextField();
        txtUsername.setPromptText("Nhập tên đăng nhập");
        GeneratedViewSupport.bind(controller, "txtUsername", txtUsername);

        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Nhập mật khẩu");
        GeneratedViewSupport.bind(controller, "txtPassword", txtPassword);

        Label lblError = wrappingLabel("", "error-label");
        lblError.setVisible(false);
        GeneratedViewSupport.bind(controller, "lblError", lblError);

        formSurface.getChildren().addAll(
                inputCard("Tên đăng nhập", txtUsername),
                inputCard("Mật khẩu", txtPassword),
                lblError
        );

        Button btnLogin = new Button("Đăng nhập");
        btnLogin.setDefaultButton(true);
        btnLogin.setPrefHeight(46);
        btnLogin.setMaxWidth(Double.MAX_VALUE);
        btnLogin.getStyleClass().add("primary-button");
        GeneratedViewSupport.bindAction(btnLogin, controller, "handleLogin");
        GeneratedViewSupport.bind(controller, "btnLogin", btnLogin);

        loginCard.getChildren().addAll(formSurface, btnLogin);
        shell.getChildren().addAll(brandPanel, loginCard);
        root.getChildren().addAll(orbPrimary, orbSecondary, shell);

        ResponsiveLayoutUtil.install(root);
        GeneratedViewSupport.initialize(controller);
        return new ViewBundle(root, controller);
    }

    private static HBox statRow() {
        HBox row = new HBox(12);
        row.getChildren().addAll(
                statCard("24/7", "Theo dõi doanh thu, hóa đơn và trạng thái bán hàng theo thời gian thực.", "login-stat-card-primary"),
                statCard("POS", "Voucher, loyalty và giỏ hàng được xử lý ngay trong cùng luồng bán.", "login-stat-card-secondary"),
                statCard("1 chạm", "Mở nhanh danh mục, thống kê và lịch sử giao dịch khi cần đối soát.", "login-stat-card-accent")
        );
        return row;
    }

    private static VBox statCard(String value, String description, String styleClass) {
        VBox card = new VBox(6);
        card.getStyleClass().add(styleClass);
        HBox.setHgrow(card, Priority.ALWAYS);
        card.getChildren().addAll(
                styledLabel(value, "login-stat-value"),
                wrappingLabel(description, "login-stat-label")
        );
        return card;
    }

    private static VBox inputCard(String title, TextField field) {
        VBox card = new VBox(8);
        card.getStyleClass().add("login-input-card");
        card.getChildren().addAll(styledLabel(title, "form-label"), field);
        return card;
    }

    private static VBox teamCard() {
        VBox card = new VBox(8);
        card.getStyleClass().add("login-team-card");
        card.getChildren().addAll(
                styledLabel("Nhóm 2k6", "login-team-title"),
                memberLabel("Nguyễn Doãn Huy"),
                memberLabel("Nguyễn Trần Tuấn Khôi"),
                memberLabel("Đỗ Văn Đạt"),
                memberLabel("Đỗ Đặng Việt Hoàng"),
                memberLabel("Trần Nguyễn Lam Huy")
        );
        return card;
    }

    private static Label memberLabel(String name) {
        return styledLabel("• " + name, "login-team-member");
    }

    private static Label styledLabel(String text, String styleClass) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        return label;
    }

    private static Label wrappingLabel(String text, String styleClass) {
        Label label = styledLabel(text, styleClass);
        label.setWrapText(true);
        return label;
    }

    private static Region orb(String styleClass, double width, double height) {
        Region region = new Region();
        region.setMouseTransparent(true);
        region.setPrefSize(width, height);
        region.getStyleClass().add(styleClass);
        return region;
    }

    private static StackPane logoCard() {
        InputStream input = LoginView.class.getResourceAsStream("/com/bookstore/images/OIP.jpg");
        if (input == null) {
            return null;
        }

        ImageView imageView = new ImageView(new Image(input));
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(132);
        imageView.setSmooth(true);
        imageView.getStyleClass().add("login-logo-image");

        StackPane logoCard = new StackPane(imageView);
        logoCard.setPrefSize(156, 156);
        logoCard.setMaxSize(156, 156);
        logoCard.getStyleClass().add("login-logo-card");
        return logoCard;
    }
}
