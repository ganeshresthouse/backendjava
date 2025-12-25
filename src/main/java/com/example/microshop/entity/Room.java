package com.example.microshop.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomNumber;

    @Enumerated(EnumType.STRING)
    private RoomType roomType;
    private double pricePerNight;   
    private String status; // ✅ ADD THIS (ACTIVE, INACTIVE, MAINTENANCE)
    private String imageUrl;


}
