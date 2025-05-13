package com.example.sy.service;

import java.util.Map;

public interface DashboardService {
    long getTotalBookings();
    double getTotalRevenue();
    long getActiveBuses();
    long getRegisteredUsers();
    Map<String, Double> getMonthlyRevenue();
    Map<String, Double> getBusOccupancy();
}