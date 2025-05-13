package com.example.sy.controller;

import com.example.sy.model.User;
import com.example.sy.service.BookingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BookingHistoryController {
    private final BookingService bookingService;

    public BookingHistoryController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/my-bookings")
    public String showMyBookings(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth";
        }
        
        model.addAttribute("bookings", bookingService.getBookingsByUser(user));
        return "my-bookings";
    }
}