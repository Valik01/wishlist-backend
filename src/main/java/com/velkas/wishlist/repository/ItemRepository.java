package com.velkas.wishlist.repository;

import com.velkas.wishlist.model.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Integer> {
}
