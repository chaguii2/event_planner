package org.example.session;

import org.example.model.*;

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

    public static boolean isOrganizer() {
        return currentUser instanceof Organizer;
    }

    public static boolean isParticipant() {
        return currentUser instanceof Participant;
    }

    public static Role getRole() {
        return isLoggedIn() ? currentUser.getRole() : null;
    }

    public static void clear() {
        currentUser = null;
    }
}