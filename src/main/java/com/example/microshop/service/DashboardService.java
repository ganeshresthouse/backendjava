package com.example.microshop.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.microshop.repository.BookingRepository;
import com.example.microshop.repository.PaymentRecordRepository;
import com.example.microshop.repository.RoomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final RoomRepository roomRepo;
    private final BookingRepository bookingRepo;
    private final PaymentRecordRepository paymentRepo;

    public Map<String, Object> getDashboardData() {

        Map<String, Object> data = new HashMap<>();

        long totalRooms = roomRepo.count();
        long active = roomRepo.countByStatus("ACTIVE");
        long inactive = roomRepo.countByStatus("INACTIVE");
        long maintenance = roomRepo.countByStatus("MAINTENANCE");

        // ✅ Date ranges (JPQL safe)
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);

        LocalDateTime weekStart = todayStart.minusDays(
                todayStart.getDayOfWeek().getValue() - 1);
        LocalDateTime monthStart = todayStart.withDayOfMonth(1);
        LocalDateTime yearStart = todayStart.withDayOfYear(1);

        // ✅ New bookings today
        long newBookings =
                bookingRepo.countTodayBookings(todayStart, todayEnd);

        // ✅ Occupancy %
        int occupancy = totalRooms == 0
                ? 0
                : (int) ((inactive * 100.0) / totalRooms);

        // ✅ Revenue
        Map<String, Object> revenue = new HashMap<>();
        revenue.put("daily", paymentRepo.revenueBetween(todayStart, todayEnd));
        revenue.put("weekly", paymentRepo.revenueBetween(weekStart, todayEnd));
        revenue.put("monthly", paymentRepo.revenueBetween(monthStart, todayEnd));
        revenue.put("yearly", paymentRepo.revenueBetween(yearStart, todayEnd));

        // ✅ Response
        data.put("revenue", revenue);
        data.put("newBookings", newBookings);
        data.put("occupancy", occupancy);
        data.put("totalRooms", totalRooms);
        data.put("activeRooms", active);
        data.put("inactiveRooms", inactive);
        data.put("maintenanceRooms", maintenance);

        return data;
    }
}
