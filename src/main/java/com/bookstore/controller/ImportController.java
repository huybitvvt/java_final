package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.model.ImportDetail;
import com.bookstore.model.ImportOrder;
import com.bookstore.model.Supplier;
import com.bookstore.session.SessionContext;
import com.bookstore.service.BookService;
import com.bookstore.service.ImportService;
import com.bookstore.service.SupplierService;
import com.bookstore.util.AnimationUtil;
import com.bookstore.util.DialogService;
import com.bookstore.util.IconUtil;
import com.bookstore.util.InputFieldUtil;
import com.bookstore.util.NotificationService;
import com.bookstore.util.ThemeManager;
import javafx.beans.binding.DoubleBinding;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Import Controller
 */
public class ImportController {
    private VBox pageRoot;
    private HBox headerBar;
    private VBox contentCard;
    private TextField txtSearch;
    private Button btnAdd;
    private TableView<ImportOrder> tableImports;
    private TableColumn<ImportOrder, String> colMaPN;
    private TableColumn<ImportOrder, Date> colNgayNhap;
    private TableColumn<ImportOrder, String> colNCC;
    private TableColumn<ImportOrder, String> colNhanVien;
    private TableColumn<ImportOrder, Double> colTongTien;
    private TableColumn<ImportOrder, String> colTrangThai;

    private ImportService importService;
    private SupplierService supplierService;
    private BookService bookService;

