package org.example.service;

import org.example.model.Group;
import org.example.model.User;
import org.example.model.dto.GroupDTO;
import org.example.repository.GroupRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class GroupService {
    private final GroupRepository groupRepository = new GroupRepository();

    //получение группы по id
    public Group getGroupById(int id) throws SQLException {
        return groupRepository.getGroupById(id);
    }

    //получение списка всех групп
    public List<GroupDTO> getAllGroups() throws SQLException {
        return groupRepository.getAllGroups().stream()
                .map(GroupDTO::fromGroup)
                .collect(Collectors.toList());
    }

    //получение пользователей группы
    public List<User> getUsersByGroupId(int groupId) throws SQLException {
        return groupRepository.getUsersByGroupId(groupId);
    }

    //создание новой группы
    public boolean createGroup(GroupDTO groupDTO) throws SQLException {
        if (groupDTO.getName() == null || groupDTO.getName().trim().isEmpty()) {
            throw new SQLException("Group name cannot be empty");
        }
        Group group = new Group(0, groupDTO.getName());
        return groupRepository.createGroup(group);
    }

    //удаление группы
    public boolean deleteGroupById(int groupId) throws SQLException {
        if (groupId <= 0) {
            throw new SQLException("Group id cannot be empty");
        }
        return groupRepository.deleteGroupById(groupId);
    }

    public boolean deleteGroupByName(String name) throws SQLException {
        if (name == null || name.isEmpty()) {
            throw new SQLException("Group name cannot be empty");
        }
        return groupRepository.deleteGroupByName(name);
    }
}
