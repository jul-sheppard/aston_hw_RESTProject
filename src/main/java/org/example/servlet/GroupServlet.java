package org.example.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Group;
import org.example.model.User;
import org.example.model.dto.GroupDTO;
import org.example.repository.GroupRepository;
import org.example.service.GroupService;
import org.example.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/groups/*") // URL для работы с группами
public class GroupServlet extends HttpServlet {

    private final GroupService groupService = new GroupService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String path = req.getPathInfo();
            if (path == null || path.equals("/")) {
                List<GroupDTO> groupDTO = groupService.getAllGroups();
                resp.setStatus(HttpServletResponse.SC_OK);
                JsonUtil.writeJson(resp, groupDTO);
                return;
            }
            String[] paths = path.split("/");
            if (paths.length == 2) {
                String groupIdParam = paths[1];
                int groupId;
                try {
                    groupId = Integer.parseInt(groupIdParam);
                } catch (NumberFormatException e) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    JsonUtil.writeJson(resp, "Invalid Group ID");
                    return;
                }
                Group group = groupService.getGroupById(groupId);
                if (group != null) {
                    GroupDTO groupDTO = GroupDTO.fromGroup(group);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    JsonUtil.writeJson(resp, groupDTO);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    JsonUtil.writeJson(resp, "Group not found");
                }
            } else if (paths.length == 3 && "users".equals(paths[2])) {
                String groupIdParam = paths[1];
                int groupId;
                try {
                    groupId = Integer.parseInt(groupIdParam);
                } catch (NumberFormatException e) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    JsonUtil.writeJson(resp, "Invalid Group ID");
                    return;
                }
                List<User> users = groupService.getUsersByGroupId(groupId);
                if (users != null && !users.isEmpty()) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    JsonUtil.writeJson(resp, users);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    JsonUtil.writeJson(resp, "No users found for this group");
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonUtil.writeJson(resp, "Invalid URL structure");
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonUtil.writeJson(resp, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String name = req.getParameter("name");
            if (name == null || name.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonUtil.writeJson(resp, "Invalid name");
                return;
            }
            GroupDTO groupDTO = new GroupDTO();
            groupDTO.setName(name);
            boolean isCreated = groupService.createGroup(groupDTO);
            if (isCreated) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                JsonUtil.writeJson(resp, "Group created");
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                JsonUtil.writeJson(resp, "Group creation failed");
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonUtil.writeJson(resp, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String groupName = req.getParameter("name");
            if (groupName != null && !groupName.isEmpty()) {
                boolean isDeleted = groupService.deleteGroupByName(groupName);
                if (isDeleted) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    JsonUtil.writeJson(resp, "Group deleted");
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    JsonUtil.writeJson(resp, "Group not found");
                }
            } else {
                String path = req.getPathInfo();
                if (path != null && !path.equals("/")) {
                    try {
                        int groupId = Integer.parseInt(path.substring(1));
                        boolean idDeleted = groupService.deleteGroupById(groupId);
                        if (idDeleted) {
                            resp.setStatus(HttpServletResponse.SC_OK);
                            JsonUtil.writeJson(resp, "Group deleted");
                        } else {
                            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            JsonUtil.writeJson(resp, "Group not found");
                        }
                    } catch (NumberFormatException e) {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        JsonUtil.writeJson(resp, "Invalid Group ID format");
                    }
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    JsonUtil.writeJson(resp, "Invalid request format");
                }
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonUtil.writeJson(resp, e.getMessage());
        }
    }
}

