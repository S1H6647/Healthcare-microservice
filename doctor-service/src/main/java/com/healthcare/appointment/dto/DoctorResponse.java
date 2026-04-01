package com.healthcare.appointment.dto;

import com.healthcare.appointment.entity.Doctor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
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
        String qualification,
        String qualificationUrl,
        LocalDateTime createdAt
) {
    public static DoctorResponse from(Doctor doctor) {
        return DoctorResponse.builder()
                .id(doctor.getId())
                .firstName(doctor.getFirstName())
                .lastName(doctor.getLastName())
                .email(doctor.getEmail())
                .phone(doctor.getPhone())
                .specialization(doctor.getSpecialization())
                .licenseNumber(doctor.getLicenseNumber())
                .availableDays(doctor.getAvailableDays())
                .consultationFee(doctor.getConsultationFee())
                .qualification(doctor.getQualification())
                .qualificationUrl(doctor.getQualificationUrl())
                .createdAt(doctor.getCreatedAt())
                .build();
    }
}
