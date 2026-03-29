package com.bookstore.controller;

import com.bookstore.model.Customer;
import com.bookstore.view.ViewBundle;
import com.bookstore.view.ViewId;
import com.bookstore.service.CustomerService;
import com.bookstore.util.AppLog;
import com.bookstore.util.DialogService;
import com.bookstore.util.ExceptionMessageUtil;
import com.bookstore.util.FormatUtil;
import com.bookstore.util.ExportExcelUtil;
import com.bookstore.util.ActionButtonFactory;
import com.bookstore.util.AnimationUtil;
import com.bookstore.util.IconUtil;
import com.bookstore.util.InputFieldUtil;
import com.bookstore.util.NotificationService;
import com.bookstore.util.ResponsiveLayoutUtil;
import com.bookstore.util.ThemeManager;
import com.bookstore.util.TooltipUtil;
import com.bookstore.util.ViewLoaderUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Customer Controller - Xử lý quản lý khách hàng
 */
public class CustomerController {
    private VBox pageRoot;
    private HBox headerBar;
    private VBox contentCard;
    private TextField txtSearch;
    private Button btnSearch;
    private Button btnAdd;
    private Button btnExport;
    private Button btnRefresh;
    private TableView<Customer> tableCustomers;
    private TableColumn<Customer, Integer> colMaKH;
    private TableColumn<Customer, String> colHoTen;
    private TableColumn<Customer, String> colGioiTinh;
    private TableColumn<Customer, Date> colNgaySinh;
    private TableColumn<Customer, String> colSDT;
    private TableColumn<Customer, String> colEmail;
    private TableColumn<Customer, String> colDiaChi;
    private TableColumn<Customer, Integer> colDiem;
    private TableColumn<Customer, String> colHanhDong;

    private CustomerService customerService;
    private ObservableList<Customer> customerList;
    private final boolean compactMode;

    public CustomerController() {
        this.customerService = new CustomerService();
        this.customerList = FXCollections.observableArrayList();
        this.compactMode = ResponsiveLayoutUtil.isCompactScreen();
    }
    public void initialize() {
        setupTableColumns();
        setupLayout();
        decorateToolbar();
        if (compactMode) {
            applyCompactLayout();
        }
        tableCustomers.getSortOrder().clear();
        tableCustomers.getSortOrder().add(colMaKH);
        colMaKH.setSortType(TableColumn.SortType.ASCENDING);
        loadCustomers();
        AnimationUtil.playStagger(List.of(headerBar, contentCard));
        InputFieldUtil.bindKeywordSearch(txtSearch, this::handleSearch);
        InputFieldUtil.installCarryOverWorkaround(txtSearch);
    }

    private void setupTableColumns() {
        colMaKH.setCellValueFactory(new PropertyValueFactory<>("maKhachHang"));
        colHoTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colGioiTinh.setCellValueFactory(new PropertyValueFactory<>("gioiTinh"));
        colNgaySinh.setCellValueFactory(new PropertyValueFactory<>("ngaySinh"));
        colSDT.setCellValueFactory(new PropertyValueFactory<>("soDienThoai"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colDiaChi.setCellValueFactory(new PropertyValueFactory<>("diaChi"));
        colDiem.setCellValueFactory(new PropertyValueFactory<>("diemTichLuy"));
        colHanhDong.setCellValueFactory(cellData -> new SimpleStringProperty("actions"));

        colNgaySinh.setCellFactory(column -> new TableCell<Customer, Date>() {
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    setText(FormatUtil.formatDate(item));
                }
            }
        });

        colDiem.setCellFactory(column -> new TableCell<Customer, Integer>() {
            private final Label badge = new Label();

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                Customer customer = getTableView().getItems().get(getIndex());
                String tier = resolveTier(customer);
                badge.setText(compactMode
                        ? (item == null ? 0 : item) + " • " + tier
                        : (item == null ? 0 : item) + " điểm • " + tier);
                badge.getStyleClass().removeAll("tier-badge-standard", "tier-badge-silver",
                        "status-badge-warning", "status-badge-success");
                badge.getStyleClass().add("tier-badge");
                switch (tier) {
                    case "Vàng" -> badge.getStyleClass().add("status-badge-warning");
                    case "Kim cương" -> badge.getStyleClass().add("status-badge-success");
                    case "Bạc" -> badge.getStyleClass().add("tier-badge-silver");
                    default -> badge.getStyleClass().add("tier-badge-standard");
                }
                setGraphic(badge);
            }
        });

