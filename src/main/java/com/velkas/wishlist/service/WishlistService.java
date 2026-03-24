package com.velkas.wishlist.service;

import com.velkas.wishlist.mapper.WishlistMapper;
import com.velkas.wishlist.model.dto.request.WishlistRequest;
import com.velkas.wishlist.model.dto.response.WishlistResponse;
import com.velkas.wishlist.model.entity.User;
import com.velkas.wishlist.model.entity.Wishlist;
import com.velkas.wishlist.repository.UserRepository;
import com.velkas.wishlist.repository.WishlistRepository;
import com.velkas.wishlist.security.JwtUserClaims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final WishlistMapper wishlistMapper;

    @Transactional
    public WishlistResponse saveWishlist(WishlistRequest request) {
        Long userId = getCurrentUserId();

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("User with id %s not found", userId)));

        Wishlist wishlist = wishlistRepository.save(wishlistMapper.toEntity(request, user));

        return wishlistMapper.toResponse(wishlist);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserClaims claims)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        return claims.userId();
    }

}
