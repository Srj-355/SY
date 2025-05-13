package com.example.sy.service;

import com.example.sy.model.Booking;
import com.example.sy.model.Bus;
import com.example.sy.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    Booking saveBooking(Booking booking);
    Optional<Booking> getBookingById(Long bookingId);
    List<Booking> getBookingsByUser(User user);
    Booking updateBookingStatus(Long bookingId, String status);
    Optional<Booking> findByTransactionId(String transactionId);
    Page<Booking> getAllBookings(Pageable pageable);
    void deleteBooking(Long id);
Page<Booking> findByFirstNameAndLastNameContaining(String firstName, String lastName, Pageable pageable);
List<Booking> findByBusAndTravelDate(Bus bus, String travelDate);
List<Booking> findConfirmedBookingsByBusAndDate(Bus bus, String date);
}