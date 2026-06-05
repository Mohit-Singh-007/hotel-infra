package com.project.booking.controller;

import com.project.booking.dto.BookingReq;
import com.project.booking.dto.BookingRes;
import com.project.booking.service.impl.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingRes> createBooking(@RequestBody @Valid BookingReq req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingRes> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingRes>> getBookingsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getBookingByUserId(userId));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<BookingRes> cancelBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }
}