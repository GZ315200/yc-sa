package com.unistack.tamboo.commons.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;


/**
 * @author Gyges Zean
 * @date 2018/4/25
 * this method create for jdbc-sqlite, supply some select , insert methods
 */
public class JdbcHelper implements AutoCloseable {

    public static  Logger log = LoggerFactory.getLogger(JdbcHelper.class);


    public  Connection c;

    public  Statement s;

    public JdbcHelper(Connection c, Statement s) {
        this.c = c;
        this.s = s;
    }

    public static class SqliteJdbcBuilder {


        private static Connection c;

        private static Statement s;

        private Properties newProperties = new Properties();

        /**
         * sqlite jdbc settings
         *
         * @param k key
         * @param v value
         * @return
         */
        public SqliteJdbcBuilder settings(String k, String v) {
            newProperties.put(k, v); //
            return this;
        }

        public SqliteJdbcBuilder setClassName() {
            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException e) {
                log.error("driver class not found", e);
            }
            return this;
        }

        public SqliteJdbcBuilder setClassName(String className) {
            try {
                Class.forName(className);
            } catch (ClassNotFoundException e) {
                log.error("driver class not found", e);
            }
            return this;
        }

        public SqliteJdbcBuilder setConnection(String url) {
            try {
                c = DriverManager.getConnection(url, newProperties);
            } catch (SQLException e) {
                log.error("Failed to create jdbc connection", e);
            }
            return this;
        }

        public SqliteJdbcBuilder setConnection(String url, String user, String password) {
            try {
                c = DriverManager.getConnection(url, user, password);
            } catch (SQLException e) {
                log.error("Failed to create jdbc connection", e);
            }
            return this;
        }


        public SqliteJdbcBuilder setAutoCommit(boolean isAutoCommit) {
            try {
                c.setAutoCommit(isAutoCommit);
            } catch (SQLException e) {
                log.error("Failed to set auto commit.");
            }
            return this;
        }

        public SqliteJdbcBuilder setTransactionIsolation(int level) {
            try {
                c.setTransactionIsolation(level);
            } catch (SQLException e) {
                log.error("Failed to set transactionIsolation.");
            }
            return this;
        }

        public SqliteJdbcBuilder createStatement() {
            try {
                s = c.createStatement();
            } catch (SQLException e) {
                log.error("Failed to create statement", e);
            }
            return this;
        }

        public JdbcHelper build() {
            return new JdbcHelper(c, s);
        }

    }

    public static SqliteJdbcBuilder define() {
        return new SqliteJdbcBuilder();
    }


    /**
     * supply the table name to create the table in sqlite db.
     *
     * @param table
     * @return
     */
    public boolean createTable(String table) {
        try {
            s.executeUpdate("CREATE TABLE IF NOT EXISTS `" + space(table) + "`" +
                    "(`topic` TEXT NOT NULL, " +
                    "`offset` BIGINT NOT NULL, " +
                    "`timestamp` BITINT NOT NULL" +
                    ")");
            c.commit();
        } catch (SQLException e) {
            log.error("Failed to create the table", e);
            return false;
        }
        return true;
    }


    private static String space(String s) {
        return " " + s + " ";
    }


    /**
     * Supply the auto close the sql connection & statement method.
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        try {
            c.close();
        } catch (SQLException e) {
            log.error("Failed to auto close connection.");
        }
        try {
            s.close();
        } catch (SQLException e) {
            log.error("Failed to auto close statement.");
        }
    }

    public static void main(String[] args) {
        JdbcHelper j = JdbcHelper.define()
                .setClassName("org.sqlite.JDBC")
                .settings("shared_cache", "true")
                .setConnection("jdbc:sqlite:/Users/mazean/project/tamboo-sa/data/test.db")
                .setAutoCommit(false)
                .setTransactionIsolation(1)
                .createStatement()
                .build();

        try {
            j.createTable("test");
            long timestamp = System.currentTimeMillis();
            j.s.executeUpdate("insert into test VALUES ('1','2','3','4'," + "'" + timestamp + "'" + ")");
            ResultSet rs = j.s.executeQuery("select * from test");
            System.out.println(rs.toString());
            System.out.println(rs.getString("offset"));
            System.out.println(rs.getString("metadata"));
            System.out.println(rs.getString("timestamp"));
            j.c.commit();
//            j.s.executeUpdate("select * from test");
//            System.out.println();
        } catch (SQLException e) {
            log.error("failed", e);
        }
    }

}
