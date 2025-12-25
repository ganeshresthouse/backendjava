package com.example.microshop.DTO;

import java.time.LocalDate;

import com.example.microshop.entity.RoomType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingAdminDTO {

    private Long id;
    private String guestName;
    private String roomNumber;
    private RoomType roomType;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Double amount;
    private String status;

    // ✅ THIS CONSTRUCTOR IS REQUIRED FOR JPQL
    public BookingAdminDTO(
            Long id,
            String guestName,
            String roomNumber,
            RoomType roomType,
            LocalDate checkIn,
            LocalDate checkOut,
            Double amount,
            String status
    ) {
        this.id = id;
        this.guestName = guestName;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.amount = amount;
        this.status = status;
    }
}
