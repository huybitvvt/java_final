package com.bookstore.dao;

import com.bookstore.database.DatabaseConnection;
import com.bookstore.model.Invoice;
import com.bookstore.model.TopBookSale;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Invoice DAO - Data Access Object for Invoice entity
 */
public class InvoiceDAO implements IBaseDAO<Invoice> {
    private static final String PAID_STATUS = "Đã thanh toán";

    private static final String INVOICE_SELECT = """
            SELECT hd.*, kh.HoTen AS TenKhachHang, nv.HoTen AS TenNhanVien
            FROM HoaDon hd
            LEFT JOIN KhachHang kh ON kh.MaKhachHang = hd.MaKhachHang
            LEFT JOIN NhanVien nv ON nv.MaNhanVien = hd.MaNhanVien
            """;

    @Override
    public List<Invoice> getAll() throws SQLException {
        List<Invoice> invoices = new ArrayList<>();
        String sql = INVOICE_SELECT + " ORDER BY hd.MaHoaDon DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                invoices.add(mapResultSetToInvoice(rs));
            }
        }
        return invoices;
    }

    @Override
    public Invoice getById(int id) throws SQLException {
        String sql = INVOICE_SELECT + " WHERE hd.MaHoaDon = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToInvoice(rs);
                }
            }
        }
        return null;
    }

    @Override
    public boolean insert(Invoice invoice) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return insert(conn, invoice);
        }
    }

    public boolean insert(Connection conn, Invoice invoice) throws SQLException {
        String sql = "INSERT INTO HoaDon (MaHoaDonString, MaKhachHang, MaNhanVien, NgayLap, TongTien, GiamGia, ThanhToan, PhuongThucThanhToan, GhiChu, TrangThai) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, invoice.getMaHoaDonString());
            if (invoice.getMaKhachHang() > 0) {
                ps.setInt(2, invoice.getMaKhachHang());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setInt(3, invoice.getMaNhanVien());
            ps.setTimestamp(4, invoice.getNgayLap() != null ? new Timestamp(invoice.getNgayLap().getTime()) : new Timestamp(System.currentTimeMillis()));
            ps.setDouble(5, invoice.getTongTien());
            ps.setDouble(6, invoice.getGiamGia());
            ps.setDouble(7, invoice.getThanhToan());
            ps.setString(8, invoice.getPhuongThucThanhToan());
            ps.setString(9, invoice.getGhiChu());
            ps.setString(10, invoice.getTrangThai());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        invoice.setMaHoaDon(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean update(Invoice invoice) throws SQLException {
        String sql = "UPDATE HoaDon SET MaHoaDonString = ?, MaKhachHang = ?, MaNhanVien = ?, NgayLap = ?, " +
                     "TongTien = ?, GiamGia = ?, ThanhToan = ?, PhuongThucThanhToan = ?, GhiChu = ?, TrangThai = ?, NgayCapNhat = CURRENT_TIMESTAMP " +
                     "WHERE MaHoaDon = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, invoice.getMaHoaDonString());
            if (invoice.getMaKhachHang() > 0) {
                ps.setInt(2, invoice.getMaKhachHang());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setInt(3, invoice.getMaNhanVien());
            ps.setTimestamp(4, invoice.getNgayLap() != null ? new Timestamp(invoice.getNgayLap().getTime()) : new Timestamp(System.currentTimeMillis()));
            ps.setDouble(5, invoice.getTongTien());
            ps.setDouble(6, invoice.getGiamGia());
            ps.setDouble(7, invoice.getThanhToan());
            ps.setString(8, invoice.getPhuongThucThanhToan());
            ps.setString(9, invoice.getGhiChu());
            ps.setString(10, invoice.getTrangThai());
            ps.setInt(11, invoice.getMaHoaDon());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return delete(conn, id);
        }
    }

    public boolean delete(Connection conn, int id) throws SQLException {
        String sql = "DELETE FROM HoaDon WHERE MaHoaDon = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<Invoice> search(String keyword) throws SQLException {
        List<Invoice> invoices = new ArrayList<>();
        String sql = INVOICE_SELECT + """
                 WHERE hd.MaHoaDonString LIKE ?
                    OR COALESCE(kh.HoTen, '') LIKE ?
                    OR COALESCE(nv.HoTen, '') LIKE ?
                    OR COALESCE(hd.GhiChu, '') LIKE ?
                 ORDER BY hd.MaHoaDon DESC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ps.setString(4, searchPattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    invoices.add(mapResultSetToInvoice(rs));
                }
            }
        }
        return invoices;
    }

    @Override
    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM HoaDon";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Lấy hóa đơn theo ngày
     */
    public List<Invoice> getByDateRange(Date startDate, Date endDate) throws SQLException {
        List<Invoice> invoices = new ArrayList<>();
        String sql = INVOICE_SELECT + " WHERE hd.NgayLap BETWEEN ? AND ? ORDER BY hd.NgayLap DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, new Timestamp(startDate.getTime()));
            ps.setTimestamp(2, new Timestamp(endDate.getTime()));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    invoices.add(mapResultSetToInvoice(rs));
                }
            }
        }
        return invoices;
    }

    /**
     * Lấy hóa đơn theo khách hàng
     */
    public List<Invoice> getByCustomer(int customerId) throws SQLException {
        List<Invoice> invoices = new ArrayList<>();
        String sql = INVOICE_SELECT + " WHERE hd.MaKhachHang = ? ORDER BY hd.NgayLap DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    invoices.add(mapResultSetToInvoice(rs));
                }
            }
        }
        return invoices;
    }

    /**
     * Tính tổng doanh thu theo ngày
     */
    public double getRevenueByDate(Date date) throws SQLException {
        String sql = "SELECT " + sumOrZero("ThanhToan") + " FROM HoaDon WHERE CAST(NgayLap AS DATE) = ? AND TrangThai = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, new java.sql.Date(date.getTime()));
            ps.setString(2, PAID_STATUS);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }
        return 0;
    }

    /**
     * Tính tổng doanh thu trong khoảng thời gian
     */
    public double getRevenueByDateRange(Date startDate, Date endDate) throws SQLException {
        String sql = "SELECT " + sumOrZero("ThanhToan") + " FROM HoaDon WHERE NgayLap BETWEEN ? AND ? AND TrangThai = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, new Timestamp(startDate.getTime()));
            ps.setTimestamp(2, new Timestamp(endDate.getTime()));
            ps.setString(3, PAID_STATUS);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }
        return 0;
    }

    /**
     * Tạo mã hóa đơn tự động
     */
    public String generateInvoiceCode() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return generateInvoiceCode(conn);
        }
    }

    public String generateInvoiceCode(Connection conn) throws SQLException {
        String sql = DatabaseConnection.isUsingEmbeddedDatabase()
                ? "SELECT MaHoaDonString FROM HoaDon ORDER BY MaHoaDon DESC LIMIT 1"
                : "SELECT TOP 1 MaHoaDonString FROM HoaDon ORDER BY MaHoaDon DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String lastCode = rs.getString("MaHoaDonString");
                int number = Integer.parseInt(lastCode.substring(2));
                return String.format("HD%05d", number + 1);
            }
        }
        return "HD00001";
    }

    public Map<String, Double> getRevenueLastDays(int days) throws SQLException {
        Map<String, Double> revenueMap = new LinkedHashMap<>();
        String sql = DatabaseConnection.isUsingEmbeddedDatabase()
                ? """
                SELECT FORMATDATETIME(NgayLap, 'dd/MM') AS Label, COALESCE(SUM(ThanhToan), 0) AS Revenue
                FROM HoaDon
                WHERE NgayLap >= DATEADD('DAY', ?, CURRENT_TIMESTAMP)
                  AND TrangThai = ?
                GROUP BY CAST(NgayLap AS DATE), FORMATDATETIME(NgayLap, 'dd/MM')
                ORDER BY CAST(NgayLap AS DATE)
                """
                : """
                SELECT FORMAT(NgayLap, 'dd/MM') AS Label, ISNULL(SUM(ThanhToan), 0) AS Revenue
                FROM HoaDon
                WHERE NgayLap >= DATEADD(DAY, -?, GETDATE())
                  AND TrangThai = ?
                GROUP BY CAST(NgayLap AS DATE), FORMAT(NgayLap, 'dd/MM')
                ORDER BY CAST(NgayLap AS DATE)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, DatabaseConnection.isUsingEmbeddedDatabase() ? -(days - 1) : days - 1);
            ps.setString(2, PAID_STATUS);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    revenueMap.put(rs.getString("Label"), rs.getDouble("Revenue"));
                }
            }
        }
        return revenueMap;
    }

    public Map<String, Double> getRevenueLastMonths(int months) throws SQLException {
        Map<String, Double> revenueMap = new LinkedHashMap<>();
        String sql = DatabaseConnection.isUsingEmbeddedDatabase()
                ? """
                SELECT FORMATDATETIME(NgayLap, 'MM/yyyy') AS Label, COALESCE(SUM(ThanhToan), 0) AS Revenue
                FROM HoaDon
                WHERE NgayLap >= DATEADD('MONTH', ?, CURRENT_TIMESTAMP)
                  AND TrangThai = ?
                GROUP BY YEAR(NgayLap), MONTH(NgayLap), FORMATDATETIME(NgayLap, 'MM/yyyy')
                ORDER BY YEAR(NgayLap), MONTH(NgayLap)
                """
                : """
                SELECT FORMAT(NgayLap, 'MM/yyyy') AS Label, ISNULL(SUM(ThanhToan), 0) AS Revenue
                FROM HoaDon
                WHERE NgayLap >= DATEADD(MONTH, -?, GETDATE())
                  AND TrangThai = ?
                GROUP BY YEAR(NgayLap), MONTH(NgayLap), FORMAT(NgayLap, 'MM/yyyy')
                ORDER BY YEAR(NgayLap), MONTH(NgayLap)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, DatabaseConnection.isUsingEmbeddedDatabase() ? -(months - 1) : months - 1);
            ps.setString(2, PAID_STATUS);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    revenueMap.put(rs.getString("Label"), rs.getDouble("Revenue"));
                }
            }
        }
        return revenueMap;
    }

    public List<TopBookSale> getTopSellingBooks(int limit) throws SQLException {
        List<TopBookSale> topBooks = new ArrayList<>();
        String sql = DatabaseConnection.isUsingEmbeddedDatabase()
                ? """
                SELECT s.TenSach, SUM(ct.SoLuong) AS Quantity, SUM(ct.ThanhTien) AS Revenue
                FROM ChiTietHoaDon ct
                JOIN Sach s ON s.MaSach = ct.MaSach
                JOIN HoaDon hd ON hd.MaHoaDon = ct.MaHoaDon
                WHERE hd.TrangThai = ?
                GROUP BY s.MaSach, s.TenSach
                ORDER BY Quantity DESC, Revenue DESC
                LIMIT ?
                """
                : """
                SELECT s.TenSach, SUM(ct.SoLuong) AS Quantity, SUM(ct.ThanhTien) AS Revenue
                FROM ChiTietHoaDon ct
                JOIN Sach s ON s.MaSach = ct.MaSach
                JOIN HoaDon hd ON hd.MaHoaDon = ct.MaHoaDon
                WHERE hd.TrangThai = ?
                GROUP BY s.MaSach, s.TenSach
                ORDER BY Quantity DESC, Revenue DESC
                OFFSET 0 ROWS FETCH NEXT ? ROWS ONLY
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, PAID_STATUS);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    topBooks.add(new TopBookSale(
                            rs.getString("TenSach"),
                            rs.getInt("Quantity"),
                            rs.getDouble("Revenue")
                    ));
                }
            }
        }
        return topBooks;
    }

    public Map<String, Double> getRevenueByCategory() throws SQLException {
        Map<String, Double> revenueMap = new LinkedHashMap<>();
        String sql = """
                SELECT COALESCE(s.TheLoai, 'Khác') AS Category, SUM(ct.ThanhTien) AS Revenue
                FROM ChiTietHoaDon ct
                JOIN Sach s ON s.MaSach = ct.MaSach
                JOIN HoaDon hd ON hd.MaHoaDon = ct.MaHoaDon
                WHERE hd.TrangThai = ?
                GROUP BY s.TheLoai
                ORDER BY Revenue DESC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, PAID_STATUS);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    revenueMap.put(rs.getString("Category"), rs.getDouble("Revenue"));
                }
            }
        }
        return revenueMap;
    }

    public double getCustomerTotalSpent(int customerId) throws SQLException {
        String sql = """
                SELECT %s
                FROM HoaDon
                WHERE MaKhachHang = ?
                  AND TrangThai = ?
                """.formatted(sumOrZero("ThanhToan"));

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ps.setString(2, PAID_STATUS);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }
        return 0;
    }

    /**
     * Map ResultSet to Invoice object
     */
    private Invoice mapResultSetToInvoice(ResultSet rs) throws SQLException {
        Invoice invoice = new Invoice();
        invoice.setMaHoaDon(rs.getInt("MaHoaDon"));
        invoice.setMaHoaDonString(rs.getString("MaHoaDonString"));
        invoice.setMaKhachHang(rs.getInt("MaKhachHang"));
        invoice.setMaNhanVien(rs.getInt("MaNhanVien"));

        Timestamp ngayLap = rs.getTimestamp("NgayLap");
        if (ngayLap != null) {
            invoice.setNgayLap(ngayLap);
        }

        invoice.setTongTien(rs.getDouble("TongTien"));
        invoice.setGiamGia(rs.getDouble("GiamGia"));
        invoice.setThanhToan(rs.getDouble("ThanhToan"));
        invoice.setPhuongThucThanhToan(rs.getString("PhuongThucThanhToan"));
        invoice.setGhiChu(rs.getString("GhiChu"));
        invoice.setTrangThai(rs.getString("TrangThai"));

        Timestamp ngayTao = rs.getTimestamp("NgayTao");
        if (ngayTao != null) {
            invoice.setNgayTao(ngayTao);
        }

        Timestamp ngayCapNhat = rs.getTimestamp("NgayCapNhat");
        if (ngayCapNhat != null) {
            invoice.setNgayCapNhat(ngayCapNhat);
        }

        String tenKhachHang = rs.getString("TenKhachHang");
        if (tenKhachHang != null && !tenKhachHang.isBlank()) {
            com.bookstore.model.Customer customer = new com.bookstore.model.Customer();
            customer.setMaKhachHang(invoice.getMaKhachHang());
            customer.setHoTen(tenKhachHang);
            invoice.setKhachHang(customer);
        }

        String tenNhanVien = rs.getString("TenNhanVien");
        if (tenNhanVien != null && !tenNhanVien.isBlank()) {
            com.bookstore.model.Employee employee = new com.bookstore.model.Employee();
            employee.setMaNhanVien(invoice.getMaNhanVien());
            employee.setHoTen(tenNhanVien);
            invoice.setNhanVien(employee);
        }

        return invoice;
    }

    private String sumOrZero(String column) {
        return (DatabaseConnection.isUsingEmbeddedDatabase() ? "COALESCE" : "ISNULL")
                + "(SUM(" + column + "), 0)";
    }
}
