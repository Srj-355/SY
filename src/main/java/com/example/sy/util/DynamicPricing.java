package com.example.sy.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;


import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class DynamicPricing {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final double MIN_PRICE_FACTOR = 0.8;  // Minimum 80% of base fare
    private static final double MAX_PRICE_FACTOR = 1.5;  // Maximum 150% of base fare

    private Map<String, Double> holidayMultipliers;
    private double baseFare;
    private int totalSeats ;
    private int bookedSeats;

    public DynamicPricing(String holidayFilePath, int totalSeats) {
        this.totalSeats = totalSeats;
        this.holidayMultipliers = loadHolidayMultipliers(holidayFilePath);
    }

    public void setBaseFare(double baseFare) {
        this.baseFare = baseFare;
    }

    public void setBookedSeats(int bookedSeats) {
        this.bookedSeats = bookedSeats;
    }

//    public double getFare(String dateString) {
//        LocalDate date = LocalDate.parse(dateString, DATE_FORMATTER);
//
//
//        // 1. Calculate BASE price factors (time, day, holiday)
//        double basePrice = baseFare * calculateTimeFactor(date)
//                * calculateDayFactor(date)
//                * calculateHolidayFactor(dateString);
//
//        // 2. Apply occupancy factor SEPARATELY with stronger weighting
//        double occupancyFactor = calculateOccupancyFactor();
//        double dynamicPrice = basePrice * occupancyFactor;
//
//        // 3. Apply absolute min/max bounds
//        return Math.max(baseFare * MIN_PRICE_FACTOR,
//                Math.min(baseFare * MAX_PRICE_FACTOR, dynamicPrice));
//    }

    public double getFare(String dateString) {
        LocalDate date = LocalDate.parse(dateString, DATE_FORMATTER);

        // Calculate all factors
        double timeFactor = calculateTimeFactor(date);
        double dayFactor = calculateDayFactor(date);
        double holidayFactor = calculateHolidayFactor(dateString);
        double occupancyFactor = calculateOccupancyFactor();

        System.out.println("\n--- Pricing Calculation Debug ---");
        System.out.println("Base Fare: " + baseFare);
        System.out.println("Time Factor: " + timeFactor);
        System.out.println("Day Factor: " + dayFactor);
        System.out.println("Holiday Factor: " + holidayFactor);
        System.out.println("Occupancy (" + bookedSeats + "/" + totalSeats + "): " + occupancyFactor);

        double dynamicPrice = baseFare * timeFactor * dayFactor * holidayFactor * occupancyFactor;
        double finalPrice = Math.max(baseFare * MIN_PRICE_FACTOR,
                Math.min(baseFare * MAX_PRICE_FACTOR, dynamicPrice));

        System.out.println("Calculated Price: " + dynamicPrice);
        System.out.println("Final Price: " + finalPrice);
        System.out.println("--------------------------------\n");

        return finalPrice;
    }

    private double calculateTimeFactor(LocalDate date) {
        long daysUntilDeparture = LocalDate.now().until(date).getDays();

        // More pronounced time-based pricing
        if (daysUntilDeparture <= 1) return 1.5; // 50% increase for last-minute
        if (daysUntilDeparture <= 3) return 1.3;
        if (daysUntilDeparture <= 7) return 1.15;
        if (daysUntilDeparture <= 14) return 1.0;
        if (daysUntilDeparture <= 30) return 0.85; // 15% early bird discount
        return 0.75; // 25% discount for very early booking
    }

    private double calculateDayFactor(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();

        // Higher prices on weekends and Fridays
        if (day == DayOfWeek.FRIDAY || day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            return 1.15;
        }
        return 1.0;
    }

    private double calculateHolidayFactor(String dateString) {
        return holidayMultipliers.getOrDefault(dateString, 1.0);
    }

    private double calculateOccupancyFactor() {
        if (totalSeats == 0) return 1.0; // safety check

        double occupancyRate = (double) bookedSeats / totalSeats;

        // More aggressive discounts for empty buses
        if (occupancyRate < 0.1) return 0.7; // 30% discount for <10% full
        if (occupancyRate < 0.3) return 0.8; // 20% discount for <30% full
        if (occupancyRate < 0.5) return 0.9; // 10% discount for <50% full
        if (occupancyRate < 0.7) return 1.0; // standard price
        if (occupancyRate < 0.9) return 1.1; // 10% premium
        return 1.3; // 30% premium for >90% full
    }

    private Map<String, Double> loadHolidayMultipliers(String filePath) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> months = mapper.readValue(new File(filePath),
                    new TypeReference<List<Map<String, Object>>>() {});

            Map<String, Double> multipliers = new HashMap<>();
            for (Map<String, Object> month : months) {
                List<Map<String, String>> monthHolidays = (List<Map<String, String>>) month.get("holidays");
                for (Map<String, String> holiday : monthHolidays) {
                    String date = holiday.get("adDate");
                    double multiplier = Double.parseDouble(holiday.getOrDefault("multiplier", "1.2"));
                    multipliers.put(date, multiplier);
                }
            }
            return multipliers;
        } catch (Exception e) {
            System.err.println("Error loading holiday multipliers: " + e.getMessage());
            return new HashMap<>();
        }
    }

}

