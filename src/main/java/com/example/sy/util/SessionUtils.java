package com.example.sy.util;

import com.example.sy.model.User;
import jakarta.servlet.http.HttpSession;

public class SessionUtils {

    public static User getCurrentUser(HttpSession session) {
        return (session != null) ? (User) session.getAttribute("user") : null;
    }

    public static boolean isAuthenticated(HttpSession session) {
        return getCurrentUser(session) != null;
    }

public static boolean isAdmin(HttpSession session) {
    User user = getCurrentUser(session);
    return user != null && user.isAdmin();
}

    public static void requireAdmin(HttpSession session) {
        if (!isAdmin(session)) {
            throw new SecurityException("Admin privileges required");
        }
    }

    public static void requireUser(HttpSession session) {
        if (!isAuthenticated(session)) {
            throw new SecurityException("Authentication required");
        }
    }

    public static void refreshSession(HttpSession session) {
        if (session != null) {
            session.setMaxInactiveInterval(1800); // 30 minutes
        }
    }

    public static String getUsername(HttpSession session) {
        User user = getCurrentUser(session);
        return user != null ? user.getUsername() : null;
    }

    public static String getRole(HttpSession session) {
        User user = getCurrentUser(session);
        return user != null ? user.getRole() : null;
    }
}