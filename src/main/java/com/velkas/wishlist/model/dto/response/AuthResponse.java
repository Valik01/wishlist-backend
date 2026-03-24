package com.velkas.wishlist.model.dto.response;

import com.velkas.wishlist.model.dto.UserDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {

    private UserDto user;

    private String token;

}
