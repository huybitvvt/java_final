package com.bookstore.view.generated;

import com.bookstore.controller.InventoryController;
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

public final class InventoryViewBuilder {
    private InventoryViewBuilder() {
    }

    public static ViewBundle create() {
        InventoryController controller = new InventoryController();

        VBox root = new VBox();
        GeneratedViewSupport.setProperty(root, "spacing", "18.0");
        GeneratedViewSupport.addStyleClasses(root, "page-root");
        HBox hBox = new HBox();
        GeneratedViewSupport.setProperty(hBox, "alignment", "CENTER_LEFT");
        GeneratedViewSupport.addStyleClasses(hBox, "page-header");
        VBox vBox = new VBox();
        GeneratedViewSupport.setProperty(vBox, "spacing", "4.0");
        Label label = new Label();
        GeneratedViewSupport.addStyleClasses(label, "section-title");
        GeneratedViewSupport.setProperty(label, "text", "Thống kê tồn kho");
        GeneratedViewSupport.addChild(vBox, label);
        Label label2 = new Label();
        GeneratedViewSupport.addStyleClasses(label2, "section-subtitle");
        GeneratedViewSupport.setProperty(label2, "text", "Theo dõi tồn kho hiện tại và giá trị tồn của từng đầu sách để chủ động nhập hàng.");
        GeneratedViewSupport.setProperty(label2, "wrapText", "true");
        GeneratedViewSupport.addChild(vBox, label2);
        GeneratedViewSupport.addChild(hBox, vBox);
        Region region = new Region();
        GeneratedViewSupport.applyConstraint(region, "HBox.hgrow", "ALWAYS");
        GeneratedViewSupport.addChild(hBox, region);
        Label label3 = new Label();
        GeneratedViewSupport.addStyleClasses(label3, "dashboard-badge");
        GeneratedViewSupport.setProperty(label3, "text", "Kho vận");
        GeneratedViewSupport.addChild(hBox, label3);
        GeneratedViewSupport.addChild(root, hBox);
        VBox vBox2 = new VBox();
        GeneratedViewSupport.setProperty(vBox2, "spacing", "12.0");
        GeneratedViewSupport.addStyleClasses(vBox2, "chart-panel");
        GeneratedViewSupport.applyConstraint(vBox2, "VBox.vgrow", "ALWAYS");
        Label label4 = new Label();
        GeneratedViewSupport.addStyleClasses(label4, "chart-title");
        GeneratedViewSupport.setProperty(label4, "text", "Danh mục tồn kho hiện tại");
        GeneratedViewSupport.addChild(vBox2, label4);
        Label label5 = new Label();
        GeneratedViewSupport.addStyleClasses(label5, "chart-subtitle");
        GeneratedViewSupport.setProperty(label5, "text", "Bảng tổng hợp số lượng còn lại và giá trị tồn kho của từng đầu sách.");
        GeneratedViewSupport.setProperty(label5, "wrapText", "true");
        GeneratedViewSupport.addChild(vBox2, label5);
        TableView<?> tableInventory = new TableView<>();
        GeneratedViewSupport.applyConstraint(tableInventory, "VBox.vgrow", "ALWAYS");
        GeneratedViewSupport.bind(controller, "tableInventory", tableInventory);
        TableColumn<?, ?> colMaSach = new TableColumn<>();
        GeneratedViewSupport.setProperty(colMaSach, "text", "Mã");
        GeneratedViewSupport.bind(controller, "colMaSach", colMaSach);
        GeneratedViewSupport.addColumn(tableInventory, colMaSach);
        TableColumn<?, ?> colTenSach = new TableColumn<>();
        GeneratedViewSupport.setProperty(colTenSach, "text", "Tên sách");
        GeneratedViewSupport.bind(controller, "colTenSach", colTenSach);
        GeneratedViewSupport.addColumn(tableInventory, colTenSach);
        TableColumn<?, ?> colTheLoai = new TableColumn<>();
        GeneratedViewSupport.setProperty(colTheLoai, "text", "Thể loại");
        GeneratedViewSupport.bind(controller, "colTheLoai", colTheLoai);
        GeneratedViewSupport.addColumn(tableInventory, colTheLoai);
        TableColumn<?, ?> colSoLuong = new TableColumn<>();
        GeneratedViewSupport.setProperty(colSoLuong, "text", "Số lượng");
        GeneratedViewSupport.bind(controller, "colSoLuong", colSoLuong);
        GeneratedViewSupport.addColumn(tableInventory, colSoLuong);
        TableColumn<?, ?> colGiaBia = new TableColumn<>();
        GeneratedViewSupport.setProperty(colGiaBia, "text", "Giá bìa");
        GeneratedViewSupport.bind(controller, "colGiaBia", colGiaBia);
        GeneratedViewSupport.addColumn(tableInventory, colGiaBia);
        TableColumn<?, ?> colGiaTri = new TableColumn<>();
        GeneratedViewSupport.setProperty(colGiaTri, "text", "Giá trị tồn");
        GeneratedViewSupport.bind(controller, "colGiaTri", colGiaTri);
        GeneratedViewSupport.addColumn(tableInventory, colGiaTri);
        GeneratedViewSupport.addChild(vBox2, tableInventory);
        GeneratedViewSupport.addChild(root, vBox2);

        GeneratedViewSupport.initialize(controller);
        return new ViewBundle(root, controller);
    }
}
