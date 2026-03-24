package com.velkas.wishlist.controller;

import com.velkas.wishlist.model.dto.response.AuthResponse;
import com.velkas.wishlist.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping
    public ResponseEntity<AuthResponse> getProfile(@RequestHeader("Authorization") String authorization) {
        return ResponseEntity.ok(authService.authenticate(authorization));
    }

}
