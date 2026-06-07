package com.project.booking.service.impl;

import com.project.booking.dto.*;
import com.project.booking.exceptions.custom.ConflictException;
import com.project.booking.exceptions.custom.ResourceNotFoundException;
import com.project.booking.kafka.records.BookingCancelledEvent;
import com.project.booking.kafka.records.BookingConfirmedEvent;
import com.project.booking.models.Booking;
import com.project.booking.models.BookingStatus;
import com.project.booking.repo.BookingRepo;
import com.project.booking.service.BookingServiceImpl;
import com.project.booking.sync.HotelClient;
import com.project.booking.sync.UserClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService implements BookingServiceImpl {

    private final BookingRepo bookingRepo;
    private final UserClient userClient;
    private final HotelClient hotelClient;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public BookingRes createBooking(BookingReq req) {
        /*
        * validate user -> check if room available [feign] -> if false return[not available]
        * calc price
        * save
        * publish -> booking.created
        * update room availability
        * confirm booking -> save
        * publish -> booking.confirmed
        * */
        UserRes user = userClient.getUserById(req.userId());
        log.info("User validated: {}", user.email());

        AvailabilityRes availability = hotelClient.checkAvailability(req.roomId());
        if(!availability.available()){
            throw new ConflictException("Room is not available...");
        }
        log.info("Room {} is available", req.roomId());

        long nights = ChronoUnit.DAYS.between(req.checkIn(),req.checkOut());
        if(nights <= 0){
            throw new IllegalArgumentException("Check out must be after check in");
        }

        double price = nights * availability.pricePerNight();

        Booking booking = Booking.builder()
                .userId(req.userId())
                .roomId(req.roomId())
                .hotelId(req.hotelId())
                .status(BookingStatus.PENDING)
                .checkIn(req.checkIn())
                .checkOut(req.checkOut())
                .totalPrice(price)
                .build();

        Booking saved = bookingRepo.save(booking);
        log.info("Booking created with id {}", saved.getId());


        hotelClient.updateAvailability(req.roomId(), false);

        saved.setStatus(BookingStatus.CONFIRMED);
        saved.setUpdatedAt(LocalDateTime.now());
        bookingRepo.save(saved);

        // publish event after db - transaction is done [keeping it simple for now]
    //    bookingProducer.publishBookingCreated(mapToEvent(saved));
       eventPublisher.publishEvent(new BookingConfirmedEvent(saved));

        return mapToRes(saved);
    }

    @Override
    public BookingRes getBookingById(Long id) {
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        return mapToRes(booking);
    }

    @Override
    public List<BookingRes> getBookingByUserId(Long userId) {
        return bookingRepo.findById(userId)
                .stream()
                .map(this::mapToRes)
                .toList();
    }

    @Override
    @Transactional
    public BookingRes cancelBooking(Long id) {
        /*
        * find krunga booking -> if status==cancelled return
        * restore availability
        * status -> CANCELLED -> save
        * publish -> booking.cancelled
        * */
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Booking not found"));

        if(booking.getStatus()== BookingStatus.CANCELLED){
            throw new ConflictException("Booking already cancelled...");
        }

        hotelClient.updateAvailability(booking.getRoomId(),true);
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepo.save(booking);

       eventPublisher.publishEvent(new BookingCancelledEvent(booking));
        return mapToRes(booking);
    }


    private BookingEvent mapToEvent(Booking booking) {
        return new BookingEvent(
                booking.getId(),
                booking.getUserId(),
                booking.getRoomId(),
                booking.getHotelId(),
                booking.getStatus(),
                booking.getCheckIn(),
                booking.getCheckOut(),
                booking.getTotalPrice(),
                LocalDateTime.now()
        );
    }

    private BookingRes mapToRes(Booking booking) {
        return new BookingRes(
                booking.getId(),
                booking.getUserId(),
                booking.getRoomId(),
                booking.getHotelId(),
                booking.getStatus(),
                booking.getCheckIn(),
                booking.getCheckOut(),
                booking.getTotalPrice(),
                booking.getCreatedAt()
        );
    }


}
