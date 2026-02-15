package org.example.dao.interfaces;

import org.example.model.Role;
import org.example.model.User;
import java.util.List;

public interface IUserDAO {
    void create(User user) throws Exception;
    User findById(int id) throws Exception;
    User findByEmail(String email) throws Exception;
    List<User> findAll() throws Exception;
    List<User> findByRole(Role role) throws Exception;
    void update(User user) throws Exception;
    void delete(int id) throws Exception;
}