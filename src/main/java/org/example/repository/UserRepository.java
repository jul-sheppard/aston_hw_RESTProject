package org.example.repository;

import org.example.config.DataBaseConfig;
import org.example.model.Group;
import org.example.model.User;
import org.example.model.dto.UserDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    public List<Group> getGroupsByUserId(int userId) throws SQLException {
        List<Group> groups = new ArrayList<Group>();
        String sql = """
                SELECT g.id, g.name
                FROM user_groups ug
                JOIN `groups` g ON ug.group_id = g.id
                WHERE ug.user_id = ?;
                """;

        try (Connection connection = DataBaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    groups.add(new Group(
                            resultSet.getInt("id"),
                            resultSet.getString("name")
                    ));
                }
            }
        }
        return groups;
    }

    public boolean addUserToGroup(int userId, int groupId) throws SQLException {
        String sql = "INSERT INTO user_groups (user_id, group_id) VALUES (?, ?)";
        try (Connection connection = DataBaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setInt(2, groupId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Failed to add user to group", e);
        }
    }

    public void removeUserFromGroup(int userId, int groupId) throws SQLException {
        String sql = "DELETE FROM groups WHERE id = ?";
        try (Connection connection = DataBaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setInt(2, groupId);
            statement.executeUpdate();
        }
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<User>();
        String sql = "SELECT id, name, email FROM users";

        try (Connection connection = DataBaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                users.add(new User(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("email")
                ));
            }
        }
        return users;
    }

    public boolean addUser(UserDTO userDTO) throws SQLException {
        String sql = "INSERT INTO users (name, email) VALUES (?, ?)";
        boolean rowsAffected = false;
        try (Connection connection = DataBaseConfig.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userDTO.getName());
            statement.setString(2, userDTO.getEmail());
            rowsAffected = statement.executeUpdate() == 0 ? false : true;
        }
        return rowsAffected;
    }
}
