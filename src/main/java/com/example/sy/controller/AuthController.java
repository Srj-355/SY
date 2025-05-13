package com.example.sy.controller;

import com.example.sy.model.User;
import com.example.sy.service.EmailService;
import com.example.sy.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @GetMapping
    public String authPage(@RequestParam(required = false) String error,
                           @RequestParam(required = false) String success,
                           @RequestParam(required = false) String form,
                           @RequestParam(required = false) String redirect,
                           Model model) {
        if (error != null) model.addAttribute("error", error);
        if (success != null) model.addAttribute("success", success);
        model.addAttribute("showSignup", "signup".equals(form));
        if (redirect != null) model.addAttribute("redirect", redirect);
        return "auth/auth";
    }

@PostMapping("/login")
public String loginUser(
        @RequestParam String username,
        @RequestParam String password,
        @RequestParam(required = false) String redirect,
        HttpServletRequest request,
        HttpSession session,
        RedirectAttributes redirectAttributes) {

    try {
        User user = userService.authenticate(username, password);

        if (!user.isEnabled()) {
            redirectAttributes.addAttribute("error", "Account not activated. Please check your email.");
            return "redirect:/auth";
        }

        // Invalidate old session if exists and create new one
        Map<String, Object> pendingBooking = null;
        if (session != null) {
            pendingBooking = (Map<String, Object>) session.getAttribute("pendingBooking");
            session.invalidate();
        }
        session = request.getSession(true);

        // Set session attributes
        session.setAttribute("user", user);
        session.setAttribute("username", user.getUsername());
        session.setAttribute("email", user.getEmail());
        session.setAttribute("role", user.getRole());
        session.setMaxInactiveInterval((int) TimeUnit.MINUTES.toSeconds(30)); // 30 minutes
        System.out.println("Session before checking pendingBooking: " + session.getAttribute("pendingBooking"));
        // Check for pending booking
        if (pendingBooking != null) {
            session.setAttribute("pendingBooking", pendingBooking);
            System.out.println("Restored pendingBooking to new session: " + pendingBooking);
            session.removeAttribute("pendingBooking");
            return buildBookingRedirectUrl(pendingBooking);
        }
        if (user.isAdmin()) {
            session.setAttribute("adminMode", true);
            return "redirect:/admin";
        }

        // Handle redirect parameter
        if (redirect != null && !redirect.isEmpty()) {
            return "redirect:" + redirect;
        }

        return "redirect:/index";
    } catch (IllegalArgumentException e) {
        redirectAttributes.addAttribute("error", e.getMessage());
        if (redirect != null && !redirect.isEmpty()) {
            redirectAttributes.addAttribute("redirect", redirect);
        }
        return "redirect:/auth";
    }
}

    // Add this helper method
    private String buildBookingRedirectUrl(Map<String, Object> pendingBooking) {
        return UriComponentsBuilder.fromPath("redirect:/booking")
                .queryParam("busId", pendingBooking.get("busId"))
                .queryParam("seats", URLEncoder.encode((String) pendingBooking.get("seats"), StandardCharsets.UTF_8))
                .queryParam("boardingPoint", URLEncoder.encode((String) pendingBooking.get("boardingPoint"), StandardCharsets.UTF_8))
                .queryParam("totalAmount", pendingBooking.get("totalAmount"))
                .queryParam("departure", URLEncoder.encode((String) pendingBooking.get("departure"), StandardCharsets.UTF_8))
                .queryParam("arrival", URLEncoder.encode((String) pendingBooking.get("arrival"), StandardCharsets.UTF_8)) // <-- FIXED HERE
                .queryParam("date", pendingBooking.get("date"))
                .build().toUriString();
    }

    // Update logout method
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/auth?success=You have been logged out successfully";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String email,
                               @RequestParam String password,
                               RedirectAttributes redirectAttributes) {
        try {
            System.out.println("Attempting to register user: " + username);
            User user = new User(username, email, password);
            User registeredUser = userService.registerUser(user);
            System.out.println("User registered: " + registeredUser.getId());
            redirectAttributes.addAttribute("success", "Registration successful! Please check your email to activate your account.");
            return "redirect:/auth";
        } catch (IllegalArgumentException e) {
            System.out.println("Registration failed: " + e.getMessage());
            redirectAttributes.addAttribute("error", e.getMessage());
            return "redirect:/auth";
        }
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email,
                                        RedirectAttributes redirectAttributes) {
        try {
            String resetToken = userService.generatePasswordResetToken(email);
            System.out.println("Generated token: " + resetToken);
            User user = userService.getUserByEmail(email);
            emailService.sendPasswordResetEmail(user, resetToken);
            redirectAttributes.addFlashAttribute("success", "Password reset link sent to your email");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "No user found with this email");
        }
        return "redirect:/auth/forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam String token, Model model) {
        Optional<User> user = userService.findByResetToken(token);
        if (user.isPresent() && !user.get().isEnabled()) {
            model.addAttribute("accountInactive", true);
        }
        model.addAttribute("token", token);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam String token,
                                       @RequestParam String password,
                                       @RequestParam String confirmPassword,
                                       RedirectAttributes redirectAttributes) {
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match");
            return "redirect:/auth/reset-password?token=" + token;
        }

        try {
            userService.resetPassword(token, password);
            redirectAttributes.addFlashAttribute("success", "Password reset successfully. You can now login.");
            return "redirect:/auth";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Invalid or expired reset token");
            return "redirect:/auth/reset-password?token=" + token;
        }
    }

    @GetMapping("/activate")
    public String activateAccount(@RequestParam String code, RedirectAttributes redirectAttributes) {
        if (userService.activateUser(code)) {
            redirectAttributes.addFlashAttribute("success", "Account activated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid activation code");
        }
        return "redirect:/auth";
    }
}