package com.examSystem.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple JDBC Database utility with a thread-safe connection pool.
 * <p>
 * Notes:
 * - This is a lightweight internal pool (not a full-featured library). For production use,
 *   consider using HikariCP or another proven pool implementation.
 * - Configuration can be provided via system properties or environment variables:
 *   - DB_URL (jdbc url)
 *   - DB_USER
 *   - DB_PASS
 *   - DB_POOL_SIZE (integer)
 */
public final class DatabaseUtil {
    private static final Logger LOGGER = Logger.getLogger(DatabaseUtil.class.getName());

    private static final String DEFAULT_DB_URL = "jdbc:mysql://localhost:3306/online_exam_system?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String DEFAULT_DB_USER = "examuser";
    private static final String DEFAULT_DB_PASS = "exampass123";
    private static final int DEFAULT_POOL_SIZE = 10;
    private static final Duration DEFAULT_CONN_TIMEOUT = Duration.ofSeconds(10);

    private static final String DB_URL = firstNonNull(System.getProperty("DB_URL"), System.getenv("DB_URL"), DEFAULT_DB_URL);
    private static final String DB_USER = firstNonNull(System.getProperty("DB_USER"), System.getenv("DB_USER"), DEFAULT_DB_USER);
    private static final String DB_PASS = firstNonNull(System.getProperty("DB_PASS"), System.getenv("DB_PASS"), DEFAULT_DB_PASS);
    private static final int POOL_SIZE = parsePoolSize(firstNonNull(System.getProperty("DB_POOL_SIZE"), System.getenv("DB_POOL_SIZE")));

    // The pool that holds available connections
    private static final BlockingQueue<Connection> POOL = new LinkedBlockingQueue<>();
    private static volatile boolean initialized = false;

    static {
        try {
            init();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize DatabaseUtil pool", e);
            // don't rethrow — leave initialized = false; callers will see errors when requesting connections
        }
    }

    private DatabaseUtil() { /* utility */ }

    private static void init() throws SQLException {
        if (initialized) return;

        try {
            // Load MySQL driver explicitly for older JVMs
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                // driver may already be available on classpath; log and continue
                LOGGER.log(Level.FINE, "MySQL JDBC driver not explicitly found on classpath", e);
            }

            List<Connection> created = new ArrayList<>(POOL_SIZE);
            for (int i = 0; i < POOL_SIZE; i++) {
                Connection c = createNewConnection();
                POOL.offer(c);
                created.add(c);
            }
            initialized = true;
            LOGGER.info(() -> "Initialized connection pool with size=" + POOL_SIZE + " to " + DB_URL);
        } catch (SQLException ex) {
            // Close any created connections on failure
            LOGGER.log(Level.SEVERE, "Error creating initial connections", ex);
            throw ex;
        }
    }

    private static Connection createNewConnection() throws SQLException {
        try {
            Connection c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            c.setAutoCommit(true);
            return c;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Unable to create a new DB connection", e);
            throw e;
        }
    }

    /**
     * Acquire a connection from the pool, waiting up to the default timeout.
     * @return a valid {@link Connection}
     * @throws SQLException if a connection cannot be obtained
     */
    public static Connection getConnection() throws SQLException {
        return getConnection(DEFAULT_CONN_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * Acquire a connection from the pool, waiting up to the provided timeout.
     * @param timeout the timeout
     * @param unit the timeout unit
     * @return a valid {@link Connection}
     * @throws SQLException if no connection is available or an error occurs
     */
    public static Connection getConnection(long timeout, TimeUnit unit) throws SQLException {
        if (!initialized) {
            // try to initialize lazily
            init();
            if (!initialized) throw new SQLException("Connection pool is not initialized");
        }

        try {
            Connection conn = POOL.poll(timeout, unit);
            if (conn == null) {
                throw new SQLException("Timeout waiting for a database connection");
            }

            if (!isConnectionValid(conn)) {
                // try to close and replace
                closeSilently(conn);
                Connection replacement = createNewConnection();
                return replacement;
            }

            return conn;
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new SQLException("Interrupted while waiting for a DB connection", ie);
        }
    }

    /**
     * Return a connection to the pool. If the connection is invalid, it will be closed and replaced.
     */
    public static void releaseConnection(Connection conn) {
        if (conn == null) return;

        try {
            if (!isConnectionValid(conn)) {
                closeSilently(conn);
                try {
                    POOL.offer(createNewConnection());
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Failed to create replacement connection for pool", e);
                }
                return;
            }

            // Reset some state to be safe
            try {
                if (!conn.getAutoCommit()) conn.setAutoCommit(true);
            } catch (SQLException e) {
                LOGGER.log(Level.FINE, "Error resetting connection autocommit", e);
            }

            if (!POOL.offer(conn)) {
                // pool full — close extra connection
                closeSilently(conn);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error while releasing connection back to pool", e);
            closeSilently(conn);
        }
    }

    private static boolean isConnectionValid(Connection conn) {
        try {
            return conn != null && !conn.isClosed() && conn.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

    private static void closeSilently(Connection c) {
        if (c == null) return;
        try { c.close(); } catch (SQLException ignored) {}
    }

    /**
     * Close all pooled connections and clear the pool.
     */
    public static void shutdown() {
        initialized = false;
        List<Connection> drain = new ArrayList<>();
        POOL.drainTo(drain);
        for (Connection c : drain) closeSilently(c);
        LOGGER.info("Database connection pool shut down");
    }

    private static String firstNonNull(String... values) {
        for (String v : values) if (v != null && !v.isEmpty()) return v;
        return null;
    }

    private static int parsePoolSize(String s) {
        if (s == null) return DEFAULT_POOL_SIZE;
        try {
            int v = Integer.parseInt(s);
            return Math.max(1, v);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid DB_POOL_SIZE value: " + s + ", using default: " + DEFAULT_POOL_SIZE, e);
            return DEFAULT_POOL_SIZE;
        }
    }
}
