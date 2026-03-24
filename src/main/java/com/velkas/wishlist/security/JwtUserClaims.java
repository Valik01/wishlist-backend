package com.velkas.wishlist.security;

public record JwtUserClaims(Long userId, String username) {
}
