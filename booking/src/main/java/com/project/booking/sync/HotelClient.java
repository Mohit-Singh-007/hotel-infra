package com.project.booking.sync;

import com.project.booking.dto.AvailabilityRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "HOTEL" , path = "/hotels")
public interface HotelClient {

    @GetMapping("/rooms/{roomId}/availability")
    AvailabilityRes checkAvailability(@PathVariable Long roomId);

    @PutMapping("/rooms/{roomId}/availability")
    void updateAvailability(@PathVariable Long roomId,@RequestParam boolean available);
}
