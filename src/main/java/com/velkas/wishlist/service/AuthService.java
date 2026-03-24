package com.velkas.wishlist.service;

import com.velkas.wishlist.model.dto.response.AuthResponse;
import com.velkas.wishlist.model.dto.UserDto;
import com.velkas.wishlist.model.telegram.TelegramUser;
import com.velkas.wishlist.security.JwtService;
import com.velkas.wishlist.service.telegram.TelegramAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TelegramAuthService telegramAuthService;
    private final UserService userService;
    private final JwtService jwtService;

    public AuthResponse authenticate(String authorization) {
        TelegramUser telegramUser = telegramAuthService.authenticate(authorization);

        UserDto user = userService.getOrCreateUser(telegramUser);

        String token = jwtService.generateToken(user.getId(), user.getUsername());

        return AuthResponse.builder()
            .user(user)
            .token(token)
            .build();
    }

}
