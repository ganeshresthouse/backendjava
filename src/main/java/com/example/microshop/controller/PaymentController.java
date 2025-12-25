package com.example.microshop.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

import com.example.microshop.entity.Booking;
import com.example.microshop.repository.BookingRepository;
import com.example.microshop.service.RazorpayService;
import com.razorpay.Order;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@CrossOrigin
public class PaymentController {

    private final BookingRepository bookingRepository;
    private final RazorpayService razorpayService;

    // ================= CREATE RAZORPAY ORDER =================
    @PostMapping(value = "/create/{bookingId}", produces = "application/json")
    public Map<String, Object> createPaymentOrder(@PathVariable Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // 🔐 Create Razorpay Order
        Order order = razorpayService.createOrder(booking.getAmount());

        // 🔥 SAVE ORDER ID (CRITICAL)
        booking.setRazorpayOrderId(order.get("id"));
        bookingRepository.save(booking);

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", order.get("id"));
        response.put("amount", order.get("amount"));
        response.put("currency", order.get("currency"));
        response.put("status", order.get("status"));

        return response;
    }
}
