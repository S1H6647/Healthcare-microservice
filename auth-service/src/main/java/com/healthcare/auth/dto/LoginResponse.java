package com.healthcare.auth.dto;

import com.healthcare.auth.entity.User;
import lombok.Builder;

@Builder
public record LoginResponse(
        String token,
        String email,
        String role
) {
    public static LoginResponse from(User user, String token) {
        return new LoginResponse(
                token,
                user.getEmail(),
                user.getRole().toString()
        );
    }
}