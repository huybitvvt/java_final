package com.bookstore.controller;

import com.bookstore.model.Employee;
import com.bookstore.service.EmployeeService;
import com.bookstore.util.ActionButtonFactory;
import com.bookstore.util.AnimationUtil;
import com.bookstore.util.DialogService;
import com.bookstore.util.FormatUtil;
import com.bookstore.util.IconUtil;
import com.bookstore.util.InputFieldUtil;
import com.bookstore.util.NotificationService;
import com.bookstore.util.ResponsiveLayoutUtil;
import com.bookstore.util.ThemeManager;
import com.bookstore.util.ValidationUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.List;

/**
 * Employee Controller
 */
public class EmployeeController {
    private VBox pageRoot;
    private HBox headerBar;
    private VBox contentCard;
    private TextField txtSearch;
    private Button btnSearch;
    private Button btnAdd;
    private Button btnEdit;
    private Button btnDelete;
    private TableView<Employee> tableEmployees;
    private TableColumn<Employee, Integer> colMaNV;
    private TableColumn<Employee, String> colHoTen;
    private TableColumn<Employee, String> colGioiTinh;
    private TableColumn<Employee, String> colSDT;
    private TableColumn<Employee, String> colEmail;
    private TableColumn<Employee, String> colChucVu;
    private TableColumn<Employee, Double> colLuong;
    private TableColumn<Employee, String> colTrangThai;
    private TableColumn<Employee, String> colHanhDong;

    private EmployeeService employeeService;
    private ObservableList<Employee> employeeList;
    private final boolean compactMode;

    public EmployeeController() {
        this.employeeService = new EmployeeService();
        this.employeeList = FXCollections.observableArrayList();
        this.compactMode = ResponsiveLayoutUtil.isCompactScreen();
    }
    public void initialize() {
        setupTableColumns();
        setupLayout();
        decorateToolbar();
        setupSelectionActions();
        if (compactMode) {
            applyCompactLayout();
        }
        tableEmployees.getSortOrder().clear();
        tableEmployees.getSortOrder().add(colMaNV);
        colMaNV.setSortType(TableColumn.SortType.ASCENDING);
        loadEmployees();
        AnimationUtil.playStagger(List.of(headerBar, contentCard));
        InputFieldUtil.bindKeywordSearch(txtSearch, this::handleSearch);
        InputFieldUtil.installCarryOverWorkaround(txtSearch);
    }

    private void setupTableColumns() {
        colMaNV.setCellValueFactory(new PropertyValueFactory<>("maNhanVien"));
        colHoTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colGioiTinh.setCellValueFactory(new PropertyValueFactory<>("gioiTinh"));
        colSDT.setCellValueFactory(new PropertyValueFactory<>("soDienThoai"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colChucVu.setCellValueFactory(new PropertyValueFactory<>("chucVu"));
        colLuong.setCellValueFactory(new PropertyValueFactory<>("luong"));
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        colHanhDong.setCellValueFactory(cellData -> new SimpleStringProperty("actions"));

        colHanhDong.setSortable(false);
        colLuong.setCellFactory(column -> new TableCell<Employee, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    setText(FormatUtil.formatCurrency(item));
                }
            }
        });

