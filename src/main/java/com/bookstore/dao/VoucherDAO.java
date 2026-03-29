package com.bookstore.dao;

import com.bookstore.database.DatabaseConnection;
import com.bookstore.model.Voucher;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * DAO for Voucher data.
 */
public class VoucherDAO {
    private static final String TABLE_NAME = "Voucher";

    public List<Voucher> getAll() {
        List<Voucher> vouchers = new ArrayList<>();
        ensureSchema();
        String sql = "SELECT Id, Code, Type, " + valueColumnExpression()
                + " AS Value, MinimumOrderValue, StartDate, EndDate, UsageLimit, UsedCount, Status "
                + "FROM Voucher ORDER BY StartDate DESC, Id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                vouchers.add(map(rs));
            }
            return vouchers;
        } catch (SQLException e) {
            throw new RuntimeException("Không thể lấy danh sách mã giảm giá", e);
        }
    }

    public boolean insert(Voucher voucher) {
        ensureSchema();
        String sql = "INSERT INTO Voucher (Code, Type, " + valueColumnExpression()
                + ", MinimumOrderValue, StartDate, EndDate, UsageLimit, UsedCount, Status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            fillStatement(ps, voucher);
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        voucher.setId(rs.getInt(1));
                    }
                }
            }
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Không thể thêm mã giảm giá", e);
        }
    }

    public boolean update(Voucher voucher) {
        ensureSchema();
        String sql = "UPDATE Voucher SET Code = ?, Type = ?, " + valueColumnExpression()
                + " = ?, MinimumOrderValue = ?, StartDate = ?, EndDate = ?, UsageLimit = ?, UsedCount = ?, Status = ? WHERE Id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            fillStatement(ps, voucher);
            ps.setInt(10, voucher.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Không thể cập nhật mã giảm giá", e);
        }
    }

    public boolean delete(int id) {
        ensureSchema();
        String sql = "DELETE FROM Voucher WHERE Id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Không thể xóa mã giảm giá", e);
        }
    }

    private void ensureSchema() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (!tableExists(conn, TABLE_NAME)) {
                try (PreparedStatement ps = conn.prepareStatement(createTableSql())) {
                    ps.execute();
                }
            }

            if (isEmpty(conn)) {
                seedDefaults(conn);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Không thể khởi tạo bảng voucher", e);
        }
    }

    private boolean isEmpty(Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM Voucher");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1) == 0;
        }
    }

    private void seedDefaults(Connection conn) throws SQLException {
        String sql = "INSERT INTO Voucher (Code, Type, " + valueColumnExpression()
                + ", MinimumOrderValue, StartDate, EndDate, UsageLimit, UsedCount, Status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            insertSeed(ps, "WELCOME10", "PERCENT", 10, 100000,
                    LocalDate.now().minusDays(60), LocalDate.now().plusDays(180), 0, 0, "ACTIVE");
            insertSeed(ps, "STUDENT20", "PERCENT", 20, 200000,
                    LocalDate.now().minusDays(30), LocalDate.now().plusDays(90), 500, 32, "ACTIVE");
            insertSeed(ps, "SAVE50K", "FIXED", 50000, 350000,
                    LocalDate.now().minusDays(15), LocalDate.now().plusDays(60), 0, 0, "ACTIVE");
        }
    }

    private boolean tableExists(Connection conn, String tableName) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();
        String[] candidates = {tableName, tableName.toUpperCase(Locale.ROOT), tableName.toLowerCase(Locale.ROOT)};
        for (String candidate : candidates) {
            try (ResultSet rs = metaData.getTables(conn.getCatalog(), null, candidate, new String[]{"TABLE"})) {
                if (rs.next()) {
                    return true;
                }
            }
            try (ResultSet rs = metaData.getTables(null, null, candidate, new String[]{"TABLE"})) {
                if (rs.next()) {
                    return true;
                }
            }
        }
        return false;
    }

    private String createTableSql() {
        if (DatabaseConnection.isUsingEmbeddedDatabase()) {
            return """
                    CREATE TABLE Voucher (
                        Id INT AUTO_INCREMENT PRIMARY KEY,
                        Code VARCHAR(64) NOT NULL UNIQUE,
                        Type VARCHAR(32) NOT NULL,
                        `Value` DOUBLE NOT NULL,
                        MinimumOrderValue DOUBLE NOT NULL DEFAULT 0,
                        StartDate DATE NULL,
                        EndDate DATE NULL,
                        UsageLimit INT NOT NULL DEFAULT 0,
                        UsedCount INT NOT NULL DEFAULT 0,
                        Status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
                        CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                    """;
        }

        return """
                CREATE TABLE Voucher (
                    Id INT IDENTITY(1,1) PRIMARY KEY,
                    Code NVARCHAR(64) NOT NULL UNIQUE,
                    Type NVARCHAR(32) NOT NULL,
                    [Value] FLOAT NOT NULL,
                    MinimumOrderValue FLOAT NOT NULL DEFAULT 0,
                    StartDate DATE NULL,
                    EndDate DATE NULL,
                    UsageLimit INT NOT NULL DEFAULT 0,
                    UsedCount INT NOT NULL DEFAULT 0,
                    Status NVARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
                    CreatedAt DATETIME2 DEFAULT CURRENT_TIMESTAMP
                )
                """;
    }

    private String valueColumnExpression() {
        return DatabaseConnection.isUsingEmbeddedDatabase() ? "`Value`" : "[Value]";
    }

    private void insertSeed(PreparedStatement ps, String code, String type, double value, double minimumOrderValue,
                            LocalDate startDate, LocalDate endDate, int usageLimit, int usedCount, String status) throws SQLException {
        ps.setString(1, code);
        ps.setString(2, type);
        ps.setDouble(3, value);
        ps.setDouble(4, minimumOrderValue);
        ps.setDate(5, startDate == null ? null : Date.valueOf(startDate));
        ps.setDate(6, endDate == null ? null : Date.valueOf(endDate));
        ps.setInt(7, usageLimit);
        ps.setInt(8, usedCount);
        ps.setString(9, status);
        ps.executeUpdate();
    }

    private void fillStatement(PreparedStatement ps, Voucher voucher) throws SQLException {
        ps.setString(1, voucher.getCode());
        ps.setString(2, voucher.getType());
        ps.setDouble(3, voucher.getValue());
        ps.setDouble(4, voucher.getMinimumOrderValue());
        ps.setDate(5, voucher.getStartDate() == null ? null : Date.valueOf(voucher.getStartDate()));
        ps.setDate(6, voucher.getEndDate() == null ? null : Date.valueOf(voucher.getEndDate()));
        ps.setInt(7, voucher.getUsageLimit());
        ps.setInt(8, voucher.getUsedCount());
        ps.setString(9, voucher.getStatus());
    }

    private Voucher map(ResultSet rs) throws SQLException {
        Voucher voucher = new Voucher();
        voucher.setId(rs.getInt("Id"));
        voucher.setCode(rs.getString("Code"));
        voucher.setType(rs.getString("Type"));
        voucher.setValue(rs.getDouble("Value"));
        voucher.setMinimumOrderValue(rs.getDouble("MinimumOrderValue"));
        Date startDate = rs.getDate("StartDate");
        if (startDate != null) {
            voucher.setStartDate(startDate.toLocalDate());
        }
        Date endDate = rs.getDate("EndDate");
        if (endDate != null) {
            voucher.setEndDate(endDate.toLocalDate());
        }
        voucher.setUsageLimit(rs.getInt("UsageLimit"));
        voucher.setUsedCount(rs.getInt("UsedCount"));
        voucher.setStatus(rs.getString("Status"));
        return voucher;
    }
}
