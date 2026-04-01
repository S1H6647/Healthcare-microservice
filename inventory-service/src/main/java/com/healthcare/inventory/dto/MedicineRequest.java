package com.healthcare.inventory.dto;

import com.healthcare.inventory.entity.DosageType;
import com.healthcare.inventory.entity.MedicineType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MedicineRequest(
        @NotBlank(message = "Medicine name is required")
        String medicineName,

        String shortDescription,

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        Integer quantity,

        @NotNull(message = "Medicine type is required")
        MedicineType medicineType,

        String dosage,

        @NotNull(message = "Dosage type is required")
        DosageType dosageType
) {
}
