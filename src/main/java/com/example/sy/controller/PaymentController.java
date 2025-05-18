package com.example.sy.controller;

import com.example.sy.model.Booking;
import com.example.sy.service.BookingService;
import com.example.sy.service.EmailService;
import com.example.sy.service.PaymentService;
import com.example.sy.util.SessionUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/payment")
public class PaymentController {
    private final BookingService bookingService;
    private final EmailService emailService;
    private final PaymentService paymentService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${esewa.merchant.id}")
    private String esewaMerchantId;

    @Value("${esewa.secret-key}")
    private String esewaSecretKey;

    @Value("${khalti.secret-key}")
    private String khaltiSecretKey;

    public PaymentController(BookingService bookingService,
                             EmailService emailService,
                             PaymentService paymentService) {
        this.bookingService = bookingService;
        this.emailService = emailService;
        this.paymentService = paymentService;
    }

    private void validateSession(HttpSession session) {
        if (!SessionUtils.isAuthenticated(session)) {
            throw new IllegalStateException("User not authenticated");
        }
        SessionUtils.refreshSession(session);
    }

    // ESewa Payment Integration
    @GetMapping("/esewa")
    public String initiateEsewaPayment(@RequestParam Long bookingId,
                                     HttpSession session,
                                     Model model) throws NoSuchAlgorithmException, InvalidKeyException {
        validateSession(session);
        Booking booking = bookingService.getBookingById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        String transactionUuid = "SY-" + booking.getId() + "-" + System.currentTimeMillis();

        Map<String, String> params = new HashMap<>();
        params.put("amount", String.format("%.2f", booking.getTotalAmount()));
        params.put("tax_amount", "0");
        params.put("total_amount", String.format("%.2f", booking.getTotalAmount()));
        params.put("transaction_uuid", transactionUuid);
        params.put("product_code", "EPAYTEST");
        params.put("product_service_charge", "0");
        params.put("product_delivery_charge", "0");
        params.put("signed_field_names", "total_amount,transaction_uuid,product_code");

        String successUrl = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/payment/success")
                .queryParam("bookingId",booking.getId())
                .queryParam("oid", transactionUuid)
                .queryParam("amt", String.format("%.2f", booking.getTotalAmount()))
                .build()
                .toUriString();

        String failureUrl = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/payment/failure")
                .queryParam("bookingId", bookingId)
                .queryParam("date", booking.getTravelDate())
                .build()
                .toUriString();

        params.put("success_url", successUrl);
        params.put("failure_url", failureUrl);

        String signature = paymentService.generateEsewaSignature(params, esewaSecretKey);

        model.addAttribute("esewaUrl", "https://rc-epay.esewa.com.np/api/epay/main/v2/form");
        model.addAttribute("params", params);
        model.addAttribute("signature", signature);
        model.addAttribute("merchantId", esewaMerchantId);

        booking.setTransactionId(transactionUuid);
        bookingService.saveBooking(booking);

        return "payment/esewa-form";
    }

