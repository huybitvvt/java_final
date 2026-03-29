package com.bookstore.controller;

import com.bookstore.model.Supplier;
import com.bookstore.service.SupplierService;
import com.bookstore.util.ActionButtonFactory;
import com.bookstore.util.AnimationUtil;
import com.bookstore.util.DialogService;
import com.bookstore.util.IconUtil;
import com.bookstore.util.InputFieldUtil;
import com.bookstore.util.NotificationService;
import com.bookstore.util.ResponsiveLayoutUtil;
import com.bookstore.util.ThemeManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

/**
 * Supplier Controller
 */
public class SupplierController {
    private VBox pageRoot;
    private HBox headerBar;
    private VBox contentCard;
    private TextField txtSearch;
    private Button btnSearch;
    private Button btnAdd;
    private TableView<Supplier> tableSuppliers;
    private TableColumn<Supplier, Integer> colMaNCC;
    private TableColumn<Supplier, String> colTenNCC;
    private TableColumn<Supplier, String> colDiaChi;
    private TableColumn<Supplier, String> colSDT;
    private TableColumn<Supplier, String> colEmail;
    private TableColumn<Supplier, String> colNguoiLH;
    private TableColumn<Supplier, String> colHanhDong;

    private SupplierService supplierService;
    private ObservableList<Supplier> supplierList;
    private final boolean compactMode;

