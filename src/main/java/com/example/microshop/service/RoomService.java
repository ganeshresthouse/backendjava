package com.example.microshop.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.microshop.entity.Room;
import com.example.microshop.entity.RoomType;
import com.example.microshop.repository.BookingRepository;
import com.example.microshop.repository.RoomRepository;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    public List<Room> searchAvailableRooms(
            LocalDate checkIn,
            LocalDate checkOut,
            int guests) {

        // ✅ validations
        if (guests <= 0) {
            throw new RuntimeException("Invalid guest count");
        }

        if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
            throw new RuntimeException("Invalid check-in / check-out dates");
        }

        // ✅ guest → room type rules
        List<RoomType> allowedTypes;

        switch (guests) {
            case 1 -> allowedTypes = List.of(
                    RoomType.SINGLE,
                    RoomType.DOUBLE,
                    RoomType.DORMITORY
            );
            case 2 -> allowedTypes = List.of(
                    RoomType.SINGLE,
                    RoomType.DOUBLE
            );
            case 3 -> allowedTypes = List.of(
                    RoomType.DOUBLE
            );
            default -> throw new RuntimeException(
                    "For more than 3 guests, please book two rooms"
            );
        }

        // ✅ IMPORTANT FIX: use ACTIVE (matches DB)
        List<Room> activeRooms =
                roomRepository.findByRoomTypeInAndStatus(
                        allowedTypes, "ACTIVE");

        // ✅ remove already booked rooms (date overlap)
        return activeRooms.stream()
                .filter(room ->
                        !bookingRepository.isRoomBooked(
                                room.getId(), checkIn, checkOut))
                .toList();
    }
}
