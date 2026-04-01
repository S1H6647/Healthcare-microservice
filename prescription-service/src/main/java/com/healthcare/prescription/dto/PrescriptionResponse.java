package com.healthcare.prescription.dto;

import com.healthcare.prescription.entity.Prescription;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record PrescriptionResponse(
        Long id,
        Long patientId,
        String patientName,
        Long doctorId,
        String doctorName,
        String instruction,
        LocalDate visitDate,
        String symptoms,
        String diagnosis,
        String note,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<MedicineItemResponse> items
) {
    public static PrescriptionResponse from(Prescription prescription) {
        List<MedicineItemResponse> items = prescription.getMedicines().stream()
                .map(MedicineItemResponse::from)
                .toList();

        return new PrescriptionResponse(
                prescription.getId(),
                prescription.getPatientId(),
                prescription.getPatientName(),
                prescription.getDoctorId(),
                prescription.getDoctorName(),
                prescription.getInstruction(),
                prescription.getVisitDate(),
                prescription.getSymptoms(),
                prescription.getDiagnosis(),
                prescription.getNote(),
                prescription.getCreatedAt(),
                prescription.getUpdatedAt(),
                items
        );
    }
}