    @GetMapping("/esewa/verify")
    public String verifyEsewaPayment(@RequestParam String oid,
                                   @RequestParam String amt,
                                   @RequestParam(required = false) String refId,
                                   HttpSession session,
                                   Model model) {
        try {
            validateSession(session);
            
            // Verify payment with eSewa
            boolean paymentVerified = paymentService.verifyEsewaPayment(oid, amt, refId);
            
        System.out.println("Payment verification result: " + paymentVerified);
            if (paymentVerified) {
                Long bookingId = Long.parseLong(oid.split("-")[1]);
                Booking booking = bookingService.getBookingById(bookingId)
                    .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
                
                booking.setStatus("CONFIRMED");
                booking.setPaymentDate(LocalDateTime.now());
                bookingService.saveBooking(booking);
                
                emailService.sendBookingConfirmationEmail(booking);
                return "redirect:/payment/success?bookingId=" + booking.getId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/payment/failure";
    }

    @GetMapping("/success")
    public String handlePaymentSuccess(@RequestParam Long bookingId, Model model) {
        Booking booking = bookingService.getBookingById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
                booking.setStatus("CONFIRMED");
                bookingService.saveBooking(booking);
                emailService.sendBookingConfirmationEmail(booking);
        model.addAttribute("booking", booking);
        return "payment/success";
    }


    // @GetMapping("/esewa/success")
    // public String handleEsewaSuccess(@RequestParam String data, Model model) {
    //     try {
    //         Map<String, String> responseParams = parsePaymentResponse(data);

    //         String transactionUuid = responseParams.get("transaction_uuid");
    //         String status = responseParams.get("status");
    //         String signature = responseParams.get("signature");

    //         Map<String, String> verificationParams = new HashMap<>();
    //         verificationParams.put("transaction_uuid", transactionUuid);
    //         verificationParams.put("status", status);
    //         verificationParams.put("signed_field_names", "transaction_uuid,status");

    //         boolean isValid = paymentService.verifyEsewaSignature(
    //                 verificationParams,
    //                 signature,
    //                 esewaSecretKey
    //         );

    //         if (isValid && "COMPLETE".equals(status)) {
    //             Booking booking = bookingService.findByTransactionId(transactionUuid)
    //                     .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

    //             booking.setStatus("PAID");
    //             booking.setPaymentDate(LocalDateTime.now());
    //             bookingService.saveBooking(booking);

    //             emailService.sendBookingConfirmationEmail(booking);

    //             model.addAttribute("booking", booking);
    //             return "payment/success";
    //         }
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     return "redirect:/payment/failure";
    // }

    // Khalti Payment Integration
//    @GetMapping("/khalti")
//    public String initiateKhaltiPayment(@RequestParam Long bookingId, Model model) {
//        Booking booking = bookingService.getBookingById(bookingId)
//                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
//
//        String transactionId = "KH-" + booking.getId() + "-" + System.currentTimeMillis();
//
//        Map<String, String> params = new HashMap<>();
//        params.put("public_key", khaltiSecretKey);
//        params.put("product_identity", transactionId);
//        params.put("product_name", "Bus Ticket");
//        params.put("amount", String.valueOf((int)(booking.getTotalAmount() * 100))); // in paisa
//        params.put("mobile", booking.getPhoneNumber());
//
//        String returnUrl = UriComponentsBuilder.fromHttpUrl(baseUrl)
//                .path("/payment/khalti/verify")
//                .queryParam("bookingId", bookingId)
//                .build()
//                .toUriString();
//
//        params.put("product_url", returnUrl);
//
//        booking.setTransactionId(transactionId);
//        bookingService.saveBooking(booking);
//
//        model.addAttribute("khaltiConfig", params);
//        return "payment/khalti-form";
//    }
//
//    @PostMapping("/khalti/verify")
//    public String verifyKhaltiPayment(@RequestParam String token,
//                                      @RequestParam Long bookingId,
//                                      Model model) {
//        try {
//            boolean verification = paymentService.verifyKhaltiPayment(token, khaltiSecretKey);
//
//            if (verification) {
//                Booking booking = bookingService.getBookingById(bookingId)
//                        .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
//
//                booking.setStatus("PAID");
//                booking.setPaymentDate(LocalDateTime.now());
//                bookingService.saveBooking(booking);
//
//                emailService.sendBookingConfirmationEmail(booking);
//
//                model.addAttribute("booking", booking);
//                return "payment/success";
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "redirect:/payment/failure?bookingId=" + bookingId;
//    }

    // Common Failure Handler
    @GetMapping("/failure")
    public String handlePaymentFailure(
            @RequestParam(required = false) Long bookingId,
            @RequestParam(required = false) String error,
            Model model) {

        if (bookingId != null) {
            bookingService.getBookingById(bookingId).ifPresent(booking -> {
                model.addAttribute("booking", booking);
                model.addAttribute("error", "Payment failed. Please try again.");
            });
        }
        return "payment/failure";
    }

    private Map<String, String> parsePaymentResponse(String jsonData) throws JsonProcessingException {
        return objectMapper.readValue(jsonData, new TypeReference<Map<String, String>>() {});
    }
}