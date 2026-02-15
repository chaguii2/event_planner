package org.example.session;

import org.example.model.Admin;
import org.example.model.Role;
import org.example.model.User;

public class Session {
    private static User currentUser;

    public static void login(User user) {
        currentUser = user;
    }

    public static void logout() {
        currentUser = null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static boolean isAdmin() {
        return currentUser instanceof Admin;
    }

    public static Role getRole() {
        return isLoggedIn() ? currentUser.getRole() : null;
    }

    public static void clear() {
        currentUser = null;
    }
}