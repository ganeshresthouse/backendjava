//package com.example.microshop.service;
//
//import java.time.LocalDate;
//
//import org.springframework.stereotype.Service;
//
//import com.example.microshop.DTO.BookingRequest;
//import com.example.microshop.entity.Booking;
//import com.example.microshop.entity.Room;
//import com.example.microshop.entity.RoomType;
//import com.example.microshop.repository.BookingRepository;
//import com.example.microshop.repository.RoomRepository;
//
//import lombok.RequiredArgsConstructor;
//
//@Service
//@RequiredArgsConstructor
//public class BookingService {
//
//    private final BookingRepository bookingRepository;
//    private final RoomRepository roomRepository;
//    private final RazorpayService razorpayService;
//
//    // ================= CREATE BOOKING =================
//    public Booking createBooking(BookingRequest request) {
//
//        Room room = roomRepository.findById(request.getRoomId())
//                .orElseThrow(() -> new RuntimeException("Room not found"));
//
//        int guests = request.getGuests();
//
//        // 🔒 GUEST VALIDATION (FINAL BUSINESS RULES)
//        validateGuests(room.getRoomType(), guests);
//
//        LocalDate checkIn = LocalDate.parse(request.getCheckIn());
//        LocalDate checkOut = LocalDate.parse(request.getCheckOut());
//
//        // 🚫 DATE-BASED AVAILABILITY CHECK
//        boolean alreadyBooked = bookingRepository.isRoomBooked(
//                room.getId(), checkIn, checkOut
//        );
//
//        if (alreadyBooked) {
//            throw new RuntimeException("Room already booked for selected dates");
//        }
//
//        // 💰 CALCULATE AMOUNT (ADMIN PRICE)
//        double amount = razorpayService.calculateAmount(room, checkIn, checkOut);
//
//        Booking booking = new Booking();
//        booking.setRoom(room);
//        booking.setCheckIn(checkIn);
//        booking.setCheckOut(checkOut);
//        booking.setGuests(guests);
//        booking.setFullName(request.getFullName());
//        booking.setEmail(request.getEmail());
//        booking.setPhone(request.getPhone());
//        booking.setAmount(amount);
//        booking.setStatus("PENDING_PAYMENT");
//
//        return bookingRepository.save(booking);
//    }
//
//    // ================= GUEST VALIDATION LOGIC =================
//    private void validateGuests(RoomType roomType, int guests) {
//
//        if (guests <= 0) {
//            throw new RuntimeException("Invalid guest count");
//        }
//
//        switch (roomType) {
//
//            // 1 guest only
//            case DORMITORY -> {
//                if (guests != 1) {
//                    throw new RuntimeException("Dormitory allows only 1 guest");
//                }
//            }
//
//            // 1 or 2 guests
//            case SINGLE -> {
//                if (guests < 1 || guests > 2) {
//                    throw new RuntimeException("Single room allows max 2 guests");
//                }
//            }
//
//            // 1, 2, or 3 guests
//            case DOUBLE -> {
//                if (guests < 1 || guests > 3) {
//                    throw new RuntimeException("Double room allows max 3 guests");
//                }
//              
//            }
////             default -> {
////                if (guests >4) {
////                    throw new RuntimeException("please book two roooms");
////                }
////              
////            }
//        }
//    }
//}


package com.example.microshop.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.microshop.DTO.BookingRequest;
import com.example.microshop.entity.Booking;
import com.example.microshop.entity.Room;
import com.example.microshop.entity.RoomType;
import com.example.microshop.repository.BookingRepository;
import com.example.microshop.repository.RoomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final RazorpayService razorpayService;

    // ================= CREATE BOOKING =================
    public Booking createBooking(BookingRequest request) {

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        int guests = request.getGuests();

        // 🔒 Guest validation
        validateGuests(room.getRoomType(), guests);

        LocalDate checkIn = LocalDate.parse(request.getCheckIn());
        LocalDate checkOut = LocalDate.parse(request.getCheckOut());

        boolean alreadyBooked = bookingRepository.isRoomBooked(
                room.getId(), checkIn, checkOut
        );

        if (alreadyBooked) {
            throw new RuntimeException("Room already booked for selected dates");
        }

        double amount = razorpayService.calculateAmount(room, checkIn, checkOut);

        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setCheckIn(checkIn);
        booking.setCheckOut(checkOut);
        booking.setGuests(guests);
        booking.setFullName(request.getFullName());
        booking.setEmail(request.getEmail());
        booking.setPhone(request.getPhone());
        booking.setAmount(amount);
        booking.setStatus("PENDING_PAYMENT");

        return bookingRepository.save(booking);
    }

    // ================= DASHBOARD =================
    public long getTodayBookingsCount() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        return bookingRepository.countTodayBookings(start, end);
    }

    // ================= GUEST VALIDATION =================
    private void validateGuests(RoomType roomType, int guests) {

        if (guests <= 0) {
            throw new RuntimeException("Invalid guest count");
        }

        switch (roomType) {
            case DORMITORY -> {
                if (guests != 1) {
                    throw new RuntimeException("Dormitory allows only 1 guest");
                }
            }
            case SINGLE -> {
                if (guests < 1 || guests > 2) {
                    throw new RuntimeException("Single room allows max 2 guests");
                }
            }
            case DOUBLE -> {
                if (guests < 1 || guests > 3) {
                    throw new RuntimeException("Double room allows max 3 guests");
                }
            }
        }
    }
}
