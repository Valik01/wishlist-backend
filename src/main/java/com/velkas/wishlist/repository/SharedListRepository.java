package com.velkas.wishlist.repository;

import com.velkas.wishlist.model.entity.SharedList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SharedListRepository extends JpaRepository<SharedList, Integer> {
}
