package org.example.repository;

import org.example.MySQLTestBase;
import org.example.model.Group;
import org.example.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GroupRepositoryTest extends MySQLTestBase {
    private GroupRepository groupRepository;

    @BeforeEach
    public void setUp() throws Exception {
        groupRepository = new GroupRepository();
    }

    @Test
    public void testCreateGroup() throws Exception {
        Group group = new Group(0, "Test Group");
        boolean created = groupRepository.createGroup(group);
        assertTrue(created);
        assertTrue(group.getId() > 0);
        Group retrievedGroup = groupRepository.getGroupById(group.getId());
        assertNotNull(retrievedGroup);
        assertEquals(group.getName(), retrievedGroup.getName());
    }

    @Test
    public void testGetGroupById() throws Exception {
        Group group = new Group(0, "Test Group");
        groupRepository.createGroup(group);
        Group retrievedGroup = groupRepository.getGroupById(group.getId());
        assertNotNull(retrievedGroup);
        assertEquals(group.getId(), retrievedGroup.getId());
        assertEquals(group.getName(), retrievedGroup.getName());
    }

    @Test
    public void testGetAllGroups() throws Exception {
        Group group1 = new Group(0, "Test Group 1");
        Group group2 = new Group(0, "Test Group 2");
        assertTrue(groupRepository.createGroup(group1), "Failed to create Test Group 1");
        assertTrue(groupRepository.createGroup(group2), "Failed to create Test Group 2");

        List<Group> groups = groupRepository.getAllGroups();
        assertNotNull(groups, "Group list should not be null");
        assertTrue(groups.size() >= 2, "Group list should contain at least 2 groups");

        assertTrue(groups.stream().anyMatch(g -> g.getName().equals("Test Group 1")),
                "Group list should contain 'Test Group 1'");
        assertTrue(groups.stream().anyMatch(g -> g.getName().equals("Test Group 2")),
                "Group list should contain 'Test Group 2'");
    }

    @Test
    public void testGetUsersByGroupId() throws Exception {
        Group group = new Group(0, "Test Group with Users");
        groupRepository.createGroup(group);
        List<User> users = groupRepository.getUsersByGroupId(group.getId());
        assertNotNull(users);
        assertFalse(users.isEmpty());
        for (User user : users) {
            assertTrue(user.getGroups().stream().anyMatch(g -> g.getId() == group.getId()), "Each user should belong to the group");
        }
    }

    @Test
    public void testDeleteGroupById() throws Exception {
        Group group = new Group(0, "Test Group to Delete");
        groupRepository.createGroup(group);
        boolean deleted = groupRepository.deleteGroupById(group.getId());
        assertTrue(deleted);
        Group retrievedGroup = groupRepository.getGroupById(group.getId());
        assertNull(retrievedGroup);
    }

    @Test
    public void testDeleteUserByName() throws Exception {
        Group group = new Group(0, "Test Group to Delete by Name");
        groupRepository.createGroup(group);
        boolean deleted = groupRepository.deleteGroupByName(group.getName());
        assertTrue(deleted);
        Group retrievedGroup = groupRepository.getGroupById(group.getId());
        assertNull(retrievedGroup);
    }

}