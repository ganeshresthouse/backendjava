package com.example.microshop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.microshop.entity.Room;
import com.example.microshop.entity.RoomType;

public interface RoomRepository extends JpaRepository<Room, Long> {
	List<Room> findByRoomTypeIn(List<RoomType> roomTypes);

    // 🔍 used in search
    List<Room> findByRoomTypeInAndStatus(
            List<RoomType> roomTypes, String status);

    long countByStatus(String status);

    List<Room> findByStatus(String status);
}
