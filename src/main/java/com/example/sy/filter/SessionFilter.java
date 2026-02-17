package com.example.sy.filter;

import com.example.sy.annotation.AdminAccessControl;
import com.example.sy.model.User;
import com.example.sy.util.SessionUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Component
public class SessionFilter implements Filter {
    private static final List<String> ADMIN_PATHS = Arrays.asList(
            "/admin",
            "/admin/",
            "/admin/**"
    );

    private static final List<String> USER_PATHS = Arrays.asList(
            "/booking",
            "/payment",
            "/history",
            "/profile"
    );

    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/auth",
            "/static",
            "/",
            "/index",
            "/css",
            "/js",
            "/images",
            "/payment/esewa/success",
            "/payment/failure",
            "/about",
            "/contact",
            "/faq",
            "/privacy",
            "/terms"
    );

    private final RequestMappingHandlerMapping handlerMapping;

    public SessionFilter(RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getRequestURI();

        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        User user = SessionUtils.getCurrentUser(session);

        if (user == null) {
            redirectToLogin(httpRequest, httpResponse);
            return;
        }

        // Check admin access
        if (requiresAdminAccess(httpRequest, path) && !user.isAdmin()) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin access required");
            return;
        }

        // Check regular user access
        if (isUserPath(path) && !SessionUtils.isAuthenticated(session)) {
            redirectToLogin(httpRequest, httpResponse);
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean requiresAdminAccess(HttpServletRequest request, String path) {
        HttpSession session = request.getSession(false);
        Boolean adminMode = session != null ? (Boolean) session.getAttribute("adminMode") : false;
        
        // If in admin mode, only allow admin paths
        if (adminMode != null && adminMode && !isAdminPath(path)) {
            return true; // Will trigger access denied
        }
        
        return isAdminPath(path) || isAdminAnnotated(request);
    }

    private boolean isAdminAnnotated(HttpServletRequest request) {
        try {
            HandlerExecutionChain handler = handlerMapping.getHandler(request);
            if (handler != null && handler.getHandler() instanceof HandlerMethod) {
                HandlerMethod handlerMethod = (HandlerMethod) handler.getHandler();
                return AnnotationUtils.findAnnotation(handlerMethod.getMethod(), AdminAccessControl.class) != null;
            }
        } catch (Exception e) {
            // Fall through
        }
        return false;
    }

    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String redirectUrl = URLEncoder.encode(request.getRequestURI(), StandardCharsets.UTF_8);
        if (request.getQueryString() != null) {
            redirectUrl += "?" + request.getQueryString();
        }
        response.sendRedirect("/auth?redirect=" + redirectUrl);
    }

    private boolean isAdminPath(String path) {
        return ADMIN_PATHS.stream().anyMatch(adminPath -> 
            path.startsWith(adminPath.replace("**", "")) || 
            path.equals(adminPath.replace("/**", ""))
        );
    }

    private boolean isUserPath(String path) {
        return USER_PATHS.stream().anyMatch(userPath -> 
            path.startsWith(userPath.replace("**", "")) || 
            path.equals(userPath.replace("/**", ""))
        );
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }
}
