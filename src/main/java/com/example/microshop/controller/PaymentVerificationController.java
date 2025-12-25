//package com.example.microshop.controller;
//
//import java.util.Map;
//
//import org.json.JSONObject;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.example.microshop.entity.Booking;
//import com.example.microshop.entity.PaymentRecord;
//import com.example.microshop.entity.Room;
//import com.example.microshop.repository.BookingRepository;
//import com.example.microshop.repository.PaymentRecordRepository;
//import com.example.microshop.repository.RoomRepository;
//import com.razorpay.Utils;
//
//import lombok.RequiredArgsConstructor;
//
//@RestController
//@RequestMapping("/api/payment")
//@RequiredArgsConstructor
//@CrossOrigin
//public class PaymentVerificationController {
//
//    private final BookingRepository bookingRepository;
//    private final PaymentRecordRepository paymentRecordRepository;
//    private final RoomRepository roomRepository; // ✅ ADD THIS
//
//    @Value("${razorpay.key.secret}")
//    private String keySecret;
//
//    @Transactional
//    @PostMapping("/verify")
//    public Map<String, Object> verifyPayment(
//            @RequestBody Map<String, String> payload) {
//
//        String orderId = payload.get("razorpay_order_id");
//        String paymentId = payload.get("razorpay_payment_id");
//        String signature = payload.get("razorpay_signature");
//
//        if (orderId == null || paymentId == null || signature == null) {
//            throw new RuntimeException("Invalid payment payload");
//        }
//
//        // 1️⃣ Find booking
//        Booking booking = bookingRepository
//                .findByRazorpayOrderId(orderId)
//                .orElseThrow(() -> new RuntimeException("Booking not found"));
//
//        // 2️⃣ Verify signature
//        try {
//            JSONObject options = new JSONObject();
//            options.put("razorpay_order_id", orderId);
//            options.put("razorpay_payment_id", paymentId);
//            options.put("razorpay_signature", signature);
//
//            Utils.verifyPaymentSignature(options, keySecret);
//        } catch (Exception e) {
//            throw new RuntimeException("Payment signature verification failed");
//        }
//
//        // 3️⃣ Save payment
//        PaymentRecord payment = PaymentRecord.builder()
//                .amount(booking.getAmount())
//                .razorpayPaymentId(paymentId)
//                .status("SUCCESS")
//                .build();
//        paymentRecordRepository.save(payment);
//
//        // 4️⃣ Confirm booking
//        booking.setStatus("CONFIRMED");
//        booking.setRazorpayPaymentId(paymentId);
//        bookingRepository.save(booking);
//
//        // 🔥 5️⃣ MAKE ROOM INACTIVE (THIS FIXES YOUR ISSUE)
//        Room room = booking.getRoom();
//        room.setStatus("INACTIVE");
//        roomRepository.save(room);
//
//        return Map.of(
//            "message", "Payment verified, booking confirmed, room inactivated",
//            "bookingId", booking.getId(),
//            "roomId", room.getId(),
//            "status", "CONFIRMED"
//        );
//    }
//}


package com.example.microshop.controller;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.example.microshop.entity.Booking;
import com.example.microshop.entity.PaymentRecord;
import com.example.microshop.entity.Room;
import com.example.microshop.repository.BookingRepository;
import com.example.microshop.repository.PaymentRecordRepository;
import com.example.microshop.repository.RoomRepository;
import com.example.microshop.service.EmailService;
import com.razorpay.Utils;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@CrossOrigin
public class PaymentVerificationController {

    private final BookingRepository bookingRepository;
    private final PaymentRecordRepository paymentRecordRepository;
    private final RoomRepository roomRepository;
    private final EmailService emailService; // ✅ EMAIL

    @Value("${razorpay.key.secret}")
    private String keySecret;

    // =====================================================
    // ✅ VERIFY PAYMENT (FINAL FLOW)
    // =====================================================
    @Transactional
    @PostMapping("/verify")
    public Map<String, Object> verifyPayment(
            @RequestBody Map<String, String> payload) {

        String orderId = payload.get("razorpay_order_id");
        String paymentId = payload.get("razorpay_payment_id");
        String signature = payload.get("razorpay_signature");

        if (orderId == null || paymentId == null || signature == null) {
            throw new RuntimeException("Invalid payment payload");
        }

        // 1️⃣ Find booking
        Booking booking = bookingRepository
                .findByRazorpayOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // 2️⃣ Verify Razorpay signature
        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", orderId);
            options.put("razorpay_payment_id", paymentId);
            options.put("razorpay_signature", signature);

            Utils.verifyPaymentSignature(options, keySecret);
        } catch (Exception e) {
            throw new RuntimeException("Payment signature verification failed");
        }

        // 3️⃣ Save payment record
        PaymentRecord payment = PaymentRecord.builder()
                .amount(booking.getAmount())
                .razorpayPaymentId(paymentId)
                .status("SUCCESS")
                .build();
        paymentRecordRepository.save(payment);

        // 4️⃣ Confirm booking
        booking.setStatus("CONFIRMED");
        booking.setRazorpayPaymentId(paymentId);
        bookingRepository.save(booking);

        // 5️⃣ Inactivate room
        Room room = booking.getRoom();
        room.setStatus("INACTIVE");
        roomRepository.save(room);

        // 6️⃣ 📧 SEND CONFIRMATION EMAIL
        emailService.sendBookingConfirmation(
                booking.getEmail(),
                booking.getFullName(),
                room.getRoomType().name(),
                booking.getCheckIn().toString(),
                booking.getCheckOut().toString(),
                booking.getId().toString()
        );

        return Map.of(
            "message", "Payment verified, booking confirmed, email sent",
            "bookingId", booking.getId(),
            "roomId", room.getId(),
            "status", "CONFIRMED"
        );
    }
}
