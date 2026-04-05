package com.healthcare.patient.dto;

import com.healthcare.patient.entity.Patient;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record PatientResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        LocalDate dateOfBirth,
        String gender,
        String address,
        boolean isProfileCompleted,
        LocalDateTime createdAt
) {
    public static PatientResponse from(Patient patient) {
        return PatientResponse.builder()
                .id(patient.getId())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .email(patient.getEmail())
                .phone(patient.getPhone())
                .dateOfBirth(patient.getDateOfBirth())
                .gender(patient.getGender())
                .address(patient.getAddress())
                .isProfileCompleted(patient.isProfileCompleted())
                .createdAt(patient.getCreatedAt())
                .build();
    }
}
