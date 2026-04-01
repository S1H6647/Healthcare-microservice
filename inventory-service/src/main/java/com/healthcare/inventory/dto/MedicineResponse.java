package com.healthcare.inventory.dto;

import com.healthcare.inventory.entity.DosageType;
import com.healthcare.inventory.entity.Medicine;
import com.healthcare.inventory.entity.MedicineType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MedicineResponse(
        Long id,
        String medicineName,
        String shortDescription,
        Integer quantity,
        MedicineType medicineType,
        String dosage,
        DosageType dosageType,
        String imageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static MedicineResponse from(Medicine medicine) {
        return MedicineResponse.builder()
                .id(medicine.getId())
                .medicineName(medicine.getMedicineName())
                .shortDescription(medicine.getShortDescription())
                .quantity(medicine.getQuantity())
                .medicineType(medicine.getMedicineType())
                .dosage(medicine.getDosage())
                .dosageType(medicine.getDosageType())
                .imageUrl(medicine.getImageUrl())
                .createdAt(medicine.getCreatedAt())
                .updatedAt(medicine.getUpdatedAt())
                .build();
    }
}
