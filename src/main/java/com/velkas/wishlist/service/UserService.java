package com.velkas.wishlist.service;

import com.velkas.wishlist.mapper.UserMapper;
import com.velkas.wishlist.model.dto.UserDto;
import com.velkas.wishlist.model.entity.User;
import com.velkas.wishlist.model.telegram.TelegramUser;
import com.velkas.wishlist.repository.UserRepository;
import com.velkas.wishlist.service.telegram.TelegramAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final TelegramAuthService telegramAuthService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public UserDto getOrCreateUser(String authorization) {
        TelegramUser telegramUser = telegramAuthService.authenticate(authorization);

        User user = userRepository.findByTelegramId(telegramUser.getId())
            .map(tgUser -> {
                userMapper.updateUser(tgUser, telegramUser);
                return userRepository.save(tgUser);
            })
            .orElseGet(() -> userRepository.save(userMapper.toEntity(telegramUser)));

        return userMapper.toDto(user);
    }
}
