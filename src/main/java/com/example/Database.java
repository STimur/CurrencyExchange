package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static String url;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void init(String dbPath) {
        url = "jdbc:sqlite:" + dbPath;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }
}
