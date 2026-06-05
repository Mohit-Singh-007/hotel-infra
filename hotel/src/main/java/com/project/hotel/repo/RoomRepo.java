package com.project.hotel.repo;

import com.project.hotel.models.RoomType;
import com.project.hotel.models.Rooms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepo extends JpaRepository<Rooms,Long> {
    List<Rooms> findByHotelId(Long hotelId);
    List<Rooms> findByHotelIdAndAvailable(Long hotelId,boolean isAvailable);
    List<Rooms> findByHotelIdAndTypeAndAvailable(Long hotelId, RoomType type, boolean available);
}
