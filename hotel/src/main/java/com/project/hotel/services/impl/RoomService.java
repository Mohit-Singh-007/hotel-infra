package com.project.hotel.services.impl;

import com.project.hotel.dto.AvailabilityRes;
import com.project.hotel.dto.RoomReq;
import com.project.hotel.dto.RoomRes;
import com.project.hotel.models.Hotel;
import com.project.hotel.models.Rooms;
import com.project.hotel.repo.HotelRepo;
import com.project.hotel.repo.RoomRepo;
import com.project.hotel.services.RoomServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService implements RoomServiceImpl {
    private final HotelRepo hotelRepo;
    private final RoomRepo roomRepo;

    @Override
    public RoomRes createRoom(RoomReq req) {

        Hotel hotel = hotelRepo.findById(req.hotelId())
                .orElseThrow(()->new RuntimeException("Hotel not found with id: "+req.hotelId()));

        Rooms room = Rooms.builder()
                .hotel(hotel)
                .roomNumber(req.roomNumber())
                .type(req.type())
                .pricePerNight(req.pricePerNight())
                .maxOccupancy(req.maxOccupancy())
                .available(true)
                .build();

        Rooms res = roomRepo.save(room);

        // store availability in cache for fast-lookup [later]
        return mapToRoomRes(res);

    }

    // booking-service will call it later [Feign-client]
    @Override
    public AvailabilityRes checkAvailability(Long roomId) {
        return null;
        // check redis -> if miss -> DB and store in redis
    }

    @Override
    public void updateAvailability(Long roomId, boolean available) {

    }


    private RoomRes mapToRoomRes(Rooms room) {
        return new RoomRes(
                room.getId(),
                room.getHotel().getId(),
                room.getRoomNumber(),
                room.getType(),
                room.getPricePerNight(),
                room.isAvailable(),
                room.getMaxOccupancy()
        );
    }

}
