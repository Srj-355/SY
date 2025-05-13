package com.example.sy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PrivacyController {
    @GetMapping("/privacy-policy")
    public String privacy() {
        return "privacy.html";
    }
}
