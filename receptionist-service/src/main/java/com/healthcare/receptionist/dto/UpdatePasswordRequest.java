package com.healthcare.receptionist.dto;

public record UpdatePasswordRequest(
        String oldPassword,
        String newPassword
) {
}
