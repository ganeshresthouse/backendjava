package com.example.microshop.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.microshop.entity.Room;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;

@Service
public class RazorpayService {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    // ================= CREATE RAZORPAY ORDER =================
    public Order createOrder(double amount) {
        try {
            RazorpayClient client = new RazorpayClient(keyId, keySecret);

            JSONObject options = new JSONObject();
            options.put("amount", Math.round(amount * 100)); // INR → paise
            options.put("currency", "INR");
            options.put("receipt", "booking_" + System.currentTimeMillis());

            return client.orders.create(options);

        } catch (Exception e) {
            throw new RuntimeException("Razorpay order creation failed", e);
        }
    }

    // ================= CALCULATE BOOKING AMOUNT =================
    public double calculateAmount(Room room, LocalDate checkIn, LocalDate checkOut) {

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);

        if (nights <= 0) {
            throw new RuntimeException("Invalid check-in / check-out dates");
        }

        if (room.getPricePerNight() <= 0) {
            throw new RuntimeException("Room price not configured by admin");
        }

        return nights * room.getPricePerNight();
    }
}
