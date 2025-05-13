package com.example.sy.service.impl;

import com.example.sy.model.Booking;
import com.example.sy.model.User;
import com.example.sy.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Locale;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    @Value("${app.base-url}")
    private String baseUrl;
    
    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendPasswordResetEmail(User user, String resetToken) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(user.getEmail());
            helper.setSubject("Password Reset Request");

            Context context = new Context();
            context.setVariable("user", user);
            context.setVariable("resetToken", resetToken);
            context.setVariable("baseUrl", baseUrl); // Ensure this is set in properties

            String htmlContent = templateEngine.process("email/password-reset-email", context);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    @Override
    public void sendAccountActivationEmail(User user) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(user.getEmail());
            helper.setSubject("Activate Your Account");
            
            Context context = new Context(Locale.getDefault());
            context.setVariable("user", user);
            context.setVariable("activationUrl", baseUrl + "/activate?code=" + user.getActivationCode());
            context.setVariable("baseUrl", baseUrl);
            
            String htmlContent = templateEngine.process("email/account-activation", context);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send account activation email", e);
        }
    }
    @Override
    public void sendBookingConfirmationEmail(Booking booking) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(booking.getUser().getEmail());
            helper.setSubject("Your SajiloYatra Booking Confirmation");

            // Prepare the evaluation context
            Context context = new Context();
            context.setVariable("booking", booking);

            // Process the Thymeleaf template
            String htmlContent = templateEngine.process("email/booking-confirmation", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send booking confirmation email", e);
        }
    }
    @Override
    public void sendBookingCancellationEmail(Booking booking) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(booking.getUser().getEmail());
            helper.setSubject("Your SajiloYatra Booking Cancellation");

            Context context = new Context();
            context.setVariable("booking", booking);
            context.setVariable("baseUrl", baseUrl);

            String htmlContent = templateEngine.process("email/booking-cancellation", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send booking cancellation email", e);
        }
    }

}