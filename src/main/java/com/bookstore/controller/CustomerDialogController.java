package com.bookstore.controller;

import com.bookstore.model.Customer;
import com.bookstore.service.CustomerService;
import com.bookstore.util.DialogService;
import com.bookstore.util.IconUtil;
import com.bookstore.util.InputFieldUtil;
import com.bookstore.util.ValidationUtil;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Customer Dialog Controller
 */
public class CustomerDialogController {
    private TextField txtHoTen;
    private ComboBox<String> cboGioiTinh;
    private DatePicker dpNgaySinh;
    private TextField txtSDT;
    private TextField txtEmail;
    private TextField txtDiaChi;
    private Button btnSave;
    private Button btnCancel;

    private Customer customer;
    private CustomerService customerService;

    public CustomerDialogController() {
        this.customerService = new CustomerService();
    }
    public void initialize() {
        cboGioiTinh.getItems().addAll("Nam", "Nữ", "Khác");
        InputFieldUtil.installCarryOverWorkaround(txtHoTen, txtSDT, txtEmail, txtDiaChi);
        IconUtil.apply(btnSave, "fas-save", "Lưu hồ sơ khách hàng");
        IconUtil.apply(btnCancel, "fas-times", "Đóng biểu mẫu");
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        if (customer != null) {
            loadCustomerData();
        }
    }

    private void loadCustomerData() {
        txtHoTen.setText(customer.getHoTen());
        cboGioiTinh.setValue(customer.getGioiTinh());
        if (customer.getNgaySinh() != null) {
            dpNgaySinh.setValue(ValidationUtil.toLocalDate(customer.getNgaySinh()));
        }
        txtSDT.setText(customer.getSoDienThoai());
        txtEmail.setText(customer.getEmail());
        txtDiaChi.setText(customer.getDiaChi());
    }
    private void handleSave() {
        if (txtHoTen.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập họ tên!");
            return;
        }

        try {
            if (customer == null) {
                customer = new Customer();
            }

            customer.setHoTen(txtHoTen.getText().trim());
            customer.setGioiTinh(cboGioiTinh.getValue());
            customer.setNgaySinh(ValidationUtil.toDate(dpNgaySinh.getValue()));
            customer.setSoDienThoai(txtSDT.getText().trim());
            customer.setEmail(txtEmail.getText().trim());
            customer.setDiaChi(txtDiaChi.getText().trim());

            boolean success;
            if (customer.getMaKhachHang() == 0) {
                success = customerService.addCustomer(customer);
            } else {
                success = customerService.updateCustomer(customer);
            }

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã lưu khách hàng.");
                closeWindow();
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể lưu!");
            }
        } catch (RuntimeException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", e.getMessage());
        }
    }
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) txtHoTen.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        switch (type) {
            case ERROR -> DialogService.showError(txtHoTen, title, message);
            case WARNING -> DialogService.showWarning(txtHoTen, title, message);
            default -> DialogService.showInfo(txtHoTen, title, message);
        }
    }
}

