package com.bookstore.dao;

import com.bookstore.database.DatabaseConnection;
import com.bookstore.model.Promotion;

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
 * DAO for Promotion data.
 */
public class PromotionDAO {
    private static final String TABLE_NAME = "Promotion";

    public List<Promotion> getAll() {
        List<Promotion> promotions = new ArrayList<>();
        ensureSchema();
        String sql = "SELECT Id, Name, Type, " + valueColumnExpression()
                + " AS Value, Category, MinimumQuantity, StartDate, EndDate, Status, Description "
                + "FROM Promotion ORDER BY StartDate DESC, Id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                promotions.add(map(rs));
            }
            return promotions;
        } catch (SQLException e) {
            throw new RuntimeException("Không thể lấy danh sách khuyến mãi", e);
        }
    }

    public boolean insert(Promotion promotion) {
        ensureSchema();
        String sql = "INSERT INTO Promotion (Name, Type, " + valueColumnExpression()
                + ", Category, MinimumQuantity, StartDate, EndDate, Status, Description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            fillStatement(ps, promotion);
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        promotion.setId(rs.getInt(1));
                    }
                }
            }
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Không thể thêm khuyến mãi", e);
        }
    }

    public boolean update(Promotion promotion) {
        ensureSchema();
        String sql = "UPDATE Promotion SET Name = ?, Type = ?, " + valueColumnExpression()
                + " = ?, Category = ?, MinimumQuantity = ?, StartDate = ?, EndDate = ?, Status = ?, Description = ? WHERE Id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            fillStatement(ps, promotion);
            ps.setInt(10, promotion.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Không thể cập nhật khuyến mãi", e);
        }
    }

    public boolean delete(int id) {
        ensureSchema();
        String sql = "DELETE FROM Promotion WHERE Id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Không thể xóa khuyến mãi", e);
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
            throw new RuntimeException("Không thể khởi tạo bảng khuyến mãi", e);
        }
    }

    private boolean isEmpty(Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM Promotion");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1) == 0;
        }
    }

    private void seedDefaults(Connection conn) throws SQLException {
        String sql = "INSERT INTO Promotion (Name, Type, " + valueColumnExpression()
                + ", Category, MinimumQuantity, StartDate, EndDate, Status, Description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            insertSeed(ps, "Giảm 10% cuối tuần", "PERCENT_ORDER", 10, null, null,
                    LocalDate.now().minusDays(30), LocalDate.now().plusDays(30), "ACTIVE",
                    "Tự động áp dụng cho đơn hàng hợp lệ.");
            insertSeed(ps, "Khuyến mãi sách Công nghệ", "CATEGORY_PERCENT", 12, "Công nghệ", null,
                    LocalDate.now().minusDays(15), LocalDate.now().plusDays(45), "ACTIVE",
                    "Áp dụng cho toàn bộ sách công nghệ.");
            insertSeed(ps, "Mua 2 tặng 1 sách thiếu nhi", "BUY_X_GET_Y", 1, "Thiếu nhi", 2,
                    LocalDate.now().minusDays(7), LocalDate.now().plusDays(60), "ACTIVE",
                    "Mua 2 tặng 1 cùng dòng phù hợp.");
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
                    CREATE TABLE Promotion (
                        Id INT AUTO_INCREMENT PRIMARY KEY,
                        Name VARCHAR(255) NOT NULL,
                        Type VARCHAR(64) NOT NULL,
                        `Value` DOUBLE NOT NULL,
                        Category VARCHAR(120) NULL,
                        MinimumQuantity INT NULL,
                        StartDate DATE NULL,
                        EndDate DATE NULL,
                        Status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
                        Description CLOB NULL,
                        CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                    """;
        }

        return """
                CREATE TABLE Promotion (
                    Id INT IDENTITY(1,1) PRIMARY KEY,
                    Name NVARCHAR(255) NOT NULL,
                    Type NVARCHAR(64) NOT NULL,
                    [Value] FLOAT NOT NULL,
                    Category NVARCHAR(120) NULL,
                    MinimumQuantity INT NULL,
                    StartDate DATE NULL,
                    EndDate DATE NULL,
                    Status NVARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
                    Description NVARCHAR(MAX) NULL,
                    CreatedAt DATETIME2 DEFAULT CURRENT_TIMESTAMP
                )
                """;
    }

    private String valueColumnExpression() {
        return DatabaseConnection.isUsingEmbeddedDatabase() ? "`Value`" : "[Value]";
    }

    private void insertSeed(PreparedStatement ps, String name, String type, double value, String category,
                            Integer minimumQuantity, LocalDate startDate, LocalDate endDate, String status,
                            String description) throws SQLException {
        ps.setString(1, name);
        ps.setString(2, type);
        ps.setDouble(3, value);
        ps.setString(4, category);
        if (minimumQuantity == null) {
            ps.setNull(5, java.sql.Types.INTEGER);
        } else {
            ps.setInt(5, minimumQuantity);
        }
        ps.setDate(6, startDate == null ? null : Date.valueOf(startDate));
        ps.setDate(7, endDate == null ? null : Date.valueOf(endDate));
        ps.setString(8, status);
        ps.setString(9, description);
        ps.executeUpdate();
    }

    private void fillStatement(PreparedStatement ps, Promotion promotion) throws SQLException {
        ps.setString(1, promotion.getName());
        ps.setString(2, promotion.getType());
        ps.setDouble(3, promotion.getValue());
        ps.setString(4, promotion.getCategory());
        if (promotion.getMinimumQuantity() == null) {
            ps.setNull(5, java.sql.Types.INTEGER);
        } else {
            ps.setInt(5, promotion.getMinimumQuantity());
        }
        ps.setDate(6, promotion.getStartDate() == null ? null : Date.valueOf(promotion.getStartDate()));
        ps.setDate(7, promotion.getEndDate() == null ? null : Date.valueOf(promotion.getEndDate()));
        ps.setString(8, promotion.getStatus());
        ps.setString(9, promotion.getDescription());
    }

    private Promotion map(ResultSet rs) throws SQLException {
        Promotion promotion = new Promotion();
        promotion.setId(rs.getInt("Id"));
        promotion.setName(rs.getString("Name"));
        promotion.setType(rs.getString("Type"));
        promotion.setValue(rs.getDouble("Value"));
        promotion.setCategory(rs.getString("Category"));
        int minimumQuantity = rs.getInt("MinimumQuantity");
        promotion.setMinimumQuantity(rs.wasNull() ? null : minimumQuantity);
        Date startDate = rs.getDate("StartDate");
        if (startDate != null) {
            promotion.setStartDate(startDate.toLocalDate());
        }
        Date endDate = rs.getDate("EndDate");
        if (endDate != null) {
            promotion.setEndDate(endDate.toLocalDate());
        }
        promotion.setStatus(rs.getString("Status"));
        promotion.setDescription(rs.getString("Description"));
        return promotion;
    }
}
