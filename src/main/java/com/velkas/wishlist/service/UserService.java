package com.velkas.wishlist.service;

import com.velkas.wishlist.model.entity.User;
import com.velkas.wishlist.model.enums.Locale;
import com.velkas.wishlist.model.telegram.TelegramUser;
import com.velkas.wishlist.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getOrCreateUser(TelegramUser telegramUser) {
        Long telegramId = telegramUser.getId();
        LocalDateTime now = LocalDateTime.now();

        Optional<User> existing = userRepository.findByTelegramId(telegramId);

        if (existing.isPresent()) {
            User user = existing.get();
            user.setUsername(telegramUser.getUsername());
            user.setFirstName(telegramUser.getFirstName());
            user.setLastName(telegramUser.getLastName());
            user.setUpdatedAt(now);
            return userRepository.save(user);
        }

        User newUser = User.builder()
                .telegramId(telegramId)
                .username(telegramUser.getUsername())
                .firstName(telegramUser.getFirstName())
                .lastName(telegramUser.getLastName())
                .email(null)
                .locale(resolveLocale(telegramUser.getLanguageCode()))
                .createdAt(now)
                .updatedAt(now)
                .isActive(Boolean.TRUE)
                .build();

        return userRepository.save(newUser);
    }

    private Locale resolveLocale(String languageCode) {
        if (languageCode == null || languageCode.isBlank()) {
            return Locale.RU;
        }

        String normalized = languageCode.trim().toUpperCase(java.util.Locale.ROOT);
        if (normalized.length() > 2) {
            normalized = normalized.substring(0, 2);
        }

        try {
            return Locale.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            return Locale.RU;
        }
    }
}
