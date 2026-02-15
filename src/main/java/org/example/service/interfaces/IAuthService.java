package org.example.service.interfaces;

import org.example.model.User;

public interface IAuthService {
    User login(String email, String password) throws Exception;
    void register(User user) throws Exception;
}