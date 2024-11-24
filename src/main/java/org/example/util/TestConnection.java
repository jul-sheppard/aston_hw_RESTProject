package org.example.util;

import org.example.config.DataBaseConfig;

import java.sql.Connection;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        try (Connection connection = DataBaseConfig.getConnection()) {
            if (connection != null) {
                System.out.println("Connection to MySQL database is successful!");
            }
        } catch (SQLException e) {
            System.out.println("SQLException caught: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
