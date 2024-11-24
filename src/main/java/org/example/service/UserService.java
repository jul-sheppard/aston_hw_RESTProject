package org.example.service;

import org.example.model.Group;
import org.example.model.User;
import org.example.model.dto.GroupDTO;
import org.example.model.dto.UserDTO;
import org.example.repository.UserRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class UserService {
    private final UserRepository userRepository = new UserRepository();

    //получение всех пользователей
    public List<UserDTO> getAllUsers() throws SQLException {
        List<User> users = userRepository.getAllUsers();
        return users.stream()
                .map(UserDTO::fromUser)
                .collect(Collectors.toList());
    }

    //добавление нового пользователя в группу
    public boolean addUserToGroup(UserDTO userDTO, int groupId) throws SQLException {
        if (userDTO.getName() == null || userDTO.getName().isEmpty()) {
            throw new SQLException("Name is empty");
        }
        if (userDTO.getId() <= 0) {
            throw new SQLException("Id is empty");
        }
        if (groupId <= 0) {
            throw new SQLException("GroupId is empty");
        }
        User user = new User(userDTO.getId(), userDTO.getName(), userDTO.getEmail());
        return userRepository.addUserToGroup(user.getId(), groupId);
    }

    //удаление пользователя из группы
    public boolean removeUserFromGroup(UserDTO userDTO) throws SQLException {
        if (userDTO.getName() == null || userDTO.getName().isEmpty()) {
            throw new SQLException("Name is empty");
        }
        if (userDTO.getId() <= 0) {
            throw new SQLException("Id is empty");
        }
        User user = new User(userDTO.getId(), userDTO.getName(), userDTO.getEmail());
        userRepository.removeUserFromGroup(user.getId(), userDTO.getId());
        return true;
    }

    //получение групп, к которым принаджлежит пользватель
    public List<GroupDTO> getGroupsByUserId(int userId) throws SQLException {
        List<Group> groups = userRepository.getGroupsByUserId(userId);
        return groups.stream()
                .map(GroupDTO::fromGroup)
                .collect(Collectors.toList());
    }

    public boolean addUser(UserDTO userDTO) throws SQLException {
        if (userDTO.getName() == null || userDTO.getName().isEmpty()) {
            throw new SQLException("Name is empty");
        }
        return userRepository.addUser(userDTO);
    }
}
