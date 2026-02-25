package com.velkas.wishlist.repository;

import com.velkas.wishlist.model.entity.ReservationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationHistoryRepository extends JpaRepository<ReservationHistory, Integer> {
}
