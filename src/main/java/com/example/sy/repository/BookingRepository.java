package com.example.sy.repository;

import com.example.sy.model.Booking;
import com.example.sy.model.Bus;
import com.example.sy.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser(User user);
    Optional<Booking> findByTransactionId(String transactionId);
    Optional<Booking> findById(Long id);
    Page<Booking> findAll(Pageable pageable);
    List<Booking> findByBusAndTravelDate(Bus bus, String travelDate);
    Page<Booking> findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(String firstName, String lastName, Pageable pageable);
    List<Booking> findByBusAndTravelDateAndStatus(Bus bus, String travelDate, String status);

    @Query("SELECT SUM(b.totalAmount) FROM Booking b")
    Double sumTotalAmount();

    @Query("SELECT to_char(b.paymentDate, 'YYYY-MM') as month, SUM(b.totalAmount) as revenue " +
           "FROM Booking b GROUP BY to_char(b.paymentDate, 'YYYY-MM')")
    Map<String, Double> getMonthlyRevenue();

    @Query("SELECT new map(b.bus.busName as busName, COUNT(b.id) as bookings) " +
           "FROM Booking b GROUP BY b.bus.busName")
    List<Map<String, Object>> getBusOccupancy();
}