package com.example.microshop.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime createdAt; // 🔥 ADD THIS
    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now(); // 🔥 REQUIRED
    }

    // ================= ROOM =================
    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    // ================= DATES =================
    private LocalDate checkIn;
    private LocalDate checkOut;

    // ================= GUEST DETAILS =================
    private String fullName;
    private String email;
    private String phone;
    private int guests;
    private Double amount;

    // ================= STATUS =================
    private String status; // BOOKED / CANCELLED
    @Column(unique = true)
    private String razorpayOrderId;

    private String razorpayPaymentId;



	
}
