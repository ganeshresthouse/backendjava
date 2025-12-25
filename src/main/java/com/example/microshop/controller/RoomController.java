package com.example.microshop.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.microshop.entity.Room;
import com.example.microshop.entity.RoomType;
import com.example.microshop.repository.RoomRepository;
import com.example.microshop.service.RoomService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@CrossOrigin
public class RoomController {

    private final RoomService roomService;
    private final RoomRepository roomRepo;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.base-url}")
    private String baseUrl;

    // ================= ADD ROOM (IMAGE + DATA) =================
    @PostMapping(
        value = "/addrooms",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Room addRoom(
            @RequestParam String roomNumber,
            @RequestParam double pricePerNight,
            @RequestParam String roomType,
            @RequestParam(required = false) String status,
            @RequestParam MultipartFile image
    ) throws Exception {

        if (image == null || image.isEmpty()) {
            throw new RuntimeException("Image is required");
        }

        Files.createDirectories(Paths.get(uploadDir));

        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        Path path = Paths.get(uploadDir + fileName);
        Files.write(path, image.getBytes());

        Room room = new Room();
        room.setRoomNumber(roomNumber);
        room.setPricePerNight(pricePerNight);
        room.setRoomType(RoomType.valueOf(roomType.toUpperCase()));
        room.setStatus(status == null ? "ACTIVE" : status.toUpperCase());
        room.setImageUrl(baseUrl + fileName);

        return roomRepo.save(room);
    }

    // ================= GET ALL ROOMS =================
    @GetMapping("/getallrooms")
    public List<Room> getAllRooms(
            @RequestParam(required = false) String status) {

        return status != null
                ? roomRepo.findByStatus(status)
                : roomRepo.findAll();
    }

    // ================= UPDATE ROOM (NO IMAGE) =================
    @PutMapping("/updateroom/{id}")
    public Room updateRoom(
            @PathVariable Long id,
            @RequestBody Room updated) {

        Room room = roomRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        room.setRoomNumber(updated.getRoomNumber());
        room.setPricePerNight(updated.getPricePerNight());
        room.setRoomType(updated.getRoomType());

        return roomRepo.save(room);
    }

    // ================= UPDATE ROOM WITH IMAGE =================
    @PutMapping(
        value = "/updateroom-with-image/{id}",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Room updateRoomWithImage(
            @PathVariable Long id,
            @RequestParam String roomNumber,
            @RequestParam double pricePerNight,
            @RequestParam String roomType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) MultipartFile image
    ) throws Exception {

        Room room = roomRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        room.setRoomNumber(roomNumber);
        room.setPricePerNight(pricePerNight);
        room.setRoomType(RoomType.valueOf(roomType.toUpperCase()));
        room.setStatus(status == null ? room.getStatus() : status.toUpperCase());

        // update image only if provided
        if (image != null && !image.isEmpty()) {

            Files.createDirectories(Paths.get(uploadDir));

            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            Path path = Paths.get(uploadDir + fileName);
            Files.write(path, image.getBytes());

            room.setImageUrl(baseUrl + fileName);
        }

        return roomRepo.save(room);
    }

    // ================= CHANGE STATUS =================
    @PatchMapping("/{id}/status")
    public Room changeStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        Room room = roomRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        room.setStatus(status.toUpperCase());
        return roomRepo.save(room);
    }

    // ================= DELETE ROOM =================
    @DeleteMapping("/delete/{id}")
    public void deleteRoom(@PathVariable Long id) {
        roomRepo.deleteById(id);
    }

    @GetMapping("/search")
    public List<Room> searchRooms(
            @RequestParam LocalDate checkIn,
            @RequestParam LocalDate checkOut,
            @RequestParam int guests) {

        return roomService.searchAvailableRooms(checkIn, checkOut, guests);
    }
    
}
