package com.project.booking.controller;

import com.project.booking.dto.BookingReq;
import com.project.booking.dto.BookingRes;
import com.project.booking.idempotency.IdempotencyService;
import com.project.booking.service.impl.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;
    private final IdempotencyService idempotencyService;

    @PostMapping
    public ResponseEntity<BookingRes> createBooking(@RequestBody @Valid BookingReq req
    ,@RequestHeader(value = "Idempotency-Key",required = false) String idempotencyKey) {

        // if no key -> process normally
        if(idempotencyKey==null || idempotencyKey.isBlank()){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingService.createBooking(req));
        }

        // check in redis for existing res
        Optional<BookingRes> existing = idempotencyService.getResult(idempotencyKey,BookingRes.class);
        if(existing.isPresent()){
            log.info("Returning cached result for Idempotency-Key {}", idempotencyKey);
            return ResponseEntity.ok(existing.get());
        }

        // process and cache it
        BookingRes res = bookingService.createBooking(req);
        idempotencyService.saveResult(idempotencyKey,res);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);

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