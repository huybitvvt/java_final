package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.service.BookService;
import com.bookstore.util.DialogService;
import com.bookstore.util.IconUtil;
import com.bookstore.util.InputFieldUtil;
import com.bookstore.util.ValidationUtil;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Book Dialog Controller - Xử lý dialog thêm/sửa sách
 */
public class BookDialogController {
    private TextField txtTenSach;
    private TextField txtTacGia;
    private TextField txtNXB;
    private ComboBox<String> cboTheLoai;
    private TextField txtNamXB;
    private TextField txtGiaBia;
    private TextField txtSoLuongTon;
    private TextArea txtMoTa;
    private Button btnSave;
    private Button btnCancel;

    // Labels for error display
    private Label lblErrorTenSach;
    private Label lblErrorNamXB;
    private Label lblErrorGiaBia;
    private Label lblErrorSoLuong;

    private Book book;
    private BookService bookService;
    private boolean saved = false;

    private static final String[] THE_LOAI = {
        "Công nghệ", "Ngoại ngữ", "Giáo khoa", "Lịch sử", "Kinh tế", "Mỹ thuật", "Ẩm thực", "Văn học", "Khác"
    };

    public BookDialogController() {
        this.bookService = new BookService();
    }
    public void initialize() {
        cboTheLoai.getItems().addAll(THE_LOAI);
        clearErrors();
        IconUtil.apply(btnSave, "fas-save", "Lưu thông tin sách");
        IconUtil.apply(btnCancel, "fas-times", "Đóng biểu mẫu mà không lưu");
        InputFieldUtil.installCarryOverWorkaround(txtTenSach, txtTacGia, txtNXB, txtNamXB, txtGiaBia, txtSoLuongTon, txtMoTa);
    }

    private void clearErrors() {
        if (lblErrorTenSach != null) lblErrorTenSach.setText("");
        if (lblErrorNamXB != null) lblErrorNamXB.setText("");
        if (lblErrorGiaBia != null) lblErrorGiaBia.setText("");
        if (lblErrorSoLuong != null) lblErrorSoLuong.setText("");
    }

    public void setBook(Book book) {
        this.book = book;
        if (book != null) {
            loadBookData();
        }
    }

    private void loadBookData() {
        txtTenSach.setText(book.getTenSach());
        txtTacGia.setText(book.getTacGia());
        txtNXB.setText(book.getNhaXuatBan());
        cboTheLoai.setValue(book.getTheLoai());
        txtNamXB.setText(String.valueOf(book.getNamXuatBan()));
        txtGiaBia.setText(String.valueOf(book.getGiaBia()));
        txtSoLuongTon.setText(String.valueOf(book.getSoLuongTon()));
        txtMoTa.setText(book.getMoTa());
    }
    private void handleSave() {
        clearErrors();

        // Get input values
        String tenSach = txtTenSach.getText().trim();
        String tacGia = txtTacGia.getText().trim();
        String nhaXuatBan = txtNXB.getText().trim();
        int namXuatBan;
        double giaBia;
        int soLuongTon;

        // Parse numeric values
        try {
            namXuatBan = Integer.parseInt(txtNamXB.getText().trim());
        } catch (NumberFormatException e) {
            if (lblErrorNamXB != null) {
                lblErrorNamXB.setText("Năm xuất bản phải là số");
            }
            showAlert(Alert.AlertType.WARNING, "Lỗi nhập liệu", "Năm xuất bản phải là số nguyên hợp lệ!");
            return;
        }

        try {
            giaBia = Double.parseDouble(txtGiaBia.getText().trim());
        } catch (NumberFormatException e) {
            if (lblErrorGiaBia != null) {
                lblErrorGiaBia.setText("Giá bìa phải là số");
            }
            showAlert(Alert.AlertType.WARNING, "Lỗi nhập liệu", "Giá bìa phải là số hợp lệ!");
            return;
        }

        try {
            soLuongTon = Integer.parseInt(txtSoLuongTon.getText().trim());
        } catch (NumberFormatException e) {
            if (lblErrorSoLuong != null) {
                lblErrorSoLuong.setText("Số lượng phải là số");
            }
            showAlert(Alert.AlertType.WARNING, "Lỗi nhập liệu", "Số lượng tồn phải là số nguyên hợp lệ!");
            return;
        }

        // Use ValidationUtil for enhanced validation
        ValidationUtil.ValidationResult result = ValidationUtil.validateBook(
                tenSach, tacGia, nhaXuatBan, namXuatBan, giaBia, soLuongTon);

        if (!result.isValid()) {
            // Show validation errors
            for (String error : result.getErrors()) {
                if (error.contains("Tên sách")) {
                    if (lblErrorTenSach != null) lblErrorTenSach.setText(error);
                } else if (error.contains("Năm")) {
                    if (lblErrorNamXB != null) lblErrorNamXB.setText(error);
                } else if (error.contains("Giá")) {
                    if (lblErrorGiaBia != null) lblErrorGiaBia.setText(error);
                } else if (error.contains("tồn")) {
                    if (lblErrorSoLuong != null) lblErrorSoLuong.setText(error);
                }
            }
            showAlert(Alert.AlertType.WARNING, "Lỗi validation", result.getErrorMessage());
            return;
        }

        try {
            // Create or update book
            if (book == null) {
                book = new Book();
            }

            book.setTenSach(tenSach);
            book.setTacGia(tacGia);
            book.setNhaXuatBan(nhaXuatBan);
            book.setTheLoai(cboTheLoai.getValue());
            book.setNamXuatBan(namXuatBan);
            book.setGiaBia(giaBia);
            book.setSoLuongTon(soLuongTon);
            book.setMoTa(txtMoTa.getText().trim());

            boolean success;
            if (book.getMaSach() == 0) {
                success = bookService.addBook(book);
            } else {
                success = bookService.updateBook(book);
            }

            if (success) {
                saved = true;
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Lưu sách thành công!");
                closeWindow();
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể lưu sách!");
            }
        } catch (RuntimeException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Lỗi khi lưu: " + e.getMessage());
        }
    }
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) txtTenSach.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        switch (type) {
            case ERROR -> DialogService.showError(txtTenSach, title, message);
            case WARNING -> DialogService.showWarning(txtTenSach, title, message);
            default -> DialogService.showInfo(txtTenSach, title, message);
        }
    }
}

