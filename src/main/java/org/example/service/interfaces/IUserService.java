package org.example.service.interfaces;

import org.example.model.Role;
import org.example.model.User;
import java.util.List;

public interface IUserService {
    List<User> findAll() throws Exception;
    List<User> getByRole(Role role) throws Exception;
    User findById(int id) throws Exception;
    User findByEmail(String email) throws Exception;
    void update(User user) throws Exception;
    void delete(int id) throws Exception;
}