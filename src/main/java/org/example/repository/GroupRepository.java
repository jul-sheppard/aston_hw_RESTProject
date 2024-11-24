package org.example.repository;

import org.example.config.DataBaseConfig;
import org.example.model.Group;
import org.example.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GroupRepository {

    public Group getGroupById(int id) throws SQLException {
        String sql = "select * from group where id = ?";
        try (Connection connection = DataBaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new Group(
                            resultSet.getInt("id"),
                            resultSet.getString("name")
                    );
                }
            }
        }
        return null;
    }

    public List<User> getUsersByGroupId(int groupId) throws SQLException {
        List<User> users = new ArrayList<User>();
        String sql = """
                SELECT u.id, u.name, u.email, g.id AS group_id, g.name AS group_name
                FROM users u
                JOIN user_groups ug ON u.id = ug.user_id
                JOIN `groups` g ON ug.group_id = g.id
                WHERE ug.group_id = ?;
                    """;


        try (Connection connection = DataBaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, groupId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    User user = new User(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("email")
                    );
                    Group group = new Group(
                            resultSet.getInt("group_id"),
                            resultSet.getString("group_name")
                    );
                    user.setGroups(List.of(group));
                    users.add(user);
                }
            }
        }
        for (User user : users) {
            System.out.println(user.toString());
        }
        return users;
    }

    public boolean createGroup(Group group) throws SQLException {
        String sql = "insert into groups (name) values (?)";
        try (Connection connection = DataBaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, group.getName());
            int rowsInserted = preparedStatement.executeUpdate();
            return rowsInserted > 0;
        }
    }
}
