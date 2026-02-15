package org.example.util;

import org.example.model.Role;
import org.example.model.User;

public class UserValidator {
    public static void validate(User u) {
        if (u.getName() == null || u.getName().trim().length() < 3) {
            throw new RuntimeException("Name must be at least 3 characters long");
        }

        if (!u.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new RuntimeException("Invalid email format");
        }

        if (u.getPassword() == null || u.getPassword().length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters long");
        }

        if (u.getPhone() == null || u.getPhone().trim().isEmpty()) {
            throw new RuntimeException("Phone number is required");
        }

        if (u.getRole() == null) {
            throw new RuntimeException("Role is required");
        }
    }
}