package com.project.hotel.services.impl;

import com.project.hotel.dto.HotelReq;
import com.project.hotel.dto.HotelRes;
import com.project.hotel.exceptions.custom.ResourceNotFoundException;
import com.project.hotel.models.Hotel;
import com.project.hotel.repo.HotelRepo;
import com.project.hotel.services.HotelServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelService implements HotelServiceImpl {

    private final HotelRepo hotelRepo;
    @Override
    public HotelRes createHotel(HotelReq req) {
        Hotel hotel = Hotel.builder()
                .name(req.name())
                .location(req.location())
                .description(req.description())
                .rating(req.rating())
                .build();

        Hotel res = hotelRepo.save(hotel);
        return mapToHotelRes(res);
    }

    @Override
    public HotelRes getHotelById(Long hotelId) {
        Hotel hotel = hotelRepo.findById(hotelId)
                .orElseThrow(()->new ResourceNotFoundException("Hotel not found with id: "+hotelId));

        return mapToHotelRes(hotel);
    }

    @Override
    public List<HotelRes> getHotelsByLocation(String location) {
       return hotelRepo.findByLocation(location)
               .stream().map(this::mapToHotelRes)
               .toList();
    }


    private HotelRes mapToHotelRes(Hotel h){
        return new HotelRes(
                h.getId(),
                h.getName(),
                h.getLocation(),
                h.getDescription(),
                h.getRating()
        );
    }
}
