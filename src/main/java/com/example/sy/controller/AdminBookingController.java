package com.example.sy.controller;

import com.example.sy.annotation.AdminAccessControl;
import com.example.sy.model.Booking;
import com.example.sy.model.Bus;
import com.example.sy.model.User;
import com.example.sy.service.BookingService;
import com.example.sy.service.BusService;
import com.example.sy.service.EmailService;
import com.example.sy.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@AdminAccessControl
@Controller
@RequestMapping("/admin/bookings")
public class AdminBookingController {

    private final BookingService bookingService;
    private final BusService busService;
    private final UserService userService;
    private final EmailService emailService;

    public AdminBookingController(BookingService bookingService, BusService busService,
                                  UserService userService, EmailService emailService) {
        this.bookingService = bookingService;
        this.busService = busService;
        this.userService = userService;
        this.emailService = emailService;
    }

    @GetMapping
public String listBookings(Model model,
                           @RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "10") int size,
                           @RequestParam(defaultValue = "paymentDate") String sort,
                           @RequestParam(defaultValue = "desc") String direction,
                           @RequestParam(required = false) String firstName,
                           @RequestParam(required = false) String lastName) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(sortDirection, sort));
        
        Page<Booking> bookingsPage;
    if ((firstName != null && !firstName.isEmpty()) || (lastName != null && !lastName.isEmpty())) {
        bookingsPage = bookingService.findByFirstNameAndLastNameContaining(
            firstName != null ? firstName : "",
            lastName != null ? lastName : "",
            pageable);
    } else {
        bookingsPage = bookingService.getAllBookings(pageable);
    }
    
        
        model.addAttribute("bookings", bookingsPage.getContent());
        model.addAttribute("currentPage", bookingsPage.getNumber() + 1);
        model.addAttribute("totalPages", bookingsPage.getTotalPages());
        model.addAttribute("totalItems", bookingsPage.getTotalElements());
        model.addAttribute("sort", sort);
        model.addAttribute("direction", direction);
    
        return "admin/bookings/list";
    }

    @GetMapping("/{id}/view")
    public String viewBooking(@PathVariable Long id, Model model) {
        Booking booking = bookingService.getBookingById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid booking Id:" + id));
        model.addAttribute("booking", booking);
        return "admin/bookings/view";
    }

    @GetMapping("/{id}/edit")
    public String editBookingForm(@PathVariable Long id, Model model) {
        Booking booking = bookingService.getBookingById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid booking Id:" + id));
        model.addAttribute("booking", booking);
        return "admin/bookings/edit";
    }

    @PostMapping("/{id}/update")
    public String updateBooking(@PathVariable Long id, @ModelAttribute Booking bookingDetails,
                                RedirectAttributes redirectAttributes) {
        Booking booking = bookingService.getBookingById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid booking Id:" + id));

        booking.setFirstName(bookingDetails.getFirstName());
        booking.setLastName(bookingDetails.getLastName());
        booking.setPhoneNumber(bookingDetails.getPhoneNumber());
        booking.setBoardingPoint(bookingDetails.getBoardingPoint());
        booking.setSeats(bookingDetails.getSeats());
        booking.setTotalAmount(bookingDetails.getTotalAmount());
        booking.setPaymentMethod(bookingDetails.getPaymentMethod());
        booking.setStatus(bookingDetails.getStatus());
        booking.setTransactionId(bookingDetails.getTransactionId());

        bookingService.saveBooking(booking);

        if ("CONFIRMED".equals(booking.getStatus())) {
            emailService.sendBookingConfirmationEmail(booking);
        }

        redirectAttributes.addFlashAttribute("success", "Booking updated successfully!");
        return "redirect:/admin/bookings/" + id + "/view";
    }

    @PostMapping("/cancel")
    public String cancelBooking(@RequestParam Long bookingId,
                                @RequestParam String cancellationReason,
                                @RequestParam(required = false, defaultValue = "true") boolean sendEmail,
                                RedirectAttributes redirectAttributes) {
        Booking booking = bookingService.getBookingById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid booking Id:" + bookingId));

        booking.setStatus("CANCELLED");
        booking.setCancellationReason(cancellationReason);
        bookingService.saveBooking(booking);

        if (sendEmail) {
            emailService.sendBookingCancellationEmail(booking);
        }

        redirectAttributes.addFlashAttribute("success", "Booking cancelled successfully!");
        return "redirect:/admin/bookings/" + bookingId + "/view";
    }

    @GetMapping("/{id}/delete")
    public String deleteBooking(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        bookingService.deleteBooking(id);
        redirectAttributes.addFlashAttribute("success", "Booking deleted successfully!");
        return "redirect:/admin/bookings";
    }

    @GetMapping("/{id}/resend-confirmation")
    public String resendConfirmation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Booking booking = bookingService.getBookingById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid booking Id:" + id));

        emailService.sendBookingConfirmationEmail(booking);
        redirectAttributes.addFlashAttribute("success", "Confirmation email resent successfully!");
        return "redirect:/admin/bookings/" + id + "/view";
    }
}