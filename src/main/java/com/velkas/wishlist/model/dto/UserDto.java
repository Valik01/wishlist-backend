package com.velkas.wishlist.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;

    private Long telegramId;

    private String username;

    private String firstName;

    private String lastName;

    private String locale;
}
