package com.example.sy.dto;

import lombok.Data;

@Data
public class SeatDTO {
    private String number;
    private boolean available;
    private boolean premium;
}