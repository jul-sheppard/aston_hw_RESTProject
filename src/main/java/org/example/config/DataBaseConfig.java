package org.example.config;

import org.example.util.ConfigUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConfig {
    private static final String URL = ConfigUtil.getProperty("db.url");
    private static final String USER = ConfigUtil.getProperty("db.user");
    private static final String PASSWORD = ConfigUtil.getProperty("db.password");
    private static final String DRIVER = ConfigUtil.getProperty("db.driver");

    static {
        try {
            System.out.println("Attempting to load MySQL driver: " + DRIVER);
            Class.forName(DRIVER); // Загрузка драйвера
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load database driver.", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        System.out.println("Connecting to database: " + URL);
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
