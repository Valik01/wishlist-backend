package com.velkas.wishlist.repository;

import com.velkas.wishlist.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
