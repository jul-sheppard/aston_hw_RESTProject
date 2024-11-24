package org.example.servlet;

import org.example.model.User;
import org.example.repository.GroupRepository;
import org.example.service.GroupService;
import org.example.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/groups/*") // URL для работы с группами
public class GroupServlet extends HttpServlet {

    private final GroupRepository groupRepository = new GroupRepository();
    private final GroupService groupService = new GroupService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();

        try {
            if (path != null && path.startsWith("/")) {
                // получить пользователей группы
                int groupId = Integer.parseInt(path.split("/")[1]);
                List<User> users = groupService.getUsersByGroupId(groupId);
                JsonUtil.writeJson(resp, users);
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (SQLException | NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }
}
