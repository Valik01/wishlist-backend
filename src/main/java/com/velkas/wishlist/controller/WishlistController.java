package com.velkas.wishlist.controller;

import com.velkas.wishlist.model.dto.request.WishlistRequest;
import com.velkas.wishlist.model.dto.response.WishlistResponse;
import com.velkas.wishlist.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping
    public ResponseEntity<WishlistResponse> saveWishlist(@RequestBody WishlistRequest wishlistRequest) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(wishlistService.saveWishlist(wishlistRequest));
    }

}
