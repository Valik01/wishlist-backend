package com.velkas.wishlist.mapper;

import com.velkas.wishlist.model.dto.UserDto;
import com.velkas.wishlist.model.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);
}
