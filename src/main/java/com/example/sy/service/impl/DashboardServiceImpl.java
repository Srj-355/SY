package com.example.sy.service.impl;

import com.example.sy.repository.BookingRepository;
import com.example.sy.repository.BusRepository;
import com.example.sy.repository.UserRepository;
import com.example.sy.service.DashboardService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {
    private final BookingRepository bookingRepository;
    private final BusRepository busRepository;
    private final UserRepository userRepository;

    public DashboardServiceImpl(BookingRepository bookingRepository,
                              BusRepository busRepository,
                              UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.busRepository = busRepository;
        this.userRepository = userRepository;
    }

    @Override
    public long getTotalBookings() {
        return bookingRepository.count();
    }

    @Override
    public double getTotalRevenue() {
        Double total = bookingRepository.sumTotalAmount();
        return total != null ? total : 0.0;
    }

    @Override
    public long getActiveBuses() {
        return busRepository.countByIsActive(true);
    }

    @Override
    public long getRegisteredUsers() {
        return userRepository.count();
    }

    @Override
    public Map<String, Double> getMonthlyRevenue() {
        return bookingRepository.getMonthlyRevenue();
    }

    @Override
    public Map<String, Double> getBusOccupancy() {
        List<Map<String, Object>> results = bookingRepository.getBusOccupancy();
        Map<String, Double> occupancyMap = new HashMap<>();
        for (Map<String, Object> result : results) {
            String busName = (String) result.get("busName");
            Long bookings = (Long) result.get("bookings");
            occupancyMap.put(busName, bookings.doubleValue());
        }
        return occupancyMap;
    }
}