package com.stoloto.api.repository;

import com.stoloto.api.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByHasBoost(Boolean hasBoost);

    List<Room> findByStartTimeAfter(LocalDateTime startTime);

    List<Room> findByStartTimeBefore(LocalDateTime startTime);

    List<Room> findByEntryPriceLessThanEqual(Integer maxPrice);

    List<Room> findByPrizeFundGreaterThanEqual(Integer minPrize);
}