package com.healthcare.appointment.dto;

import jakarta.validation.constraints.*;

public record DoctorRequest(
        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^(98|97)\\d{8}$", message = "Phone number must start with 98 or 97 and be 10 digits long")
        String phone,

        @NotBlank(message = "Specialization is required")
        String specialization,

        @NotBlank(message = "License number is required")
        String licenseNumber,

        @NotBlank(message = "Available days are required")
        String availableDays,

        @NotNull(message = "Consultation fee is required")
        @Positive(message = "Consultation fee must be positive")
        java.math.BigDecimal consultationFee,

        @NotBlank(message = "Qualification is required")
        String qualification
) {
}

