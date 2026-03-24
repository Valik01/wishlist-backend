package com.velkas.wishlist.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WishlistRequest {

    private String name;

    private String description;

    private Boolean isPrivate;

}
