package com.velkas.wishlist.controller;

import com.velkas.wishlist.mapper.UserMapper;
import com.velkas.wishlist.model.dto.UserDto;
import com.velkas.wishlist.model.entity.User;
import com.velkas.wishlist.model.telegram.TelegramUser;
import com.velkas.wishlist.service.telegram.TelegramAuthService;
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

    private final TelegramAuthService telegramAuthService;
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    public ResponseEntity<UserDto> getProfile(@RequestHeader("Authorization") String authorization) {
        TelegramUser telegramUser = telegramAuthService.authenticate(authorization);
        User user = userService.getOrCreateUser(telegramUser);
        UserDto dto = userMapper.toDto(user);
        return ResponseEntity.ok(dto);
    }
}
