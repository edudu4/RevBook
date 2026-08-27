package com.revbook.authservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/** O front-end (Login.tsx) lê especificamente a chave "access_token". */
public record LoginResponse(@JsonProperty("access_token") String accessToken, UserResponse user) {
}
