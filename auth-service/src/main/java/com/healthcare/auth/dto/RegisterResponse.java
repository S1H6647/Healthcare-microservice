package com.healthcare.auth.dto;

import com.healthcare.auth.entity.User;
import lombok.Builder;

@Builder
public record RegisterResponse(
        String email,
        String name,
        String role
) {
    public static RegisterResponse from(User user) {
        return new RegisterResponse(
                user.getEmail(),
                user.getName(),
                user.getRole().toString()
        );
    }
}