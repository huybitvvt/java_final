package com.bookstore.database;

import com.bookstore.util.AppLog;
import org.apache.commons.dbcp2.BasicDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Database Connection Manager - Sử dụng DBCP2 Connection Pool
 */
public class DatabaseConnection {

    private static BasicDataSource dataSource;
    private static final Properties properties = new Properties();

    // Database configuration
    private static String DB_URL;
    private static String DB_USER;
    private static String DB_PASSWORD;
    private static String DB_DRIVER;
    private static volatile boolean usingEmbeddedDatabase;
    private static String embeddedDatabaseUrl;

    static {
        loadConfiguration();
        initializeDataSource();
    }

    /**
     * Load database configuration from config.properties
     */
    private static void loadConfiguration() {
        try (InputStream input = DatabaseConnection.class.getClassLoader()
                .getResourceAsStream("database.properties")) {

            if (input == null) {
                AppLog.warn(DatabaseConnection.class, "Khong tim thay database.properties, su dung cau hinh mac dinh");
                setDefaultConfiguration();
                return;
            }

            properties.load(input);

            DB_URL = properties.getProperty("db.url",
                    "jdbc:sqlserver://localhost:1433;databaseName=QuanLyCuaHangSach;encrypt=false");
            DB_USER = properties.getProperty("db.username", "sa");
            DB_PASSWORD = properties.getProperty("db.password", "123");
            DB_DRIVER = properties.getProperty("db.driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");

        } catch (IOException e) {
            AppLog.warn(DatabaseConnection.class, "Khong the doc cau hinh database, su dung mac dinh", e);
            setDefaultConfiguration();
        }
    }

    /**
     * Set default database configuration
     */
    private static void setDefaultConfiguration() {
        DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=QuanLyCuaHangSach;encrypt=false";
        DB_USER = "sa";
        DB_PASSWORD = "123";
        DB_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    }

    /**
     * Initialize DBCP2 DataSource with connection pooling
     */
    private static void initializeDataSource() {
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            AppLog.error(DatabaseConnection.class, "Database driver not found: " + e.getMessage(), e);
        }

        BasicDataSource mysqlDataSource = createDataSource(DB_DRIVER, DB_URL, DB_USER, DB_PASSWORD);
        if (canConnect(mysqlDataSource)) {
            dataSource = mysqlDataSource;
            usingEmbeddedDatabase = false;
            AppLog.info(DatabaseConnection.class, "Dang su dung SQL Server datasource");
            return;
        }

        closeQuietly(mysqlDataSource);
        switchToEmbeddedDatabase(null);
    }

    private static BasicDataSource createDataSource(String driver, String url, String username, String password) {
        BasicDataSource source = new BasicDataSource();
        source.setDriverClassName(driver);
        source.setUrl(url);
        source.setUsername(username);
        source.setPassword(password);

        // Connection Pool Configuration
        source.setInitialSize(1);
        source.setMaxTotal(10);
        source.setMaxIdle(5);
        source.setMinIdle(1);
        source.setMaxWaitMillis(10000);
        source.setTestOnBorrow(true);
        source.setTestOnReturn(false);
        source.setTestWhileIdle(true);
        source.setValidationQuery("SELECT 1");
        source.setValidationQueryTimeout(5);
        return source;
    }

    private static boolean canConnect(BasicDataSource source) {
        try (Connection ignored = source.getConnection()) {
            return true;
        } catch (SQLException e) {
            AppLog.warn(DatabaseConnection.class, "Primary database unavailable: " + e.getMessage(), e);
            return false;
        }
    }

    private static synchronized void switchToEmbeddedDatabase(SQLException cause) {
        if (usingEmbeddedDatabase && dataSource != null) {
            return;
        }

        closeQuietly(dataSource);

        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Không tìm thấy H2 database driver để chạy chế độ offline.", e);
        }

        try {
            Path dataDir = Path.of(System.getProperty("user.dir"), ".bookstore-data");
            Files.createDirectories(dataDir);
            embeddedDatabaseUrl = "jdbc:h2:file:" + dataDir.resolve("bookstore").toAbsolutePath()
                    + ";MODE=MySQL;DATABASE_TO_UPPER=FALSE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1";
        } catch (IOException e) {
            throw new IllegalStateException("Không thể khởi tạo thư mục dữ liệu nhúng.", e);
        }

        dataSource = createDataSource("org.h2.Driver", embeddedDatabaseUrl, "sa", "");
        usingEmbeddedDatabase = true;
        initializeEmbeddedSchema();

        if (cause != null) {
            AppLog.warn(DatabaseConnection.class,
                    "Switched to embedded H2 database due to connection error: " + cause.getMessage(), cause);
        } else {
            AppLog.warn(DatabaseConnection.class, "Switched to embedded H2 database because SQL Server is unavailable.");
        }
    }

    private static void initializeEmbeddedSchema() {
        try (Connection conn = dataSource.getConnection()) {
            executeSqlScript(conn, loadEmbeddedScript());
        } catch (SQLException e) {
            throw new IllegalStateException("Không thể khởi tạo schema H2 nhúng.", e);
        }
    }

    private static String loadEmbeddedScript() {
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("database-h2.sql")) {
            if (input == null) {
                throw new IllegalStateException("Không tìm thấy file database-h2.sql");
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Không thể đọc file database-h2.sql", e);
        }
    }

    private static void executeSqlScript(Connection conn, String script) throws SQLException {
        StringBuilder statement = new StringBuilder();
        for (String line : script.split("\\R")) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                continue;
            }
            statement.append(line).append('\n');
            if (trimmed.endsWith(";")) {
                String sql = statement.toString().trim();
                sql = sql.substring(0, sql.length() - 1).trim();
                if (!sql.isEmpty()) {
                    try (Statement ps = conn.createStatement()) {
                        ps.execute(sql);
                    }
                }
                statement.setLength(0);
            }
        }
    }

    private static void closeQuietly(BasicDataSource source) {
        if (source == null) {
            return;
        }
        try {
            source.close();
        } catch (SQLException ignored) {
            // no-op
        }
    }

    /**
     * Get database connection from pool
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            if (!usingEmbeddedDatabase) {
                switchToEmbeddedDatabase(e);
                return dataSource.getConnection();
            }
            throw e;
        }
    }

    /**
     * Close a connection (return to pool)
     * @param connection Connection to close
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                AppLog.warn(DatabaseConnection.class, "Error closing connection: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Test database connection
     * @return true if connection successful
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            AppLog.warn(DatabaseConnection.class, "Database connection test failed: " + e.getMessage(), e);
            return false;
        }
    }

    public static boolean isUsingEmbeddedDatabase() {
        return usingEmbeddedDatabase;
    }

    public static String getActiveDatabaseDescription() {
        return usingEmbeddedDatabase
                ? "H2 embedded (" + embeddedDatabaseUrl + ")"
                : "SQL Server (" + DB_URL + ")";
    }

    /**
     * Get DataSource for advanced operations
     * @return BasicDataSource
     */
    public static BasicDataSource getDataSource() {
        return dataSource;
    }

    /**
     * Shutdown connection pool
     */
    public static void shutdown() {
        try {
            if (dataSource != null) {
                dataSource.close();
            }
        } catch (SQLException e) {
            AppLog.warn(DatabaseConnection.class, "Error shutting down connection pool: " + e.getMessage(), e);
        }
    }
}
