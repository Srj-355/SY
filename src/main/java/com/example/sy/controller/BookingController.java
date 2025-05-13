package com.example.sy.controller;

import com.example.sy.model.Booking;
import com.example.sy.model.Bus;
import com.example.sy.model.User;
import com.example.sy.service.BookingService;
import com.example.sy.service.BusService;
import com.example.sy.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
public class BookingController {
    private final BookingService bookingService;
    private final BusService busService;
    private final UserService userService;

    public BookingController(BookingService bookingService, BusService busService, UserService userService) {
        this.bookingService = bookingService;
        this.busService = busService;
        this.userService = userService;
    }

    @GetMapping("/booking")
    public String showBookingForm(@RequestParam(required = false) Long busId,
                                @RequestParam(required = false) String seats,
                                @RequestParam(required = false) String boardingPoint,
                                @RequestParam(required = false) Double totalAmount,
                                @RequestParam String departure,
                                @RequestParam String arrival,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                HttpSession session, Model model) {

                                    User user = (User) session.getAttribute("user");
                                    if (user == null) {

                                        // Store booking details in session before redirecting to login
                                        Map<String, Object> pendingBooking = new HashMap<>();
                                        pendingBooking.put("busId", busId);
                                        pendingBooking.put("seats", seats);
                                        pendingBooking.put("boardingPoint", boardingPoint);
                                        pendingBooking.put("totalAmount", totalAmount);
                                        pendingBooking.put("date", date.toString());
                                        pendingBooking.put("departure", departure);
                                        pendingBooking.put("arrival", arrival);
                                        session.setAttribute("pendingBooking", pendingBooking);
                                        return "redirect:/auth";
                                    }

        // Check if parameters are null (might be coming from session)
        if (busId == null || seats == null || boardingPoint == null || totalAmount == null) {
            Map<String, Object> pendingBooking = (Map<String, Object>) session.getAttribute("pendingBooking");
            if (pendingBooking != null) {
                busId = (Long) pendingBooking.get("busId");
                seats = (String) pendingBooking.get("seats");
                boardingPoint = (String) pendingBooking.get("boardingPoint");
                totalAmount = (Double) pendingBooking.get("totalAmount");
                date = LocalDate.parse((String) pendingBooking.get("date"));
                departure = (String)departure;
                arrival = (String)arrival;
                session.removeAttribute("pendingBooking");
            } else {
                return "redirect:/buses?error=missing_booking_details";
            }
        }

        // Validate totalAmount
        if (totalAmount <= 0) {
            return "redirect:/buses?error=invalid_amount";
        }

        Bus bus = busService.getBusById(busId);
        model.addAttribute("bus", bus);
        model.addAttribute("travelDate", date);
        model.addAttribute("seats", seats);
        model.addAttribute("boardingPoint", boardingPoint);
        model.addAttribute("departure",departure);
        model.addAttribute("arrival",arrival);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("user", user);

        return "booking.html";
    }

    @PostMapping("/booking")
    public String processBooking(@RequestParam Long busId,
                                 @RequestParam String seats,
                                 @RequestParam String boardingPoint,
                                 @RequestParam double totalAmount,
                                 @RequestParam String firstName,
                                 @RequestParam String lastName,
                                 @RequestParam String phoneNumber,
                                 @RequestParam String paymentMethod,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate travelDate,
                                 HttpSession session) {

        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            return "redirect:/auth";
        }

        User user = userService.getUserById(sessionUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Bus bus = busService.getBusById(busId);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setBus(bus);
        booking.setFirstName(firstName);
        booking.setLastName(lastName);
        booking.setPhoneNumber(phoneNumber);
        booking.setBoardingPoint(boardingPoint);
        booking.setSeats(seats);
        booking.setTotalAmount(totalAmount);
        booking.setPaymentMethod(paymentMethod);
        booking.setStatus("PENDING");
        booking.setPaymentDate(LocalDateTime.now());
        booking.setTravelDate(String.valueOf(travelDate));

        bookingService.saveBooking(booking);

        if ("esewa".equals(paymentMethod)) {
            return "redirect:/payment/esewa?bookingId=" + booking.getId();
        } else if ("khalti".equals(paymentMethod)) {
            return "redirect:/payment/khalti?bookingId=" + booking.getId();
        } else {
            return "redirect:/payment/failure?error=invalid_payment_method";
        }
    }
}