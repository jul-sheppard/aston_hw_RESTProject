package org.example.model;

import java.util.List;

public class Group {
    private int id;
    private String name;
    private List<User> users; // cписок пользователей, входящих в эту группу

    public Group(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Group() {
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

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