    public SupplierController() {
        this.supplierService = new SupplierService();
        this.supplierList = FXCollections.observableArrayList();
        this.compactMode = ResponsiveLayoutUtil.isCompactScreen();
    }
    public void initialize() {
        colMaNCC.setCellValueFactory(new PropertyValueFactory<>("maNhaCungCap"));
        colTenNCC.setCellValueFactory(new PropertyValueFactory<>("tenNhaCungCap"));
        colDiaChi.setCellValueFactory(new PropertyValueFactory<>("diaChi"));
        colSDT.setCellValueFactory(new PropertyValueFactory<>("soDienThoai"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colNguoiLH.setCellValueFactory(new PropertyValueFactory<>("nguoiLienHe"));
        colHanhDong.setCellValueFactory(cellData -> new SimpleStringProperty("actions"));
        colHanhDong.setSortable(false);
        colHanhDong.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Supplier supplier = getTableView().getItems().get(getIndex());
                    Button btnDelete = ActionButtonFactory.createDeleteButton(e -> handleDelete(supplier));
                    setGraphic(btnDelete);
                }
            }
        });

        tableSuppliers.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tableSuppliers.setFixedCellSize(48);
        tableSuppliers.setPlaceholder(createPlaceholder("Chưa có nhà cung cấp nào."));
        tableSuppliers.getSortOrder().clear();
        tableSuppliers.getSortOrder().add(colMaNCC);
        colMaNCC.setSortType(TableColumn.SortType.ASCENDING);
        configureTableLayout();
        decorateToolbar();
        loadSuppliers();
        AnimationUtil.playStagger(List.of(headerBar, contentCard));
        InputFieldUtil.installCarryOverWorkaround(txtSearch);
        InputFieldUtil.bindKeywordSearch(txtSearch, this::handleSearch);
    }

    private void decorateToolbar() {
        IconUtil.apply(btnSearch, "fas-search", "Tìm nhà cung cấp");
        IconUtil.apply(btnAdd, "fas-plus", "Thêm nhà cung cấp mới");
        if (compactMode) {
            btnSearch.setText("Tìm");
            btnAdd.setText("Thêm");
            tableSuppliers.setFixedCellSize(42);
        }
    }

    private void loadSuppliers() {
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        if (suppliers != null) {
            suppliers.sort(java.util.Comparator.comparingInt(Supplier::getMaNhaCungCap));
            supplierList.clear();
            supplierList.addAll(suppliers);
            tableSuppliers.setItems(supplierList);
            tableSuppliers.sort();
        }
    }
    private void handleSearch() {
        String keyword = txtSearch.getText().trim();
        if (!keyword.isEmpty()) {
            List<Supplier> suppliers = supplierService.searchSuppliers(keyword);
            supplierList.clear();
            if (suppliers != null) {
                suppliers.sort(java.util.Comparator.comparingInt(Supplier::getMaNhaCungCap));
                supplierList.addAll(suppliers);
                tableSuppliers.sort();
            }
        } else {
            loadSuppliers();
        }
    }
    private void handleAdd() {
        openSupplierDialog();
    }

    private void handleDelete(Supplier supplier) {
        if (DialogService.confirm(tableSuppliers, "Xóa nhà cung cấp",
                "Xóa nhà cung cấp: " + supplier.getTenNhaCungCap() + "?", "Xóa")) {
            if (supplierService.deleteSupplier(supplier.getMaNhaCungCap())) {
                loadSuppliers();
                NotificationService.showSuccess(tableSuppliers, "Đã xóa nhà cung cấp thành công");
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa nhà cung cấp!");
            }
        }
    }

    private void openSupplierDialog() {
        Dialog<Supplier> dialog = new Dialog<>();
        DialogService.attachToOwner(dialog, tableSuppliers);
        dialog.setTitle("Thêm nhà cung cấp");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setOnShown(event -> ThemeManager.applyTheme(dialog.getDialogPane().getScene()));

        TextField txtTen = new TextField();
        TextField txtDiaChi = new TextField();
        TextField txtSdt = new TextField();
        TextField txtEmail = new TextField();
        TextField txtLienHe = new TextField();

        GridPane grid = new GridPane();
        grid.getStyleClass().add("dialog-grid");
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Tên NCC:"), txtTen);
        grid.addRow(1, new Label("Địa chỉ:"), txtDiaChi);
        grid.addRow(2, new Label("SĐT:"), txtSdt);
        grid.addRow(3, new Label("Email:"), txtEmail);
        grid.addRow(4, new Label("Liên hệ:"), txtLienHe);
        Label hintLabel = new Label("Lưu thông tin nhà cung cấp để phục vụ nhập hàng và theo dõi liên hệ.");
        hintLabel.getStyleClass().add("section-subtitle");
        VBox content = new VBox(14, hintLabel, grid);
        content.getStyleClass().add("dialog-content");
        dialog.getDialogPane().getStyleClass().add("app-dialog");
        dialog.getDialogPane().setContent(content);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setText("Lưu nhà cung cấp");
        okButton.getStyleClass().add("primary-button");
        IconUtil.apply(okButton, "fas-save", "Lưu thông tin nhà cung cấp");

        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelButton.setText("Hủy");
        cancelButton.getStyleClass().add("chip-button");
        IconUtil.apply(cancelButton, "fas-times", "Đóng biểu mẫu");

        InputFieldUtil.installCarryOverWorkaround(txtTen, txtDiaChi, txtSdt, txtEmail, txtLienHe);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                Supplier supplier = new Supplier();
                supplier.setTenNhaCungCap(txtTen.getText().trim());
                supplier.setDiaChi(txtDiaChi.getText().trim());
                supplier.setSoDienThoai(txtSdt.getText().trim());
                supplier.setEmail(txtEmail.getText().trim());
                supplier.setNguoiLienHe(txtLienHe.getText().trim());
                return supplier;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(supplier -> {
            if (supplierService.addSupplier(supplier)) {
                loadSuppliers();
                NotificationService.showSuccess(tableSuppliers, "Đã thêm nhà cung cấp thành công");
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể thêm nhà cung cấp!");
            }
        });
    }

    private void configureTableLayout() {
        if (compactMode) {
            setColumnWidth(colMaNCC, 70);
            setColumnWidth(colTenNCC, 240);
            setColumnWidth(colDiaChi, 220);
            setColumnWidth(colSDT, 140);
            setColumnWidth(colEmail, 220);
            setColumnWidth(colNguoiLH, 170);
            setColumnWidth(colHanhDong, 104);
            colHanhDong.setMaxWidth(104);
            colHanhDong.setResizable(false);
            return;
        }

        setColumnWidth(colMaNCC, 80);
        setColumnWidth(colTenNCC, 260);
        setColumnWidth(colDiaChi, 240);
        setColumnWidth(colSDT, 150);
        setColumnWidth(colEmail, 240);
        setColumnWidth(colNguoiLH, 190);
        setColumnWidth(colHanhDong, 120);
        colHanhDong.setMaxWidth(120);
        colHanhDong.setResizable(false);
    }

    private void setColumnWidth(TableColumn<?, ?> column, double width) {
        column.prefWidthProperty().unbind();
        column.setMinWidth(width);
        column.setPrefWidth(width);
    }

    private Label createPlaceholder(String message) {
        Label placeholder = new Label(message);
        placeholder.getStyleClass().add("empty-state-label");
        return placeholder;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        switch (type) {
            case ERROR -> DialogService.showError(tableSuppliers, title, message);
            case WARNING -> DialogService.showWarning(tableSuppliers, title, message);
            default -> DialogService.showInfo(tableSuppliers, title, message);
        }
    }
}

