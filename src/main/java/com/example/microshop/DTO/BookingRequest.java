package com.example.microshop.DTO;

import lombok.Data;

@Data
public class BookingRequest {

    private Long roomId;

    private String checkIn;   // yyyy-MM-dd
    private String checkOut;  // yyyy-MM-dd

    private int guests;

    private String fullName;
    private String email;
    private String phone;
}
