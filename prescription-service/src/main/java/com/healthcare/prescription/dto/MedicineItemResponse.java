package com.healthcare.prescription.dto;

import com.healthcare.prescription.entity.MedicineSummary;

public record MedicineItemResponse(
        Long id,
        String drugName,
        String dosage,
        String frequency,
        Integer durationDays,
        String route,
        String instructions
) {
    public static MedicineItemResponse from(MedicineSummary medicine) {
        return new MedicineItemResponse(
                medicine.getId(),
                medicine.getDrugName(),
                medicine.getDosage(),
                medicine.getFrequency(),
                medicine.getDurationDays(),
                medicine.getRoute(),
                medicine.getInstructions()
        );
    }
}
