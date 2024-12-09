package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class MySQLTestBase {
    public static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @BeforeEach
    public void setUp() throws Exception {
        mySQLContainer.start();

        String jdbcUrl = mySQLContainer.getJdbcUrl();
        try (Connection connection = DriverManager.getConnection(jdbcUrl, "testuser", "testpass");
             Statement statement = connection.createStatement()) {

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS `groups` (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL
                    );
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS `users` (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        email VARCHAR(255) NOT NULL
                    );
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS `user_groups` (
                        user_id INT NOT NULL,
                        group_id INT NOT NULL,
                        PRIMARY KEY (user_id, group_id),
                        FOREIGN KEY (user_id) REFERENCES `users`(id),
                        FOREIGN KEY (group_id) REFERENCES `groups`(id)
                    );
                    """);
        }
    }

    @AfterEach
    public void tearDown() {
        mySQLContainer.stop();
    }

    @Test
    void testMySQLContainerRunning() {
        assert mySQLContainer.isRunning();
    }
}
