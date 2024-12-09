package org.example.servlet;

import org.example.model.dto.GroupDTO;
import org.example.model.dto.UserDTO;
import org.example.service.UserService;
import org.example.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;


@WebServlet("/users/*") //URL для работы с пользователями
public class UserServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String path = req.getPathInfo();
            if (path == null || path.equals("/")) {
                List<UserDTO> users = userService.getAllUsers();
                JsonUtil.writeJson(resp, users);
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                String[] parts = path.split("/");
                if (parts.length == 3 && parts[2].equals("groups")) {
                    int userId = Integer.parseInt(parts[1]);
                    List<GroupDTO> groups = userService.getGroupsByUserId(userId);
                    JsonUtil.writeJson(resp, groups);
                    resp.setStatus(HttpServletResponse.SC_OK);
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    JsonUtil.writeJson(resp, "Invalid URL structure");
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonUtil.writeJson(resp, "Invalid user ID");
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonUtil.writeJson(resp, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonUtil.writeJson(resp, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String groupIdParam = req.getParameter("groupId");
            String userIdParam = req.getParameter("userId");
            String name = req.getParameter("name");
            String email = req.getParameter("email");
            if (groupIdParam != null) {
                if (userIdParam == null || userIdParam.isEmpty() || name == null || name.isEmpty() || email == null || email.isEmpty()) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    JsonUtil.writeJson(resp, "Missing or invalid parameters for adding user to group");
                    return;
                }
                int groupId = Integer.parseInt(groupIdParam);
                int userId = Integer.parseInt(userIdParam);
                UserDTO userDTO = new UserDTO(userId, name, email);
                boolean isAddedToGroup = userService.addUserToGroup(userDTO, groupId);
                if (isAddedToGroup) {
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                    JsonUtil.writeJson(resp, "User added to group successfully");
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    JsonUtil.writeJson(resp, "User could not be added to group");
                }
            } else {
                if (name == null || name.isEmpty()) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    JsonUtil.writeJson(resp, "Invalid name");
                    return;
                }
                if (email == null || email.isEmpty()) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    JsonUtil.writeJson(resp, "Invalid email");
                    return;
                }
                UserDTO userDTO = new UserDTO(0, name, email);
                boolean isUserAdded = userService.addUser(userDTO);
                if (isUserAdded) {
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                    JsonUtil.writeJson(resp, "User added successfully");
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    JsonUtil.writeJson(resp, "User could not be added");
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonUtil.writeJson(resp, "Invalid number format in request parameters");
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonUtil.writeJson(resp, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String path = req.getPathInfo();
            if (path == null || path.equals("/")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonUtil.writeJson(resp, "Invalid URL structure");
                return;
            }
            String[] parts = path.split("/");
            if (parts.length == 2) {
                int userId = Integer.parseInt(parts[1]);
                userService.deleteUser(userId);
                resp.setStatus(HttpServletResponse.SC_OK);
                JsonUtil.writeJson(resp, "User deleted successfully");
            } else if (parts.length == 3 && parts[2].equals("groups")) {
                String groupIdParam = req.getParameter("groupId");
                if (groupIdParam == null || groupIdParam.isEmpty()) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    JsonUtil.writeJson(resp, "Invalid group ID");
                    return;
                }
                int groupId = Integer.parseInt(groupIdParam);
                int userId = Integer.parseInt(parts[1]);
                UserDTO userDTO = new UserDTO();
                userDTO.setId(userId);
                boolean result = UserService.removeUserFromGroup(userDTO, groupId);
                if (result) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    JsonUtil.writeJson(resp, "User removed from group successfully");
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    JsonUtil.writeJson(resp, "Failed to remove user from group");
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonUtil.writeJson(resp, "Invalid URL structure");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonUtil.writeJson(resp, "Invalid user ID");
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonUtil.writeJson(resp, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String path = req.getPathInfo();
            if (path == null || path.equals("/")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonUtil.writeJson(resp, "Invalid URL structure");
                return;
            }
            String[] parts = path.split("/");
            if (parts.length != 2) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonUtil.writeJson(resp, "Invalid URL structure");
                return;
            }
            int userId = Integer.parseInt(parts[1]);
            String name = req.getParameter("name");
            String email = req.getParameter("email");
            if (name == null || name.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonUtil.writeJson(resp, "Invalid name");
                return;
            }
            if (email == null || email.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonUtil.writeJson(resp, "Invalid email");
                return;
            }
            UserDTO userDTO = new UserDTO(userId, name, email);
            boolean isUpdated = userService.updateUser(userDTO);
            if (isUpdated) {
                resp.setStatus(HttpServletResponse.SC_OK);
                JsonUtil.writeJson(resp, "User updated successfully");
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                JsonUtil.writeJson(resp, "User not found");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonUtil.writeJson(resp, "Invalid user ID");
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonUtil.writeJson(resp, e.getMessage());
        }
    }
}