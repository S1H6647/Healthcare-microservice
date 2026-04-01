package com.healthcare.appointment.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PatientResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        LocalDate dateOfBirth,
        String gender,
        String address,
        LocalDateTime createdAt
) {}
