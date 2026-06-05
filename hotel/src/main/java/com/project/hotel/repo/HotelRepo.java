package com.project.hotel.repo;

import com.project.hotel.models.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepo extends JpaRepository<Hotel,Long> {
    List<Hotel> findByLocation(String location);
}
