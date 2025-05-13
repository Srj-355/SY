package com.example.sy.controller;

import com.example.sy.annotation.AdminAccessControl;
import com.example.sy.model.User;
import com.example.sy.service.DashboardService;
import com.example.sy.service.UserService;
import com.example.sy.util.SessionUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/admin")
@AdminAccessControl
public class AdminController {

    private final UserService userService;
    private final DashboardService dashboardService;


    public AdminController(UserService userService, DashboardService dashboardService) {
        this.userService = userService;
        this.dashboardService = dashboardService;
    }

    @GetMapping
public String adminDashboard(HttpSession session, Model model) {
    User currentUser = SessionUtils.getCurrentUser(session);
    if (currentUser == null || !currentUser.isAdmin()) {
        return "redirect:/auth?error=Admin access required";
    }
    model.addAttribute("totalBookings", dashboardService.getTotalBookings());
    model.addAttribute("totalRevenue", dashboardService.getTotalRevenue());
    model.addAttribute("activeBuses", dashboardService.getActiveBuses());
    model.addAttribute("registeredUsers", dashboardService.getRegisteredUsers());
    model.addAttribute("monthlyRevenue", dashboardService.getMonthlyRevenue());
    model.addAttribute("busOccupancy", dashboardService.getBusOccupancy());
    model.addAttribute("currentUser", currentUser);
    return "admin/admin";
}

    @RequestMapping(value = "/logout", method = {RequestMethod.GET, RequestMethod.POST})
public String adminLogout(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session != null) {
        session.removeAttribute("adminMode");
        session.invalidate();
    }
    return "redirect:/auth?logout=true";
}

}