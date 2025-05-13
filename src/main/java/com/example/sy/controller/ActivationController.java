package com.example.sy.controller;

import com.example.sy.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ActivationController {

    private final UserService userService;

    public ActivationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/activate")
    public String activateAccount(@RequestParam("code") String activationCode, Model model) {
        boolean activated = userService.activateUser(activationCode);
        model.addAttribute("success", activated);
        model.addAttribute("message", 
            activated ? "Your account has been activated successfully!" 
                     : "Invalid activation code or account already activated.");
        return "activation-result";
    }
}