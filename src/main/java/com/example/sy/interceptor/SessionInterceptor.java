package com.example.sy.interceptor;

import com.example.sy.model.User;
import com.example.sy.util.SessionUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) 
            throws Exception {
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            User user = SessionUtils.getCurrentUser(session);
            if (user != null) {
                // Refresh session for authenticated users
                SessionUtils.refreshSession(session);
                
                // Set user attributes as request attributes for easy access in views
                request.setAttribute("currentUser", user);
                request.setAttribute("isAdmin", user.isAdmin());
            }
        }
        
        return true;
    }
}