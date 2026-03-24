package com.velkas.wishlist.mapper;

import com.velkas.wishlist.model.dto.request.WishlistRequest;
import com.velkas.wishlist.model.dto.response.WishlistResponse;
import com.velkas.wishlist.model.entity.User;
import com.velkas.wishlist.model.entity.Wishlist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WishlistMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    Wishlist toEntity(WishlistRequest request, User user);

    WishlistResponse toResponse(Wishlist wishlist);

}
