package com.bookstore.service;

import com.bookstore.database.DatabaseConnection;
import com.bookstore.dao.ImportOrderDAO;
import com.bookstore.dao.ImportDetailDAO;
import com.bookstore.dao.BookDAO;
import com.bookstore.model.ImportOrder;
import com.bookstore.model.ImportDetail;
import com.bookstore.util.AppLog;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Import Service - Business logic layer for Import Orders
 */
public class ImportService {

    private final ImportOrderDAO importOrderDAO;
    private final ImportDetailDAO importDetailDAO;
    private final BookDAO bookDAO;

    public ImportService() {
        this.importOrderDAO = new ImportOrderDAO();
        this.importDetailDAO = new ImportDetailDAO();
        this.bookDAO = new BookDAO();
    }

    public List<ImportOrder> getAllImportOrders() {
        try {
            return importOrderDAO.getAll();
        } catch (SQLException e) {
            AppLog.error(ImportService.class, "Error getting all import orders", e);
            return null;
        }
    }

    public ImportOrder getImportOrderById(int id) {
        try {
            return importOrderDAO.getById(id);
        } catch (SQLException e) {
            AppLog.error(ImportService.class, "Error getting import order by id: " + id, e);
            return null;
        }
    }

    public boolean createImportOrder(ImportOrder order, List<ImportDetail> details) {
        if (order == null || details == null || details.isEmpty()) {
            AppLog.warn(ImportService.class, "Du lieu phieu nhap khong hop le hoac khong co sach");
            return false;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            String importCode = importOrderDAO.generateImportCode(conn);
            order.setMaDonNhapString(importCode);
            order.setNgayNhap(new Date());
            order.setTrangThai("Đã nhập");

            boolean success = importOrderDAO.insert(conn, order);
            if (!success) {
                conn.rollback();
                return false;
            }

            for (ImportDetail detail : details) {
                if (detail == null || detail.getMaSach() <= 0 || detail.getSoLuong() <= 0) {
                    AppLog.warn(ImportService.class, "Chi tiet phieu nhap khong hop le");
                    conn.rollback();
                    return false;
                }

                detail.setMaDonNhap(order.getMaDonNhap());
                detail.tinhThanhTien();

                success = importDetailDAO.insert(conn, detail);
                if (!success) {
                    conn.rollback();
                    return false;
                }

                success = bookDAO.updateQuantity(conn, detail.getMaSach(), detail.getSoLuong());
                if (!success) {
                    AppLog.warn(ImportService.class,
                            "Khong the cap nhat ton kho khi nhap sach " + detail.getMaSach());
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            AppLog.error(ImportService.class, "Error creating import order", e);
            return false;
        }
    }

    public boolean deleteImportOrder(int id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            List<ImportDetail> details = importDetailDAO.getByImportId(conn, id);

            for (ImportDetail detail : details) {
                boolean success = bookDAO.updateQuantity(conn, detail.getMaSach(), -detail.getSoLuong());
                if (!success) {
                    AppLog.warn(ImportService.class,
                            "Khong the hoan tac ton kho cho sach " + detail.getMaSach());
                    conn.rollback();
                    return false;
                }
            }

            importDetailDAO.deleteByImportId(conn, id);

            boolean deleted = importOrderDAO.delete(conn, id);
            if (!deleted) {
                conn.rollback();
                return false;
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            AppLog.error(ImportService.class, "Error deleting import order: " + id, e);
            return false;
        }
    }

    public List<ImportDetail> getImportDetails(int importId) {
        try {
            return importDetailDAO.getByImportId(importId);
        } catch (SQLException e) {
            AppLog.error(ImportService.class, "Error getting import details", e);
            return null;
        }
    }

    public String generateImportCode() {
        try {
            return importOrderDAO.generateImportCode();
        } catch (SQLException e) {
            AppLog.error(ImportService.class, "Error generating import code", e);
            return "PN00001";
        }
    }

    public List<ImportOrder> searchImportOrders(String keyword) {
        try {
            return importOrderDAO.search(keyword);
        } catch (SQLException e) {
            AppLog.error(ImportService.class, "Error searching import orders", e);
            return null;
        }
    }
}
