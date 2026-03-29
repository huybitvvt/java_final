package com.bookstore.service;

import com.bookstore.database.DatabaseConnection;
import com.bookstore.dao.InvoiceDAO;
import com.bookstore.dao.InvoiceDetailDAO;
import com.bookstore.dao.BookDAO;
import com.bookstore.dao.CustomerDAO;
import com.bookstore.model.Invoice;
import com.bookstore.model.InvoiceDetail;
import com.bookstore.model.Book;
import com.bookstore.model.Customer;
import com.bookstore.util.AppLog;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Invoice Service - Business logic layer for Invoice
 */
public class InvoiceService {

    private final InvoiceDAO invoiceDAO;
    private final InvoiceDetailDAO invoiceDetailDAO;
    private final BookDAO bookDAO;
    private final CustomerDAO customerDAO;

    public InvoiceService() {
        this.invoiceDAO = new InvoiceDAO();
        this.invoiceDetailDAO = new InvoiceDetailDAO();
        this.bookDAO = new BookDAO();
        this.customerDAO = new CustomerDAO();
    }

    /**
     * Lấy tất cả hóa đơn
     */
    public List<Invoice> getAllInvoices() {
        try {
            return invoiceDAO.getAll();
        } catch (SQLException e) {
            AppLog.error(InvoiceService.class, "Error getting all invoices", e);
            return null;
        }
    }

    /**
     * Lấy hóa đơn theo ID
     */
    public Invoice getInvoiceById(int id) {
        try {
            Invoice invoice = invoiceDAO.getById(id);
            if (invoice != null) {
                loadInvoiceDetails(invoice);
            }
            return invoice;
        } catch (SQLException e) {
            AppLog.error(InvoiceService.class, "Error getting invoice by id: " + id, e);
            return null;
        }
    }

