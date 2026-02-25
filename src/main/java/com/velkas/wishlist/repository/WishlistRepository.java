package com.velkas.wishlist.repository;

import com.velkas.wishlist.model.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
}
