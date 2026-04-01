package com.healthcare.prescription.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record PrescriptionRequest(
        @NotNull Long patientId,
        @NotBlank String patientName,
        @NotNull Long doctorId,
        @NotBlank String doctorName,
        @NotBlank String instruction,
        @NotNull LocalDate visitDate,
        @NotBlank String symptoms,
        @NotBlank String diagnosis,
        String note,
        @NotEmpty List<MedicineItemRequest> items
) {}
