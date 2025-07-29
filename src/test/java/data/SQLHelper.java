package data;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLHelper {
    private static final Logger logger = LoggerFactory.getLogger(SQLHelper.class);
    private final QueryRunner QUERY_RUNNER;

    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;

    public SQLHelper(String dbUrl, String dbUser, String dbPassword, QueryRunner queryRunner) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.QUERY_RUNNER = queryRunner;
    }

    public SQLHelper(String dbUrl, String dbUser, String dbPassword) {
        this(dbUrl, dbUser, dbPassword, new QueryRunner());
    }

    private <T> T executeQuery(String sql, QueryHandler<T> handler) throws SQLException {
        try (var conn = getConn()) {
            logger.info("Executing SQL: {}", sql);
            return handler.execute(conn, sql);
        } catch (SQLException e) {
            logger.error("Error executing SQL: {}", sql, e);
            throw e;
        }
    }

    public String getVerificationCode() throws SQLException {
        String codeSQL = "SELECT code FROM auth_codes ORDER BY created DESC LIMIT 1";
        return executeQuery(codeSQL, (conn, sql) -> QUERY_RUNNER.query(conn, sql, new ScalarHandler<>()));
    }

    public void cleanDatabase() throws SQLException {
        try (var conn = getConn()) {
            conn.setAutoCommit(false);
            try {
                QUERY_RUNNER.execute(conn, "DELETE FROM auth_codes");
                QUERY_RUNNER.execute(conn, "DELETE FROM card_transactions");
                QUERY_RUNNER.execute(conn, "DELETE FROM cards");
                QUERY_RUNNER.execute(conn, "DELETE FROM users");
                conn.commit();
                logger.info("Database cleaned");
            } catch (SQLException e) {
                logger.error("Error cleaning database. Rolling back transaction.", e);
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            logger.error("Error cleaning database", e);
            throw e;
        }
    }

    public void cleanAuthCodes() throws SQLException {
        String sql = "DELETE FROM auth_codes";
        executeQuery(sql, (conn, sql1) -> {
            try {
                QUERY_RUNNER.execute(conn, sql1);
            } catch (SQLException e) {
                logger.error("Error cleaning auth_codes table", e);
                throw e;
            }
            return null; //execute возвращает void, поэтому нужно вернуть null
        });
        logger.info("auth_codes table cleaned");
    }

    private Connection getConn() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    @FunctionalInterface
    private interface QueryHandler<T> {
        T execute(Connection conn, String sql) throws SQLException;
    }
}
