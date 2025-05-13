package com.example.sy.controller;

import com.example.sy.annotation.AdminAccessControl;
import com.example.sy.model.User;
import com.example.sy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@AdminAccessControl
@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @GetMapping
public String listUsers(Model model, @RequestParam(required = false) String username) {
    if (username != null && !username.isEmpty()) {
        model.addAttribute("users", userService.findByUsername(username)
            .map(List::of)
            .orElse(List.of()));
    } else {
        model.addAttribute("users", userService.getAllUsers());
    }
    return "admin/users/list";
}

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/users/add";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute("user") User user) {
        userService.saveUser(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.getUserById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user id: " + id)));
        return "admin/users/edit";
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute("user") User user) {
        user.setId(id);
        userService.saveUser(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }
}