        colHanhDong.setSortable(false);
        colHanhDong.setCellFactory(column -> new TableCell<Customer, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Customer customer = getTableView().getItems().get(getIndex());
                    Button btnEdit = ActionButtonFactory.createEditButton(e -> handleEdit(customer));
                    Button btnDelete = ActionButtonFactory.createDeleteButton(e -> handleDelete(customer));
                    HBox actions = ActionButtonFactory.createGroup(btnEdit, btnDelete);
                    setGraphic(actions);
                }
            }
        });
    }

    private void setupLayout() {
        tableCustomers.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tableCustomers.setFixedCellSize(48);
        tableCustomers.setPlaceholder(createPlaceholder("Chưa có khách hàng nào hoặc chưa có kết quả phù hợp."));
        configureActionColumn();
        applyScrollableColumnWidths();
    }

    private void configureActionColumn() {
        colHanhDong.setMinWidth(170);
        colHanhDong.setPrefWidth(170);
        colHanhDong.setMaxWidth(170);
        colHanhDong.setResizable(false);
    }

    private Label createPlaceholder(String message) {
        Label placeholder = new Label(message);
        placeholder.getStyleClass().add("empty-state-label");
        return placeholder;
    }

    private void decorateToolbar() {
        IconUtil.apply(btnSearch, "fas-search", "Tìm khách hàng");
        IconUtil.apply(btnAdd, "fas-user-plus", "Thêm hồ sơ khách hàng mới");
        IconUtil.apply(btnExport, "fas-file-export", "Xuất danh sách khách hàng ra Excel");
        IconUtil.apply(btnRefresh, "fas-sync-alt", "Làm mới danh sách");
        TooltipUtil.install(txtSearch, "Nhập họ tên, số điện thoại hoặc email khách hàng");
    }

    private void applyCompactLayout() {
        btnSearch.setText("Tìm");
        btnAdd.setText("Thêm");
        btnExport.setText("Excel");
        btnRefresh.setText("Mới");
        tableCustomers.setFixedCellSize(42);
        applyScrollableColumnWidths();
    }

    private void applyScrollableColumnWidths() {
        if (compactMode) {
            setColumnWidth(colMaKH, 70);
            setColumnWidth(colHoTen, 210);
            setColumnWidth(colGioiTinh, 100);
            setColumnWidth(colNgaySinh, 120);
            setColumnWidth(colSDT, 135);
            setColumnWidth(colEmail, 220);
            setColumnWidth(colDiaChi, 220);
            setColumnWidth(colDiem, 150);
            setColumnWidth(colHanhDong, 104);
            colHanhDong.setMaxWidth(104);
            return;
        }

        setColumnWidth(colMaKH, 80);
        setColumnWidth(colHoTen, 230);
        setColumnWidth(colGioiTinh, 110);
        setColumnWidth(colNgaySinh, 130);
        setColumnWidth(colSDT, 150);
        setColumnWidth(colEmail, 250);
        setColumnWidth(colDiaChi, 240);
        setColumnWidth(colDiem, 170);
        setColumnWidth(colHanhDong, 150);
        colHanhDong.setMaxWidth(150);
    }

    private void setColumnWidth(TableColumn<?, ?> column, double width) {
        column.prefWidthProperty().unbind();
        column.setMinWidth(width);
        column.setPrefWidth(width);
    }

    private void loadCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        customerList.clear();
        tableCustomers.setItems(customerList);

        if (customers == null) {
            tableCustomers.setPlaceholder(createPlaceholder("Không tải được dữ liệu khách hàng. Kiểm tra kết nối MySQL."));
            return;
        }

        tableCustomers.setPlaceholder(createPlaceholder("Chưa có khách hàng nào hoặc chưa có kết quả phù hợp."));
        customers.sort(java.util.Comparator.comparingInt(Customer::getMaKhachHang));
        customerList.addAll(customers);
        tableCustomers.sort();
    }
    private void handleSearch() {
        String keyword = txtSearch.getText().trim();
        if (!keyword.isEmpty()) {
            List<Customer> customers = customerService.searchCustomers(keyword);
            customerList.clear();
            if (customers != null) {
                customers.sort(java.util.Comparator.comparingInt(Customer::getMaKhachHang));
                customerList.addAll(customers);
                tableCustomers.sort();
            }
        } else {
            loadCustomers();
        }
    }
    private void handleAdd() {
        openCustomerDialog(null);
    }
    private void handleEdit(Customer customer) {
        openCustomerDialog(customer);
    }
    private void handleDelete(Customer customer) {
        if (DialogService.confirm(tableCustomers, "Xóa khách hàng",
                "Bạn có chắc chắn muốn xóa khách hàng: " + customer.getHoTen() + "?", "Xóa")) {
            if (customerService.deleteCustomer(customer.getMaKhachHang())) {
                NotificationService.showSuccess(tableCustomers, "Đã xóa khách hàng");
                loadCustomers();
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa khách hàng!");
            }
        }
    }
    private void handleRefresh() {
        txtSearch.clear();
        loadCustomers();
    }
    private void handleExport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu file Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName("DanhSachKhachHang_" + System.currentTimeMillis() + ".xlsx");

        File file = fileChooser.showSaveDialog(tableCustomers.getScene().getWindow());
        if (file != null) {
            try {
                List<Customer> customers = customerService.getAllCustomers();
                if (customers != null && !customers.isEmpty()) {
                    boolean success = ExportExcelUtil.exportCustomers(customers, file.getAbsolutePath());
                    if (success) {
                        NotificationService.showSuccess(tableCustomers, "Xuất Excel khách hàng thành công");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Lỗi", "Xuất Excel thất bại!");
                    }
                } else {
                    showAlert(Alert.AlertType.WARNING, "Thông báo", "Không có dữ liệu để xuất!");
                }
            } catch (RuntimeException e) {
                AppLog.error(CustomerController.class, "Khong the xuat danh sach khach hang ra Excel", e);
                showAlert(Alert.AlertType.ERROR, "Lỗi",
                        "Lỗi khi xuất Excel: " + ExceptionMessageUtil.resolve(e, "Lỗi không xác định"));
            }
        }
    }

    private void openCustomerDialog(Customer customer) {
        try {
            ViewBundle view = ViewLoaderUtil.loadView(ViewId.CUSTOMER_DIALOG);
            Parent root = view.getRoot();

            CustomerDialogController controller = view.getController(CustomerDialogController.class);
            controller.setCustomer(customer);

            Stage stage = new Stage();
            DialogService.attachToOwner(stage, tableCustomers);
            stage.setTitle(customer == null ? "Thêm khách hàng" : "Sửa khách hàng");
            Scene scene = new Scene(root);
            ThemeManager.applyTheme(scene);
            stage.setScene(scene);
            stage.showAndWait();

            loadCustomers();
        } catch (RuntimeException e) {
            AppLog.error(CustomerController.class, "Khong the mo form khach hang", e);
            showAlert(Alert.AlertType.ERROR, "Lỗi",
                    "Không thể mở form khách hàng: " + ExceptionMessageUtil.resolve(e, "Lỗi không xác định"));
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        switch (type) {
            case ERROR -> DialogService.showError(tableCustomers, title, message);
            case WARNING -> DialogService.showWarning(tableCustomers, title, message);
            default -> DialogService.showInfo(tableCustomers, title, message);
        }
    }

    private String resolveTier(Customer customer) {
        if (customer == null || customer.getDiemTichLuy() <= 0) {
            return "Tiêu chuẩn";
        }
        if (customer.getDiemTichLuy() >= 300) {
            return "Kim cương";
        }
        if (customer.getDiemTichLuy() >= 120) {
            return "Vàng";
        }
        if (customer.getDiemTichLuy() >= 50) {
            return "Bạc";
        }
        return "Tiêu chuẩn";
    }
}

