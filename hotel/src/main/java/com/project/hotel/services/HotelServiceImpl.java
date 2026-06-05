package com.project.hotel.services;

import com.project.hotel.dto.HotelReq;
import com.project.hotel.dto.HotelRes;

import java.util.List;

public interface HotelServiceImpl {

    HotelRes createHotel(HotelReq req);
    HotelRes getHotelById(Long hotelId);
    List<HotelRes> getHotelsByLocation(String location);

}
