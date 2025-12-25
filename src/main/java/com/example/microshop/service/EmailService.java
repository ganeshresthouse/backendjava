package com.example.microshop.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendBookingConfirmation(
            String toEmail,
            String customerName,
            String roomType,
            String checkIn,
            String checkOut,
            String bookingId
    ) {
        try {
            System.out.println("📧 Sending booking email to: " + toEmail);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("ganeshresthouse576@gmail.com"); // 🔥 MUST
            message.setTo(toEmail);
            message.setSubject("🏨 Ganesh Rest House - Booking Confirmed");

            message.setText(
                "Dear " + customerName + ",\n\n" +
                "Your room booking is CONFIRMED ✅\n\n" +
                "Booking ID: " + bookingId + "\n" +
                "Room Type: " + roomType + "\n" +
                "Check-in: " + checkIn + "\n" +
                "Check-out: " + checkOut + "\n\n" +
                "Thank you for choosing Ganesh Rest House.\n" +
                "📞 Contact: +91-8096905269\n\n" +
                "Regards,\nGanesh Rest House"
            );

            mailSender.send(message);

            System.out.println("✅ Booking confirmation email SENT");

        } catch (Exception e) {
            System.err.println("❌ Email sending failed");
            e.printStackTrace(); // 🔥 IMPORTANT
        }
    }
}
