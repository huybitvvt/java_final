package com.bookstore.service;

import com.bookstore.dao.UserDAO;
import com.bookstore.dao.EmployeeDAO;
import com.bookstore.database.DatabaseConnection;
import com.bookstore.model.User;
import com.bookstore.model.Employee;
import com.bookstore.util.AppLog;
import com.bookstore.util.PasswordUtil;
import com.bookstore.util.ValidationUtil;

import java.sql.SQLException;

/**
 * User Service - Business logic layer for User (Authentication)
 */
public class UserService {

    private final UserDAO userDAO;
    private final EmployeeDAO employeeDAO;
    private User currentUser;

    public UserService() {
        this.userDAO = new UserDAO();
        this.employeeDAO = new EmployeeDAO();
    }

    /**
     * Đăng nhập
     */
    public User login(String username, String password) {
        try {
            if (!DatabaseConnection.testConnection()) {
                throw new IllegalStateException("Không kết nối được database. Kiểm tra kết nối SQL Server và cấu hình database.properties.");
            }

            String hashedPassword = PasswordUtil.md5(password);
            User user = userDAO.login(username, hashedPassword);

            if (user != null) {
                try {
                    Employee employee = employeeDAO.getById(user.getMaNhanVien());
                    user.setNhanVien(employee);
                } catch (SQLException e) {
                    AppLog.warn(UserService.class, "Khong the tai thong tin nhan vien cho tai khoan " + user.getMaTaiKhoan(), e);
                }
                this.currentUser = user;
            }

            return user;
        } catch (SQLException e) {
            AppLog.error(UserService.class, "Loi truy van trong qua trinh dang nhap", e);
            throw new RuntimeException("Lỗi truy vấn database: " + e.getMessage(), e);
        }
    }

    /**
     * Đăng xuất
     */
    public void logout() {
        this.currentUser = null;
    }

    /**
     * Lấy thông tin user hiện tại
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Kiểm tra đã đăng nhập chưa
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Kiểm tra có phải Admin không
     */
    public boolean isAdmin() {
        return currentUser != null && ("Admin".equals(currentUser.getVaiTro()) || "Quản lý".equals(currentUser.getVaiTro()));
    }

    /**
     * Đổi mật khẩu
     */
    public boolean changePassword(String oldPassword, String newPassword) {
        return changePassword(currentUser, oldPassword, newPassword);
    }

    public boolean changePassword(User user, String oldPassword, String newPassword) {
        try {
            if (user == null) {
                AppLog.warn(UserService.class, "Khong the doi mat khau khi chua co nguoi dung dang nhap");
                return false;
            }

            if (!ValidationUtil.isValidPassword(newPassword)) {
                AppLog.warn(UserService.class, "Mat khau moi khong dat yeu cau toi thieu");
                return false;
            }

            User persistedUser = userDAO.getById(user.getMaTaiKhoan());
            if (persistedUser == null) {
                AppLog.warn(UserService.class, "Khong tim thay tai khoan de doi mat khau: " + user.getMaTaiKhoan());
                return false;
            }

            if (!PasswordUtil.verify(oldPassword, persistedUser.getMatKhau())) {
                return false;
            }

            String hashedNewPassword = PasswordUtil.md5(newPassword);
            boolean changed = userDAO.changePassword(user.getMaTaiKhoan(), hashedNewPassword);
            if (changed) {
                user.setMatKhau(hashedNewPassword);
                this.currentUser = user;
            }
            return changed;
        } catch (SQLException e) {
            AppLog.warn(UserService.class, "Khong the doi mat khau", e);
            return false;
        }
    }

    /**
     * Kiểm tra username đã tồn tại chưa
     */
    public boolean isUsernameExists(String username) {
        try {
            return userDAO.isUsernameExists(username);
        } catch (SQLException e) {
            AppLog.warn(UserService.class, "Khong the kiem tra ten dang nhap", e);
            return false;
        }
    }

    /**
     * Tạo tài khoản mới
     */
    public boolean createUser(User user) {
        try {
            if (user.getTenDangNhap() == null || user.getTenDangNhap().trim().isEmpty()) {
                return false;
            }
            if (userDAO.isUsernameExists(user.getTenDangNhap())) {
                return false;
            }
            return userDAO.insert(user);
        } catch (SQLException e) {
            AppLog.warn(UserService.class, "Khong the tao tai khoan moi", e);
            return false;
        }
    }

    /**
     * Lấy tất cả user
     */
    public java.util.List<User> getAllUsers() {
        try {
            return userDAO.getAll();
        } catch (SQLException e) {
            AppLog.warn(UserService.class, "Khong the tai danh sach tai khoan", e);
            return null;
        }
    }
}
