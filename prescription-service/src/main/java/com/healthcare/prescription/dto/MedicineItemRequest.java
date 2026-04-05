package com.healthcare.prescription.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MedicineItemRequest(
        @NotNull Long medicineId,
        @NotBlank String drugName,
        @NotBlank String dosage,
        @NotBlank String frequency,
        @NotNull @Positive Integer durationDays,
        @NotBlank String route,
        String instructions
) {
}