    public ImportController() {
        this.importService = new ImportService();
        this.supplierService = new SupplierService();
        this.bookService = new BookService();
    }
    public void initialize() {
        colMaPN.setCellValueFactory(new PropertyValueFactory<>("maDonNhapString"));
        colNgayNhap.setCellValueFactory(new PropertyValueFactory<>("ngayNhap"));
        colNCC.setCellValueFactory(cell -> new SimpleStringProperty(resolveSupplierName(cell.getValue().getMaNhaCungCap())));
        colNhanVien.setCellValueFactory(cell -> new SimpleStringProperty("NV #" + cell.getValue().getMaNhanVien()));
        colTongTien.setCellValueFactory(new PropertyValueFactory<>("tongTien"));
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        colNgayNhap.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : com.bookstore.util.FormatUtil.formatDateTime(item));
            }
        });
        colTongTien.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : com.bookstore.util.FormatUtil.formatCurrency(item));
            }
        });
        tableImports.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableImports.setFixedCellSize(48);
        tableImports.setPlaceholder(createPlaceholder("Chưa có phiếu nhập nào."));
        tableImports.getSortOrder().clear();
        tableImports.getSortOrder().add(colMaPN);
        colMaPN.setSortType(TableColumn.SortType.ASCENDING);
        configureTableLayout();
        decorateToolbar();
        loadImports();
        AnimationUtil.playStagger(List.of(headerBar, contentCard));
        InputFieldUtil.installCarryOverWorkaround(txtSearch);
        InputFieldUtil.bindKeywordSearch(txtSearch, this::handleSearch);
    }

    private void decorateToolbar() {
        IconUtil.apply(btnAdd, "fas-box-open", "Tạo phiếu nhập hàng mới");
    }

    private void loadImports() {
        List<ImportOrder> orders = importService.getAllImportOrders();
        if (orders != null) {
            tableImports.getItems().setAll(orders);
            tableImports.sort();
        }
    }
    private void handleAdd() {
        openImportDialog();
    }
    private void handleSearch() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadImports();
            return;
        }
        List<ImportOrder> orders = importService.searchImportOrders(keyword);
        tableImports.getItems().setAll(orders != null ? orders : List.of());
        tableImports.sort();
    }

    private void openImportDialog() {
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        List<Book> books = bookService.getAllBooks();
        if (suppliers == null || suppliers.isEmpty() || books == null || books.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu dữ liệu", "Cần có ít nhất 1 nhà cung cấp và 1 sách để nhập hàng.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        DialogService.attachToOwner(dialog, tableImports);
        dialog.setTitle("Nhập hàng");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setOnShown(event -> ThemeManager.applyTheme(dialog.getDialogPane().getScene()));

        ComboBox<Supplier> cboSupplier = new ComboBox<>();
        cboSupplier.getItems().addAll(suppliers);
        cboSupplier.setValue(suppliers.get(0));

        ComboBox<Book> cboBook = new ComboBox<>();
        cboBook.getItems().addAll(books);
        cboBook.setValue(books.get(0));

        Spinner<Integer> spQuantity = new Spinner<>();
        spQuantity.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 1));
        TextField txtPrice = new TextField(String.valueOf(books.get(0).getGiaBia()));
        DatePicker dpNgayNhap = new DatePicker(java.time.LocalDate.now());
        TextField txtGhiChu = new TextField();

        cboBook.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                txtPrice.setText(String.valueOf(newVal.getGiaBia()));
            }
        });

        GridPane grid = new GridPane();
        grid.getStyleClass().add("dialog-grid");
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Nhà cung cấp:"), cboSupplier);
        grid.addRow(1, new Label("Sách:"), cboBook);
        grid.addRow(2, new Label("Số lượng:"), spQuantity);
        grid.addRow(3, new Label("Đơn giá:"), txtPrice);
        grid.addRow(4, new Label("Ngày nhập:"), dpNgayNhap);
        grid.addRow(5, new Label("Ghi chú:"), txtGhiChu);
        Label hintLabel = new Label("Tạo phiếu nhập nhanh với nhà cung cấp, đầu sách và đơn giá cập nhật theo thời gian thực.");
        hintLabel.getStyleClass().add("section-subtitle");
        VBox content = new VBox(14, hintLabel, grid);
        content.getStyleClass().add("dialog-content");
        dialog.getDialogPane().getStyleClass().add("app-dialog");
        dialog.getDialogPane().setContent(content);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setText("Tạo phiếu nhập");
        okButton.getStyleClass().add("primary-button");
        IconUtil.apply(okButton, "fas-save", "Tạo phiếu nhập");

        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelButton.setText("Hủy");
        cancelButton.getStyleClass().add("chip-button");
        IconUtil.apply(cancelButton, "fas-times", "Đóng biểu mẫu");

        InputFieldUtil.installCarryOverWorkaround(txtPrice, txtGhiChu, dpNgayNhap.getEditor());

        if (dialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                Book book = cboBook.getValue();
                ImportDetail detail = new ImportDetail();
                detail.setMaSach(book.getMaSach());
                detail.setSoLuong(spQuantity.getValue());
                detail.setDonGia(Double.parseDouble(txtPrice.getText().trim()));
                detail.tinhThanhTien();

                ImportOrder order = new ImportOrder();
                order.setMaNhaCungCap(cboSupplier.getValue().getMaNhaCungCap());
                order.setMaNhanVien(resolveEmployeeId());
                order.setNgayNhap(Date.from(dpNgayNhap.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
                order.setTongTien(detail.getThanhTien());
                order.setGhiChu(txtGhiChu.getText().trim());

                if (importService.createImportOrder(order, List.of(detail))) {
                    loadImports();
                    NotificationService.showSuccess(tableImports, "Đã tạo phiếu nhập hàng");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tạo phiếu nhập.");
                }
            } catch (RuntimeException e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Dữ liệu nhập không hợp lệ: " + e.getMessage());
            }
        }
    }

    private void configureTableLayout() {
        DoubleBinding tableWidth = tableImports.widthProperty().subtract(26);
        colMaPN.prefWidthProperty().bind(tableWidth.multiply(0.12));
        colNgayNhap.prefWidthProperty().bind(tableWidth.multiply(0.20));
        colNCC.prefWidthProperty().bind(tableWidth.multiply(0.26));
        colNhanVien.prefWidthProperty().bind(tableWidth.multiply(0.12));
        colTongTien.prefWidthProperty().bind(tableWidth.multiply(0.18));
        colTrangThai.prefWidthProperty().bind(tableWidth.multiply(0.12));
    }

    private Label createPlaceholder(String message) {
        Label placeholder = new Label(message);
        placeholder.getStyleClass().add("empty-state-label");
        return placeholder;
    }

    private int resolveEmployeeId() {
        if (SessionContext.getCurrentUser() != null
                && SessionContext.getCurrentUser().getMaNhanVien() > 0) {
            return SessionContext.getCurrentUser().getMaNhanVien();
        }
        return 1;
    }

    private String resolveSupplierName(int supplierId) {
        Supplier supplier = supplierService.getSupplierById(supplierId);
        return supplier != null ? supplier.getTenNhaCungCap() : "";
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        switch (type) {
            case ERROR -> DialogService.showError(tableImports, title, message);
            case WARNING -> DialogService.showWarning(tableImports, title, message);
            default -> DialogService.showInfo(tableImports, title, message);
        }
    }
}

