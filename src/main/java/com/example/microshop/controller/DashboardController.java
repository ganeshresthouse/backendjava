package com.example.microshop.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.microshop.DTO.BookingAdminDTO;
import com.example.microshop.repository.BookingRepository;
import com.example.microshop.service.DashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final BookingRepository bookingRepo;

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboardData());
    }
    
    @GetMapping("/bookings")
    public List<BookingAdminDTO> getBookings(
            @RequestParam(required = false) String status) {

        if (status != null) {
            return bookingRepo.getBookingsByStatus(status);
        }
        return bookingRepo.getAllBookings();
    }
}
