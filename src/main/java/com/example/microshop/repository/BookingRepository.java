//package com.example.microshop.repository;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import com.example.microshop.DTO.BookingAdminDTO; // ✅ MATCH PACKAGE
//import com.example.microshop.entity.Booking;
//
//public interface BookingRepository extends JpaRepository<Booking, Long> {
//
//    // 🔒 Check if room already booked (date overlap)
//    @Query("""
//        SELECT COUNT(b) > 0
//        FROM Booking b
//        WHERE b.room.id = :roomId
//          AND b.status = 'CONFIRMED'
//          AND (:checkIn < b.checkOut AND :checkOut > b.checkIn)
//    """)
//    boolean isRoomBooked(
//            @Param("roomId") Long roomId,
//            @Param("checkIn") LocalDate checkIn,
//            @Param("checkOut") LocalDate checkOut
//    );
//
//    // 🔎 Find booking by Razorpay order id
//    Optional<Booking> findByRazorpayOrderId(String razorpayOrderId);
//
//    // 📊 Count today's confirmed bookings
//    @Query("""
//        SELECT COUNT(b)
//        FROM Booking b
//        WHERE DATE(b.createdAt) = CURRENT_DATE
//          AND b.status = 'CONFIRMED'
//    """)
//    long countTodayBookings();
//
//    // 📋 ALL BOOKINGS (ADMIN LIST)
//    @Query("""
//        SELECT new com.example.microshop.DTO.BookingAdminDTO(
//            b.id,
//            b.fullName,
//            r.roomNumber,
//            r.roomType,
//            b.checkIn,
//            b.checkOut,
//            b.amount,
//            b.status
//        )
//        FROM Booking b
//        JOIN b.room r
//        ORDER BY b.createdAt DESC
//    """)
//    List<BookingAdminDTO> getAllBookings();
//
//    // 🔎 BOOKINGS BY STATUS (ADMIN FILTER)
//    @Query("""
//        SELECT new com.example.microshop.DTO.BookingAdminDTO(
//            b.id,
//            b.fullName,
//            r.roomNumber,
//            r.roomType,
//            b.checkIn,
//            b.checkOut,
//            b.amount,
//            b.status
//        )
//        FROM Booking b
//        JOIN b.room r
//        WHERE b.status = :status
//        ORDER BY b.createdAt DESC
//    """)
//    List<BookingAdminDTO> getBookingsByStatus(@Param("status") String status);
//}



package com.example.microshop.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.microshop.DTO.BookingAdminDTO;
import com.example.microshop.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // 🔒 Check room availability (DATE OVERLAP) ✅ WORKS
    @Query("""
        SELECT COUNT(b) > 0
        FROM Booking b
        WHERE b.room.id = :roomId
          AND b.status = 'CONFIRMED'
          AND (:checkIn < b.checkOut AND :checkOut > b.checkIn)
    """)
    boolean isRoomBooked(
            @Param("roomId") Long roomId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );

    // 🔎 Razorpay order lookup
    Optional<Booking> findByRazorpayOrderId(String razorpayOrderId);

    // 📊 TODAY'S CONFIRMED BOOKINGS (FIXED)
    @Query("""
        SELECT COUNT(b)
        FROM Booking b
        WHERE b.createdAt >= :start
          AND b.createdAt < :end
          AND b.status = 'CONFIRMED'
    """)
    long countTodayBookings(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // 📋 ALL BOOKINGS (ADMIN)
    @Query("""
        SELECT new com.example.microshop.DTO.BookingAdminDTO(
            b.id,
            b.fullName,
            r.roomNumber,
            r.roomType,
            b.checkIn,
            b.checkOut,
            b.amount,
            b.status
        )
        FROM Booking b
        JOIN b.room r
        ORDER BY b.createdAt DESC
    """)
    List<BookingAdminDTO> getAllBookings();

    // 🔎 BOOKINGS BY STATUS (ADMIN)
    @Query("""
        SELECT new com.example.microshop.DTO.BookingAdminDTO(
            b.id,
            b.fullName,
            r.roomNumber,
            r.roomType,
            b.checkIn,
            b.checkOut,
            b.amount,
            b.status
        )
        FROM Booking b
        JOIN b.room r
        WHERE b.status = :status
        ORDER BY b.createdAt DESC
    """)
    List<BookingAdminDTO> getBookingsByStatus(@Param("status") String status);
}
