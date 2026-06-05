package com.project.hotel.services;

import com.project.hotel.dto.AvailabilityRes;
import com.project.hotel.dto.RoomReq;
import com.project.hotel.dto.RoomRes;

public interface RoomServiceImpl {
    RoomRes createRoom(RoomReq req);
    AvailabilityRes checkAvailability(Long roomId);
    void updateAvailability(Long roomId,boolean available);

}
