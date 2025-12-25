////package com.example.microshop.repository;
////
////import org.springframework.data.jpa.repository.*;
////import org.springframework.stereotype.Repository;
////import com.example.microshop.entity.PaymentRecord;
////
////@Repository
////public interface PaymentRecordRepository
////        extends JpaRepository<PaymentRecord, Long> {
////
////    // DAILY
////    @Query("""
////        SELECT COALESCE(SUM(p.amount), 0)
////        FROM PaymentRecord p
////        WHERE DATE(p.createdAt) = CURRENT_DATE
////        AND p.status = 'SUCCESS'
////    """)
////    Double dailyRevenue();
////
////    // WEEKLY
////    @Query("""
////        SELECT COALESCE(SUM(p.amount), 0)
////        FROM PaymentRecord p
////        WHERE YEARWEEK(p.createdAt, 1) = YEARWEEK(CURRENT_DATE, 1)
////        AND p.status = 'SUCCESS'
////    """)
////    Double weeklyRevenue();
////
////    // MONTHLY
////    @Query("""
////        SELECT COALESCE(SUM(p.amount), 0)
////        FROM PaymentRecord p
////        WHERE MONTH(p.createdAt) = MONTH(CURRENT_DATE)
////        AND YEAR(p.createdAt) = YEAR(CURRENT_DATE)
////        AND p.status = 'SUCCESS'
////    """)
////    Double monthlyRevenue();
////
////    // YEARLY
////    @Query("""
////        SELECT COALESCE(SUM(p.amount), 0)
////        FROM PaymentRecord p
////        WHERE YEAR(p.createdAt) = YEAR(CURRENT_DATE)
////        AND p.status = 'SUCCESS'
////    """)
////    Double yearlyRevenue();
////}
//
//
//
//package com.example.microshop.repository;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//
//import com.example.microshop.entity.PaymentRecord;
//
//@Repository
//public interface PaymentRecordRepository
//        extends JpaRepository<PaymentRecord, Long> {
//
//    // 📅 DAILY
//    @Query("""
//        SELECT COALESCE(SUM(p.amount), 0)
//        FROM PaymentRecord p
//        WHERE p.createdAt >= CURRENT_DATE
//          AND p.createdAt < CURRENT_DATE + 1
//          AND p.status = 'SUCCESS'
//    """)
//    Double dailyRevenue();
//
//    // 📅 WEEKLY
//    @Query("""
//        SELECT COALESCE(SUM(p.amount), 0)
//        FROM PaymentRecord p
//        WHERE p.createdAt >= date_trunc('week', CURRENT_DATE)
//          AND p.createdAt < date_trunc('week', CURRENT_DATE) + INTERVAL '7 days'
//          AND p.status = 'SUCCESS'
//    """)
//    Double weeklyRevenue();
//
//    // 📅 MONTHLY
//    @Query("""
//        SELECT COALESCE(SUM(p.amount), 0)
//        FROM PaymentRecord p
//        WHERE p.createdAt >= date_trunc('month', CURRENT_DATE)
//          AND p.createdAt < date_trunc('month', CURRENT_DATE) + INTERVAL '1 month'
//          AND p.status = 'SUCCESS'
//    """)
//    Double monthlyRevenue();
//
//    // 📅 YEARLY
//    @Query("""
//        SELECT COALESCE(SUM(p.amount), 0)
//        FROM PaymentRecord p
//        WHERE p.createdAt >= date_trunc('year', CURRENT_DATE)
//          AND p.status = 'SUCCESS'
//    """)
//    Double yearlyRevenue();
//}


package com.example.microshop.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.microshop.entity.PaymentRecord;

public interface PaymentRecordRepository
        extends JpaRepository<PaymentRecord, Long> {

    @Query("""
        SELECT COALESCE(SUM(p.amount), 0)
        FROM PaymentRecord p
        WHERE p.createdAt >= :start
          AND p.createdAt < :end
          AND p.status = 'SUCCESS'
    """)
    Double revenueBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
