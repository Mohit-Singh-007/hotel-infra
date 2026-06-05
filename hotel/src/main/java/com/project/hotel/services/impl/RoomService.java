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
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


/*So basically 2 ways to cache -> manual [key -> redis set etc...] -> redisTemplate with TTL
*
* using annotations -> @Cacheable , @CacheEvict etc... -> Bean picks everything [CacheManageer]
* */

@Service
@RequiredArgsConstructor
@Slf4j
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
        return mapToRoomRes(res);

    }

    // booking-service will call it later [Feign-client]
    @Override
    @Cacheable(value = "availability",key="#roomId") // auto checks redis - else DB call on miss
    public AvailabilityRes checkAvailability(Long roomId) {
        log.info("CACHE MISS - hitting DB for room {}",roomId);
        Rooms room = roomRepo.findById(roomId)
                .orElseThrow(()-> new RuntimeException("Room not found..."));

        return new AvailabilityRes(
                room.getId(),
                room.isAvailable(),
                room.getPricePerNight()
        );
        // check redis -> If it misses -> DB and store in redis
    }


    // kafka event streaming
    // called by Kafka consumer when booking confirmed/cancelled
    @CacheEvict(value = "availability", key = "#roomId")
    @Override
    public void updateAvailability(Long roomId, boolean available) {

        Rooms room = roomRepo.findById(roomId)
                .orElseThrow(()-> new RuntimeException("Room not found..."));

        room.setAvailable(available);
        roomRepo.save(room);
        log.info("Room {} availability updated to {}", roomId, available);
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
