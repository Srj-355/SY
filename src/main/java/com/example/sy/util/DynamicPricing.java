package com.example.sy.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class DynamicPricing {
    private Map<String, Boolean> holidayMap;
    private double baseFare;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public DynamicPricing(String holidayFilePath, double baseFare) {
        this.baseFare = baseFare;
        this.holidayMap = loadHolidays(holidayFilePath);
    }

    private Map<String, Boolean> loadHolidays(String filePath) {
        Map<String, Boolean> holidays = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        try {
            List<Map<String, Object>> months = mapper.readValue(new File(filePath), 
                new TypeReference<List<Map<String, Object>>>() {});

            for (Map<String, Object> month : months) {
                List<Map<String, String>> monthHolidays = (List<Map<String, String>>) month.get("holidays");
                for (Map<String, String> holiday : monthHolidays) {
                    holidays.put(holiday.get("adDate"), true);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading holiday file: " + e.getMessage());
        }

        return holidays;
    }

    public double getFare(String date) {
        try {
            // Validate date format
            LocalDate.parse(date, DATE_FORMATTER);
            
            if (holidayMap.containsKey(date)) {
                // Apply holiday pricing (20% discount in this example)
                return baseFare * 0.8;
            } else {
                // Regular pricing
                return baseFare;
            }
        } catch (Exception e) {
            System.err.println("Invalid date format. Expected yyyy-MM-dd: " + date);
            return baseFare; // Return base fare if date is invalid
        }
    }

    public double getFare(LocalDate date) {
        String dateStr = date.format(DATE_FORMATTER);
        return getFare(dateStr);
    }

    public double getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(double baseFare) {
        this.baseFare = baseFare;
    }
}