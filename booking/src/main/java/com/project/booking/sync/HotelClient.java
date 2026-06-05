package com.project.booking.sync;

import com.project.booking.dto.AvailabilityRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "hotel-service" , path = "/hotels")
public interface HotelClient {

    @GetMapping("/rooms/{roomId}/availability")
    AvailabilityRes checkAvailability(Long roomId);

    @PutMapping("/rooms/{roomId}/availability")
    void updateAvailability(Long roomId, boolean available);
}