        colTrangThai.setCellFactory(column -> new TableCell<Employee, String>() {
            private final Label badge = new Label();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }

                badge.setText(item);
                badge.getStyleClass().removeAll("status-badge-success", "status-badge-neutral");
                badge.getStyleClass().addAll("status-badge",
                        "Hoạt động".equalsIgnoreCase(item) ? "status-badge-success" : "status-badge-neutral");
                setGraphic(badge);
            }
        });

        colHanhDong.setCellFactory(column -> new TableCell<Employee, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Employee emp = getTableView().getItems().get(getIndex());
                    Button btnEdit = ActionButtonFactory.createEditButton(e -> handleEdit(emp));
                    Button btnDelete = ActionButtonFactory.createDeleteButton(e -> handleDelete(emp));
                    HBox actions = ActionButtonFactory.createGroup(btnEdit, btnDelete);
                    setGraphic(actions);
                }
            }
        });
    }

    private void setupLayout() {
        tableEmployees.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tableEmployees.setFixedCellSize(48);
        tableEmployees.setPlaceholder(createPlaceholder("Chưa có nhân viên nào hoặc chưa có kết quả phù hợp."));
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
        IconUtil.apply(btnSearch, "fas-search", "Tìm nhân viên");
        IconUtil.apply(btnAdd, "fas-user-plus", "Thêm hồ sơ nhân viên");
        IconUtil.apply(btnEdit, "fas-pen", "Sửa nhân viên đang chọn");
        IconUtil.apply(btnDelete, "fas-trash", "Xóa nhân viên đang chọn");
    }

    private void applyCompactLayout() {
        btnSearch.setText("Tìm");
        btnAdd.setText("Thêm");
        tableEmployees.setFixedCellSize(42);
        applyScrollableColumnWidths();
    }

    private void applyScrollableColumnWidths() {
        if (compactMode) {
            setColumnWidth(colMaNV, 70);
            setColumnWidth(colHoTen, 220);
            setColumnWidth(colGioiTinh, 100);
            setColumnWidth(colSDT, 135);
            setColumnWidth(colEmail, 240);
            setColumnWidth(colChucVu, 140);
            setColumnWidth(colLuong, 150);
            setColumnWidth(colTrangThai, 140);
            setColumnWidth(colHanhDong, 104);
            colHanhDong.setMaxWidth(104);
            return;
        }

        setColumnWidth(colMaNV, 80);
        setColumnWidth(colHoTen, 240);
        setColumnWidth(colGioiTinh, 110);
        setColumnWidth(colSDT, 150);
        setColumnWidth(colEmail, 260);
        setColumnWidth(colChucVu, 160);
        setColumnWidth(colLuong, 160);
        setColumnWidth(colTrangThai, 150);
        setColumnWidth(colHanhDong, 150);
        colHanhDong.setMaxWidth(150);
    }

    private void setColumnWidth(TableColumn<?, ?> column, double width) {
        column.prefWidthProperty().unbind();
        column.setMinWidth(width);
        column.setPrefWidth(width);
    }

    private void setupSelectionActions() {
        updateSelectionButtons(null);
        tableEmployees.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) ->
                updateSelectionButtons(newValue));
    }

    private void updateSelectionButtons(Employee employee) {
        boolean hasSelection = employee != null;
        if (btnEdit != null) {
            btnEdit.setDisable(!hasSelection);
        }
        if (btnDelete != null) {
            btnDelete.setDisable(!hasSelection);
        }
    }

    private void loadEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        employeeList.clear();
        tableEmployees.setItems(employeeList);

        if (employees == null) {
            tableEmployees.setPlaceholder(createPlaceholder("Không tải được dữ liệu nhân viên. Kiểm tra kết nối MySQL."));
            return;
        }

        tableEmployees.setPlaceholder(createPlaceholder("Chưa có nhân viên nào hoặc chưa có kết quả phù hợp."));
        employees.sort(java.util.Comparator.comparingInt(Employee::getMaNhanVien));
        employeeList.addAll(employees);
        tableEmployees.sort();
    }
    private void handleSearch() {
        String keyword = txtSearch.getText().trim();
        if (!keyword.isEmpty()) {
            List<Employee> employees = employeeService.searchEmployees(keyword);
            employeeList.clear();
            if (employees != null) {
                employees.sort(java.util.Comparator.comparingInt(Employee::getMaNhanVien));
                employeeList.addAll(employees);
                tableEmployees.sort();
            }
        } else {
            loadEmployees();
        }
    }
    private void handleAdd() {
        openEmployeeDialog(null);
    }
    private void handleEditSelected() {
        Employee employee = tableEmployees.getSelectionModel().getSelectedItem();
        if (employee == null) {
            showAlert(Alert.AlertType.WARNING, "Thông báo", "Vui lòng chọn nhân viên cần sửa.");
            return;
        }
        handleEdit(employee);
    }
    private void handleDeleteSelected() {
        Employee employee = tableEmployees.getSelectionModel().getSelectedItem();
        if (employee == null) {
            showAlert(Alert.AlertType.WARNING, "Thông báo", "Vui lòng chọn nhân viên cần xóa.");
            return;
        }
        handleDelete(employee);
    }
    private void handleEdit(Employee employee) {
        openEmployeeDialog(employee);
    }
    private void handleDelete(Employee employee) {
        if (DialogService.confirm(tableEmployees, "Xóa nhân viên",
                "Bạn có chắc chắn muốn xóa nhân viên: " + employee.getHoTen() + "?", "Xóa")) {
            if (employeeService.deleteEmployee(employee.getMaNhanVien())) {
                NotificationService.showSuccess(tableEmployees, "Đã xóa nhân viên");
                loadEmployees();
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa nhân viên!");
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        switch (type) {
            case ERROR -> DialogService.showError(tableEmployees, title, message);
            case WARNING -> DialogService.showWarning(tableEmployees, title, message);
            default -> DialogService.showInfo(tableEmployees, title, message);
        }
    }

    private void openEmployeeDialog(Employee employee) {
        boolean isNew = employee == null;
        Employee target = isNew ? new Employee() : employee;

        Dialog<Employee> dialog = new Dialog<>();
        DialogService.attachToOwner(dialog, tableEmployees);
        dialog.setTitle(isNew ? "Thêm nhân viên" : "Sửa nhân viên");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setOnShown(event -> ThemeManager.applyTheme(dialog.getDialogPane().getScene()));
        dialog.getDialogPane().getStyleClass().add("app-dialog");

        TextField txtHoTen = new TextField(target.getHoTen());
        ComboBox<String> cboGioiTinh = new ComboBox<>(FXCollections.observableArrayList("Nam", "Nữ", "Khác"));
        cboGioiTinh.setValue(target.getGioiTinh() != null ? target.getGioiTinh() : "Nam");
        TextField txtSdt = new TextField(target.getSoDienThoai());
        TextField txtEmail = new TextField(target.getEmail());
        TextField txtDiaChi = new TextField(target.getDiaChi());
        TextField txtChucVu = new TextField(target.getChucVu() != null ? target.getChucVu() : "Nhân viên");
        TextField txtLuong = new TextField(target.getLuong() > 0 ? String.valueOf(target.getLuong()) : "8000000");
        ComboBox<String> cboTrangThai = new ComboBox<>(FXCollections.observableArrayList("Hoạt động", "Ngừng hoạt động"));
        cboTrangThai.setValue(target.getTrangThai() != null ? target.getTrangThai() : "Hoạt động");
        DatePicker dpNgayVaoLam = new DatePicker(target.getNgayVaoLam() != null
                ? ValidationUtil.toLocalDate(target.getNgayVaoLam())
                : LocalDate.now());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Họ tên:"), txtHoTen);
        grid.addRow(1, new Label("Giới tính:"), cboGioiTinh);
        grid.addRow(2, new Label("SĐT:"), txtSdt);
        grid.addRow(3, new Label("Email:"), txtEmail);
        grid.addRow(4, new Label("Địa chỉ:"), txtDiaChi);
        grid.addRow(5, new Label("Chức vụ:"), txtChucVu);
        grid.addRow(6, new Label("Lương:"), txtLuong);
        grid.addRow(7, new Label("Ngày vào làm:"), dpNgayVaoLam);
        grid.addRow(8, new Label("Trạng thái:"), cboTrangThai);
        dialog.getDialogPane().setContent(grid);
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setText(isNew ? "Thêm nhân viên" : "Lưu thay đổi");
        okButton.getStyleClass().add("primary-button");
        IconUtil.apply(okButton, isNew ? "fas-user-plus" : "fas-save", isNew ? "Thêm nhân viên" : "Lưu thay đổi");
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelButton.setText("Hủy");
        cancelButton.getStyleClass().add("chip-button");
        IconUtil.apply(cancelButton, "fas-times", "Đóng biểu mẫu");
        InputFieldUtil.installCarryOverWorkaround(txtHoTen, txtSdt, txtEmail, txtDiaChi, txtChucVu, txtLuong);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                target.setHoTen(txtHoTen.getText().trim());
                target.setGioiTinh(cboGioiTinh.getValue());
                target.setSoDienThoai(txtSdt.getText().trim());
                target.setEmail(txtEmail.getText().trim());
                target.setDiaChi(txtDiaChi.getText().trim());
                target.setChucVu(txtChucVu.getText().trim());
                target.setLuong(Double.parseDouble(txtLuong.getText().trim()));
                target.setTrangThai(cboTrangThai.getValue());
                target.setNgayVaoLam(ValidationUtil.toDate(dpNgayVaoLam.getValue()));
                return target;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            boolean success = isNew ? employeeService.addEmployee(result) : employeeService.updateEmployee(result);
            if (success) {
                loadEmployees();
                NotificationService.showSuccess(tableEmployees,
                        isNew ? "Đã thêm nhân viên thành công" : "Đã cập nhật nhân viên");
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể lưu nhân viên!");
            }
        });
    }
}

