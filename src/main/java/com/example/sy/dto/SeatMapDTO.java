package com.example.sy.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class SeatMapDTO {
    private Long busId;
    private LocalDate travelDate;
    private int totalSeats;
    private List<SeatDTO> seats;
}