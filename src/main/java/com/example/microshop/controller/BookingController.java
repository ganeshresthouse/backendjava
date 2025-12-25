package com.example.microshop.controller;

import org.springframework.web.bind.annotation.*;

import com.example.microshop.DTO.BookingRequest;
import com.example.microshop.entity.Booking;
import com.example.microshop.service.BookingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@CrossOrigin
public class BookingController {

    private final BookingService bookingService;

    // ✅ CREATE BOOKING (PAYMENT PENDING)
    @PostMapping("/book")
    public Booking bookRoom(@RequestBody BookingRequest request) {
        return bookingService.createBooking(request);
    }
}
