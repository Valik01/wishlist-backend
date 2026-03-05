package com.velkas.wishlist.service.telegram;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.velkas.wishlist.config.TelegramAuthProperties;
import com.velkas.wishlist.model.telegram.TelegramUser;
import com.velkas.wishlist.telegram.TelegramAuthConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TelegramAuthService {

    private final TelegramAuthProperties properties;
    private final ObjectMapper objectMapper;

    public TelegramUser authenticate(String authorizationHeader) {
        String initData = extractInitDataFromHeader(authorizationHeader);
        verifyInitData(initData);
        return extractTelegramUser(initData);
    }

    /**
     * Verification of init data according to Telegram WebApp documentation.
     */
    public void verifyInitData(String initData) {
        if (properties.isMock()) {
            return;
        }

        if (!StringUtils.hasText(properties.getBotToken())) {
            throw new IllegalStateException("Telegram bot token is not configured");
        }

        Map<String, String> data = parseInitData(initData);
        String receivedHash = data.get(TelegramAuthConstants.HASH_PARAM);

        if (!StringUtils.hasText(receivedHash)) {
            throw new SecurityException("Missing Telegram init data hash");
        }

        String dataCheckString = data.entrySet().stream()
                .filter(entry -> !TelegramAuthConstants.HASH_PARAM.equals(entry.getKey()))
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("\n"));

        String expectedHash = calculateHash(dataCheckString, properties.getBotToken());

        if (!constantTimeEquals(receivedHash, expectedHash)) {
            throw new SecurityException("Invalid Telegram init data signature");
        }
    }

    /**
     * Extract Telegram user data from init data.
     */
    public TelegramUser extractTelegramUser(String initData) {
        Map<String, String> data = parseInitData(initData);

        String userJson = data.get(TelegramAuthConstants.USER_PARAM);
        if (!StringUtils.hasText(userJson)) {
            if (properties.isMock()) {
                return TelegramUser.builder()
                        .id(1L)
                        .firstName("Mock")
                        .lastName("User")
                        .username("mock_user")
                        .languageCode("ru")
                        .isPremium(Boolean.FALSE)
                        .photoUrl(null)
                        .build();
            }
            throw new IllegalArgumentException("Telegram init data does not contain user information");
        }

        try {
            return objectMapper.readValue(userJson, TelegramUser.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to parse Telegram user data", e);
        }
    }

    private String extractInitDataFromHeader(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader)) {
            if (properties.isMock()) {
                return "";
            }
            throw new SecurityException("Missing Authorization header");
        }

        String value = authorizationHeader.trim();

        // Support possible prefixes like "Bearer " or "TMA "
        for (String prefix : Arrays.asList("Bearer ", "TMA ")) {
            if (value.regionMatches(true, 0, prefix, 0, prefix.length())) {
                return value.substring(prefix.length());
            }
        }

        return value;
    }

    private Map<String, String> parseInitData(String initData) {
        if (!StringUtils.hasText(initData)) {
            return Map.of();
        }

        return Arrays.stream(initData.split("&"))
                .map(pair -> pair.split("=", 2))
                .filter(parts -> parts.length == 2)
                .collect(Collectors.toMap(
                        parts -> urlDecode(parts[0]),
                        parts -> urlDecode(parts[1]),
                        (first, second) -> second
                ));
    }

    private String urlDecode(String value) {
        try {
            return java.net.URLDecoder.decode(value, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException ex) {
            return value;
        }
    }

    private String calculateHash(String dataCheckString, String botToken) {
        byte[] secretKey = hmacSha256(TelegramAuthConstants.WEB_APP_DATA_SECRET.getBytes(StandardCharsets.UTF_8),
                botToken.getBytes(StandardCharsets.UTF_8));
        byte[] hash = hmacSha256(secretKey, dataCheckString.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }

    private byte[] hmacSha256(byte[] key, byte[] data) {
        try {
            Mac mac = Mac.getInstance(TelegramAuthConstants.HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(key, TelegramAuthConstants.HMAC_ALGORITHM));
            return mac.doFinal(data);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("Unable to calculate HMAC-SHA256 hash", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) {
            return false;
        }

        byte[] aBytes = a.getBytes(StandardCharsets.UTF_8);
        byte[] bBytes = b.getBytes(StandardCharsets.UTF_8);

        if (aBytes.length != bBytes.length) {
            return false;
        }

        int result = 0;
        for (int i = 0; i < aBytes.length; i++) {
            result |= aBytes[i] ^ bBytes[i];
        }
        return result == 0;
    }
}
