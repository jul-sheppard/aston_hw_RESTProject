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
            String name = req.getParameter("name");
            String email = req.getParameter("email");
            if (name == null || name.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonUtil.writeJson(resp, "Invalid name or email");
                return;
            }
            if (email == null || email.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonUtil.writeJson(resp, "Invalid email");
                return;
            }
            UserDTO userDTO = new UserDTO(0, name, email);
            boolean result = userService.addUser(userDTO);
            if (result) {
                resp.setStatus(HttpServletResponse.SC_OK);
                JsonUtil.writeJson(resp, "User added");
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonUtil.writeJson(resp, "Failed to add user");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonUtil.writeJson(resp, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {}
}
