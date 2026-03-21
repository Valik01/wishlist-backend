package com.velkas.wishlist.controller;

import com.velkas.wishlist.model.dto.UserDto;
import com.velkas.wishlist.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserDto> getProfile(@RequestHeader("Authorization") String authorization) {
        return ResponseEntity.ok(userService.getOrCreateUser(authorization));
    }

}
