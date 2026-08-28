package com.revbook.authservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginResponse(
        @JsonProperty("access_token") String accessToken, String refreshToken, UserResponse user) {
}
