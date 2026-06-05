package com.project.booking.service;

import com.project.booking.dto.BookingReq;
import com.project.booking.dto.BookingRes;

import java.util.List;

public interface BookingServiceImpl {
    BookingRes createBooking(BookingReq req);
    BookingRes getBookingById(Long bookingId);
    List<BookingRes> getBookingByUserId(Long userId);
    BookingRes cancelBooking(Long userId);
}
