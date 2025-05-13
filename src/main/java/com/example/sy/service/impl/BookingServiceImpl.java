package com.example.sy.service.impl;

import com.example.sy.model.Booking;
import com.example.sy.model.Bus;
import com.example.sy.model.User;
import com.example.sy.repository.BookingRepository;
import com.example.sy.service.BookingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;

    public BookingServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Booking saveBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    @Override
    public Optional<Booking> getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId);
    }

    @Override
    public List<Booking> getBookingsByUser(User user) {
        return bookingRepository.findByUser(user);
    }

    @Override
    public Booking updateBookingStatus(Long bookingId, String status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }

    @Override
    public Optional<Booking> findByTransactionId(String transactionId) {
        return bookingRepository.findByTransactionId(transactionId);
    }

    @Override
    public Page<Booking> getAllBookings(Pageable pageable) {
        return bookingRepository.findAll(pageable);
    }

    @Override
    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }

    // ... existing code ...
@Override
public Page<Booking> findByFirstNameAndLastNameContaining(String firstName, String lastName, Pageable pageable) {
    return bookingRepository.findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(
        firstName, lastName, pageable);
}
@Override
    public List<Booking> findByBusAndTravelDate(Bus bus, String travelDate) {
        return bookingRepository.findByBusAndTravelDate(bus, travelDate);
    }
    @Override
    public List<Booking> findConfirmedBookingsByBusAndDate(Bus bus, String date) {
        return bookingRepository.findByBusAndTravelDateAndStatus(bus, date, "CONFIRMED");
    }
}