    /**
     * Tạo hóa đơn mới (có transaction)
     */
    public boolean createInvoice(Invoice invoice, List<InvoiceDetail> details) {
        if (invoice == null || details == null || details.isEmpty()) {
            AppLog.warn(InvoiceService.class, "Du lieu hoa don khong hop le hoac khong co san pham");
            return false;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            String invoiceCode = invoiceDAO.generateInvoiceCode(conn);
            invoice.setMaHoaDonString(invoiceCode);
            invoice.setNgayLap(new Date());
            invoice.setTrangThai("Đã thanh toán");

            boolean success = invoiceDAO.insert(conn, invoice);
            if (!success || invoice.getMaHoaDon() == 0) {
                AppLog.warn(InvoiceService.class, "Khong the tao hoa don hoac lay ID vua sinh");
                conn.rollback();
                return false;
            }

            for (InvoiceDetail detail : details) {
                if (detail == null || detail.getMaSach() <= 0 || detail.getSoLuong() <= 0) {
                    AppLog.warn(InvoiceService.class, "Chi tiet hoa don khong hop le");
                    conn.rollback();
                    return false;
                }

                detail.setMaHoaDon(invoice.getMaHoaDon());
                detail.tinhThanhTien();

                success = invoiceDetailDAO.insert(conn, detail);
                if (!success) {
                    AppLog.warn(InvoiceService.class,
                            "Khong the luu chi tiet hoa don cho sach " + detail.getMaSach());
                    conn.rollback();
                    return false;
                }

                success = bookDAO.updateQuantity(conn, detail.getMaSach(), -detail.getSoLuong());
                if (!success) {
                    AppLog.warn(InvoiceService.class,
                            "So luong ton kho khong du cho sach " + detail.getMaSach());
                    conn.rollback();
                    return false;
                }
            }

            if (invoice.getMaKhachHang() > 0) {
                int points = (int) (invoice.getThanhToan() / 1000);
                success = customerDAO.updatePoints(conn, invoice.getMaKhachHang(), points);
                if (!success) {
                    AppLog.warn(InvoiceService.class,
                            "Khong the cap nhat diem tich luy cho khach hang " + invoice.getMaKhachHang());
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            AppLog.error(InvoiceService.class, "Loi SQL khi tao hoa don", e);
            return false;
        } catch (RuntimeException e) {
            AppLog.error(InvoiceService.class, "Loi nghiep vu khi tao hoa don", e);
            return false;
        }
    }

    /**
     * Cập nhật hóa đơn
     */
    public boolean updateInvoice(Invoice invoice) {
        try {
            return invoiceDAO.update(invoice);
        } catch (SQLException e) {
            AppLog.error(InvoiceService.class, "Error updating invoice", e);
            return false;
        }
    }

    /**
     * Xóa hóa đơn (hoàn lại số lượng sách)
     */
    public boolean deleteInvoice(int id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            List<InvoiceDetail> details = invoiceDetailDAO.getByInvoiceId(conn, id);

            for (InvoiceDetail detail : details) {
                boolean success = bookDAO.updateQuantity(conn, detail.getMaSach(), detail.getSoLuong());
                if (!success) {
                    AppLog.warn(InvoiceService.class,
                            "Khong the hoan lai ton kho cho sach " + detail.getMaSach());
                    conn.rollback();
                    return false;
                }
            }

            invoiceDetailDAO.deleteByInvoiceId(conn, id);

            boolean deleted = invoiceDAO.delete(conn, id);
            if (!deleted) {
                conn.rollback();
                return false;
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            AppLog.error(InvoiceService.class, "Error deleting invoice: " + id, e);
            return false;
        }
    }

    /**
     * Tìm kiếm hóa đơn
     */
    public List<Invoice> searchInvoices(String keyword) {
        try {
            return invoiceDAO.search(keyword);
        } catch (SQLException e) {
            AppLog.error(InvoiceService.class, "Error searching invoices", e);
            return null;
        }
    }

    /**
     * Lấy hóa đơn theo ngày
     */
    public List<Invoice> getInvoicesByDateRange(Date startDate, Date endDate) {
        try {
            return invoiceDAO.getByDateRange(startDate, endDate);
        } catch (SQLException e) {
            AppLog.error(InvoiceService.class, "Error getting invoices by date range", e);
            return null;
        }
    }

    /**
     * Lấy hóa đơn theo khách hàng
     */
    public List<Invoice> getInvoicesByCustomer(int customerId) {
        try {
            return invoiceDAO.getByCustomer(customerId);
        } catch (SQLException e) {
            AppLog.error(InvoiceService.class, "Error getting invoices by customer: " + customerId, e);
            return null;
        }
    }

    /**
     * Lấy chi tiết hóa đơn
     */
    public List<InvoiceDetail> getInvoiceDetails(int invoiceId) {
        try {
            return invoiceDetailDAO.getByInvoiceId(invoiceId);
        } catch (SQLException e) {
            AppLog.error(InvoiceService.class, "Error getting invoice details", e);
            return null;
        }
    }

    /**
     * Tính doanh thu theo ngày
     */
    public double getRevenueByDate(Date date) {
        try {
            return invoiceDAO.getRevenueByDate(date);
        } catch (SQLException e) {
            AppLog.error(InvoiceService.class, "Error getting revenue by date", e);
            return 0;
        }
    }

    /**
     * Tính doanh thu trong khoảng thời gian
     */
    public double getRevenueByDateRange(Date startDate, Date endDate) {
        try {
            return invoiceDAO.getRevenueByDateRange(startDate, endDate);
        } catch (SQLException e) {
            AppLog.error(InvoiceService.class, "Error getting revenue by date range", e);
            return 0;
        }
    }

    /**
     * Tạo mã hóa đơn mới
     */
    public String generateInvoiceCode() {
        try {
            return invoiceDAO.generateInvoiceCode();
        } catch (SQLException e) {
            AppLog.error(InvoiceService.class, "Error generating invoice code", e);
            return "HD00001";
        }
    }

    /**
     * Load chi tiết và thông tin liên quan cho hóa đơn
     */
    private void loadInvoiceDetails(Invoice invoice) {
        try {
            // Load invoice details
            List<InvoiceDetail> details = invoiceDetailDAO.getByInvoiceId(invoice.getMaHoaDon());
            invoice.setChiTietHoaDon(details);

            // Load customer
            if (invoice.getMaKhachHang() > 0) {
                invoice.setKhachHang(customerDAO.getById(invoice.getMaKhachHang()));
            }
        } catch (SQLException e) {
            AppLog.error(InvoiceService.class, "Error loading invoice details", e);
        }
    }

    /**
     * Đếm tổng số hóa đơn
     */
    public int countInvoices() {
        try {
            return invoiceDAO.count();
        } catch (SQLException e) {
            AppLog.error(InvoiceService.class, "Error counting invoices", e);
            return 0;
        }
    }
}
