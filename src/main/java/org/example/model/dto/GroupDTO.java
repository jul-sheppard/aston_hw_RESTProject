package org.example.model.dto;

import org.example.model.Group;

import java.util.List;

public class GroupDTO {
    private int id;
    private String name;
    private List<UserDTO> users;

    public GroupDTO(int id, String name) {
        this.id = id;
        this.name = name;
        this.users = users;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<UserDTO> getUsers() {
        return users;
    }

    public void setUsers(List<UserDTO> users) {
        this.users = users;
    }

    public static GroupDTO fromGroup(Group group) {
        return new GroupDTO(group.getId(), group.getName());
    }
}
