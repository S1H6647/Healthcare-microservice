package com.healthcare.patient.dto;

import lombok.Builder;

@Builder
public record AuthRegisterRequest(
        String firstName,
        String lastName,
        String email,
        String phone,
        String password
) {
}
