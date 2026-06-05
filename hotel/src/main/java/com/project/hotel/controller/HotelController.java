package com.project.hotel.controller;


import com.project.hotel.dto.*;
import com.project.hotel.services.impl.HotelService;
import com.project.hotel.services.impl.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;
    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<HotelRes> createHotel(@RequestBody @Valid HotelReq req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hotelService.createHotel(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelRes> getHotelById(@PathVariable Long id) {
        return ResponseEntity.ok(hotelService.getHotelById(id));
    }

    @GetMapping("/location/{location}")
    public ResponseEntity<List<HotelRes>> getHotelsByLocation(@PathVariable String location) {
        return ResponseEntity.ok(hotelService.getHotelsByLocation(location));
    }

    @PostMapping("/rooms")
    public ResponseEntity<RoomRes> createRoom(@RequestBody @Valid RoomReq req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.createRoom(req));
    }

    @GetMapping("/rooms/{roomId}/availability")
    public ResponseEntity<AvailabilityRes> checkAvailability(@PathVariable Long roomId) {
        return ResponseEntity.ok(roomService.checkAvailability(roomId));
    }
}