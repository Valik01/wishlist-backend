package com.velkas.wishlist.mapper;

import com.velkas.wishlist.model.dto.UserDto;
import com.velkas.wishlist.model.entity.User;
import com.velkas.wishlist.model.enums.Locale;
import com.velkas.wishlist.model.telegram.TelegramUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "username", source = "username")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "telegramId", source = "id")
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "locale", expression = "java(resolveLocale(telegramUser.getLanguageCode()))")
    User toEntity(TelegramUser telegramUser);

    @Mapping(target = "username", source = "telegramUser.username")
    @Mapping(target = "firstName", source = "telegramUser.firstName")
    @Mapping(target = "lastName", source = "telegramUser.lastName")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "telegramId", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "locale", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    void updateUser(@MappingTarget User user, TelegramUser telegramUser);

    default Locale resolveLocale(String languageCode) {
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
