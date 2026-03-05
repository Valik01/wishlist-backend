Wishlist backend project

## Authorization flow (Telegram WebApp)

### 1. Overview

Backend does **not** use classic login/password or JWT.  
Authentication is fully delegated to **Telegram WebApp init data** verification.

Key points:
- **Client** (Telegram WebApp) receives `initData` from Telegram.
- **Client** sends this `initData` in the `Authorization` header to backend.
- **Backend** verifies signature according to Telegram docs and extracts Telegram user.
- **User** is created/updated in DB and returned as DTO.

### 2. Required configuration

In `application.yaml` / `application-*.yaml` you configure Telegram auth:

- `telegram.auth.bot-token` – Telegram bot token, used to verify signature.
- `telegram.auth.mock` – when `true`, verification is skipped and a mock user is returned (useful in local/dev).

Example:

```yaml
telegram:
  auth:
    bot-token: ${TELEGRAM_BOT_TOKEN:}
    mock: false
```

For development the `application-dev.yaml` may contain:

```yaml
telegram:
  auth:
    bot-token: dummy-dev-token
    mock: true
```

### 3. Client request format

All authenticated requests must include HTTP header:

- `Authorization: <initData>`
  - optional prefixes are supported: `Bearer <initData>` или `TMA <initData>`.

Where `initData` is the query-string Telegram passes to WebApp, for example:

```text
query_id=AAE...&user=%7B%22id%22%3A123456...%7D&auth_date=1710000000&hash=ab12cd...
```

The backend:
- strips optional prefix (`Bearer ` / `TMA `);
- parses key-value pairs;
- takes `hash` and compares it with its own HMAC-SHA256 calculation.

If `telegram.auth.mock=true` and header is empty, a predefined mock `TelegramUser` is used.

### 4. How verification works (backend)

Class `TelegramAuthService` (`com.velkas.wishlist.service.telegram.TelegramAuthService`):

- **`authenticate(String authorizationHeader)`**
  - extracts `initData` from header;
  - calls `verifyInitData(initData)` (skipped when `mock=true`);
  - calls `extractTelegramUser(initData)` and returns `TelegramUser`.

- **`verifyInitData(String initData)`**
  - parses `initData` into map;
  - builds `data_check_string` from all params except `hash` (sorted, `key=value`, joined by `\n`);
  - calculates expected hash using Telegram algorithm:
    - HMAC-SHA256 with key `HMAC-SHA256("WebAppData", botToken)`;
    - compares received and calculated hash in constant time.
  - throws `SecurityException` when signature is invalid or hash is missing.

- **`extractTelegramUser(String initData)`**
  - reads `user` param (JSON);
  - deserializes it into `TelegramUser` (fields: `id`, `first_name`, `last_name`, `username`, `language_code`, `is_premium`, `photo_url`);
  - when `mock=true` and `user` is missing, returns static mock user.

### 5. Profile endpoint and user creation

Controller `ProfileController` (`/api/profile`):

- **Method**: `GET /api/profile`
- **Headers**:
  - `Authorization: <initData>` (optionally with `Bearer ` / `TMA ` prefix)
- **Response**:
  - `200 OK` with `UserDto` of authenticated user;
  - errors:
    - `401/403` (depending on global error handling) when signature/header is invalid.

Flow inside:
1. `TelegramAuthService.authenticate` returns `TelegramUser`.
2. `UserService.getOrCreateUser(telegramUser)`:
   - finds user by `telegramId`;
   - if exists – updates username/first/last name, `updatedAt`;
   - if not – creates new user with:
     - `telegramId`, `username`, `firstName`, `lastName`;
     - `locale` resolved from Telegram `language_code` (`RU` by default);
     - `createdAt` / `updatedAt` = now;
     - `isActive = true`.
3. `UserMapper.toDto` converts entity to `UserDto` and returns to client.

### 6. How to call from client (example)

Pseudocode for front-end in WebApp:

```ts
// In Telegram WebApp context
const initData = window.Telegram.WebApp.initData;

await fetch('/api/profile', {
  method: 'GET',
  headers: {
    // any of the following is accepted by backend:
    // 'Authorization': initData,
    // 'Authorization': 'Bearer ' + initData,
    'Authorization': 'TMA ' + initData,
  },
});
```

As long as `initData` is passed unchanged from Telegram and `telegram.auth.bot-token` matches your bot, backend will authenticate the user and return their profile.
