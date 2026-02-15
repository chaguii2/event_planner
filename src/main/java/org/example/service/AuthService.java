package org.example.service;

import org.example.dao.UserDAO;
import org.example.model.Role;
import org.example.model.User;
import org.example.security.PasswordUtil;
import org.example.service.interfaces.IAuthService;
import org.example.session.Session;

public class AuthService implements IAuthService {

    private UserDAO dao = new UserDAO();

    @Override
    public User login(String email, String password) throws Exception {
        User u = dao.findByEmail(email);

        if (u == null || !PasswordUtil.verify(password, u.getPassword())) {
            throw new RuntimeException("Email ou mot de passe incorrect");
        }

        Session.login(u);
        return u;
    }

    @Override
    public void register(User u) throws Exception {
        if (u.getRole() == Role.ADMIN) {
            throw new RuntimeException("Impossible de créer un admin");
        }

        if (dao.findByEmail(u.getEmail()) != null) {
            throw new RuntimeException("Email existe déjà");
        }

        dao.create(u);
    }
}