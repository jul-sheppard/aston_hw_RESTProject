package org.example.service;

import org.example.model.Group;
import org.example.model.User;
import org.example.repository.GroupRepository;

import java.sql.SQLException;
import java.util.List;

public class GroupService {
    private final GroupRepository groupRepository = new GroupRepository();

    //получение группы по id
    public Group getGroupById(int id) throws SQLException {
        return (Group) groupRepository.getUsersByGroupId(id);
    }

    //получение пользователей группы
    public List<User> getUsersByGroupId(int groupId) throws SQLException {
        return groupRepository.getUsersByGroupId(groupId);
    }

    //создание новой группы
    public boolean createGroup(Group group) throws SQLException {
        if (group.getName() == null || group.getName().isEmpty()) {
            throw new SQLException("Group name cannot be empty");
        }
        return groupRepository.createGroup(group);
    }
}
