package org.example.service;

import org.example.dao.UserDAO;
import org.example.model.Role;
import org.example.model.User;
import org.example.service.interfaces.IUserService;
import org.example.session.Session;

import java.util.List;

public class UserService implements IUserService {

    private UserDAO dao = new UserDAO();

    private void checkAdmin() {
        if (!Session.isAdmin()) {
            throw new RuntimeException("Access denied. Admin privileges required.");
        }
    }

    @Override
    public List<User> findAll() throws Exception {
        checkAdmin();
        return dao.findAll();
    }

    @Override
    public List<User> getByRole(Role role) throws Exception {
        checkAdmin();
        return dao.findByRole(role);
    }

    @Override
    public User findById(int id) throws Exception {
        checkAdmin();
        return dao.findById(id);
    }

    @Override
    public User findByEmail(String email) throws Exception {
        checkAdmin();
        return dao.findByEmail(email);
    }

    @Override
    public void update(User u) throws Exception {
        checkAdmin();

        if (u.getId() == Session.getCurrentUser().getId()) {
            throw new RuntimeException("Cannot modify your own account through this interface");
        }

        dao.update(u);
    }

    @Override
    public void delete(int id) throws Exception {
        checkAdmin();

        if (id == Session.getCurrentUser().getId()) {
            throw new RuntimeException("Cannot delete your own account");
        }

        dao.delete(id);
    }
}