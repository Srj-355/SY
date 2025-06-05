package com.example.sy.service;

import com.example.sy.model.Booking;
import com.example.sy.model.User;

public interface EmailService {
    void sendPasswordResetEmail(User user, String resetToken);
    void sendAccountActivationEmail(User user);
    void sendBookingCancellationEmail(Booking booking);
    void sendBookingConfirmationEmail(Booking booking);
}