package com.healthcare.prescription.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DoctorResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        String specialization,
        String licenseNumber,
        String availableDays,
        BigDecimal consultationFee,
        LocalDateTime createdAt
) {}
