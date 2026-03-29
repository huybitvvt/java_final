package com.bookstore.view.generated;

import com.bookstore.controller.ImportController;
import com.bookstore.view.GeneratedViewSupport;
import com.bookstore.view.ViewBundle;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;

public final class ImportViewBuilder {
    private ImportViewBuilder() {
    }

    public static ViewBundle create() {
        ImportController controller = new ImportController();

        AnchorPane root = new AnchorPane();
        VBox pageRoot = new VBox();
        GeneratedViewSupport.setProperty(pageRoot, "spacing", "18.0");
        GeneratedViewSupport.addStyleClasses(pageRoot, "page-root");
        GeneratedViewSupport.applyConstraint(pageRoot, "AnchorPane.bottomAnchor", "0.0");
        GeneratedViewSupport.applyConstraint(pageRoot, "AnchorPane.leftAnchor", "0.0");
        GeneratedViewSupport.applyConstraint(pageRoot, "AnchorPane.rightAnchor", "0.0");
        GeneratedViewSupport.applyConstraint(pageRoot, "AnchorPane.topAnchor", "0.0");
        GeneratedViewSupport.bind(controller, "pageRoot", pageRoot);
        HBox headerBar = new HBox();
        GeneratedViewSupport.setProperty(headerBar, "alignment", "CENTER_LEFT");
        GeneratedViewSupport.addStyleClasses(headerBar, "page-header");
        GeneratedViewSupport.bind(controller, "headerBar", headerBar);
        VBox vBox = new VBox();
        GeneratedViewSupport.setProperty(vBox, "spacing", "4.0");
        Label label = new Label();
        GeneratedViewSupport.addStyleClasses(label, "section-title");
        GeneratedViewSupport.setProperty(label, "text", "Phiếu nhập");
        GeneratedViewSupport.addChild(vBox, label);
        Label label2 = new Label();
        GeneratedViewSupport.addStyleClasses(label2, "section-subtitle");
        GeneratedViewSupport.setProperty(label2, "text", "Theo dõi lịch sử nhập hàng, nhà cung cấp và tổng giá trị từng phiếu nhập.");
        GeneratedViewSupport.setProperty(label2, "wrapText", "true");
        GeneratedViewSupport.addChild(vBox, label2);
        GeneratedViewSupport.addChild(headerBar, vBox);
        Region region = new Region();
        GeneratedViewSupport.applyConstraint(region, "HBox.hgrow", "ALWAYS");
        GeneratedViewSupport.addChild(headerBar, region);
        Label label3 = new Label();
        GeneratedViewSupport.addStyleClasses(label3, "dashboard-badge");
        GeneratedViewSupport.setProperty(label3, "text", "Nhập hàng");
        GeneratedViewSupport.addChild(headerBar, label3);
        GeneratedViewSupport.addChild(pageRoot, headerBar);
        VBox contentCard = new VBox();
        GeneratedViewSupport.setProperty(contentCard, "spacing", "16.0");
        GeneratedViewSupport.addStyleClasses(contentCard, "chart-panel");
        GeneratedViewSupport.applyConstraint(contentCard, "VBox.vgrow", "ALWAYS");
        GeneratedViewSupport.bind(controller, "contentCard", contentCard);
        HBox hBox = new HBox();
        GeneratedViewSupport.setProperty(hBox, "alignment", "CENTER_LEFT");
        GeneratedViewSupport.setProperty(hBox, "spacing", "10.0");
        TextField txtSearch = new TextField();
        GeneratedViewSupport.setProperty(txtSearch, "prefWidth", "320.0");
        GeneratedViewSupport.setProperty(txtSearch, "promptText", "Tìm kiếm đơn nhập...");
        GeneratedViewSupport.bind(controller, "txtSearch", txtSearch);
        GeneratedViewSupport.addChild(hBox, txtSearch);
        Button btnAdd = new Button();
        GeneratedViewSupport.setProperty(btnAdd, "mnemonicParsing", "false");
        GeneratedViewSupport.bindAction(btnAdd, controller, "handleAdd");
        GeneratedViewSupport.addStyleClasses(btnAdd, "primary-button");
        GeneratedViewSupport.setProperty(btnAdd, "text", "+ Nhập hàng");
        GeneratedViewSupport.bind(controller, "btnAdd", btnAdd);
        GeneratedViewSupport.addChild(hBox, btnAdd);
        GeneratedViewSupport.addChild(contentCard, hBox);
        TableView<?> tableImports = new TableView<>();
        GeneratedViewSupport.applyConstraint(tableImports, "VBox.vgrow", "ALWAYS");
        GeneratedViewSupport.bind(controller, "tableImports", tableImports);
        TableColumn<?, ?> colMaPN = new TableColumn<>();
        GeneratedViewSupport.setProperty(colMaPN, "minWidth", "110.0");
        GeneratedViewSupport.setProperty(colMaPN, "text", "Mã PN");
        GeneratedViewSupport.bind(controller, "colMaPN", colMaPN);
        GeneratedViewSupport.addColumn(tableImports, colMaPN);
        TableColumn<?, ?> colNgayNhap = new TableColumn<>();
        GeneratedViewSupport.setProperty(colNgayNhap, "minWidth", "170.0");
        GeneratedViewSupport.setProperty(colNgayNhap, "text", "Ngày nhập");
        GeneratedViewSupport.bind(controller, "colNgayNhap", colNgayNhap);
        GeneratedViewSupport.addColumn(tableImports, colNgayNhap);
        TableColumn<?, ?> colNCC = new TableColumn<>();
        GeneratedViewSupport.setProperty(colNCC, "minWidth", "220.0");
        GeneratedViewSupport.setProperty(colNCC, "text", "Nhà cung cấp");
        GeneratedViewSupport.bind(controller, "colNCC", colNCC);
        GeneratedViewSupport.addColumn(tableImports, colNCC);
        TableColumn<?, ?> colNhanVien = new TableColumn<>();
        GeneratedViewSupport.setProperty(colNhanVien, "minWidth", "120.0");
        GeneratedViewSupport.setProperty(colNhanVien, "text", "Nhân viên");
        GeneratedViewSupport.bind(controller, "colNhanVien", colNhanVien);
        GeneratedViewSupport.addColumn(tableImports, colNhanVien);
        TableColumn<?, ?> colTongTien = new TableColumn<>();
        GeneratedViewSupport.setProperty(colTongTien, "minWidth", "160.0");
        GeneratedViewSupport.setProperty(colTongTien, "text", "Tổng tiền");
        GeneratedViewSupport.bind(controller, "colTongTien", colTongTien);
        GeneratedViewSupport.addColumn(tableImports, colTongTien);
        TableColumn<?, ?> colTrangThai = new TableColumn<>();
        GeneratedViewSupport.setProperty(colTrangThai, "minWidth", "120.0");
        GeneratedViewSupport.setProperty(colTrangThai, "text", "Trạng thái");
        GeneratedViewSupport.bind(controller, "colTrangThai", colTrangThai);
        GeneratedViewSupport.addColumn(tableImports, colTrangThai);
        GeneratedViewSupport.addChild(contentCard, tableImports);
        GeneratedViewSupport.addChild(pageRoot, contentCard);
        GeneratedViewSupport.addChild(root, pageRoot);

        GeneratedViewSupport.initialize(controller);
        return new ViewBundle(root, controller);
    }
}
