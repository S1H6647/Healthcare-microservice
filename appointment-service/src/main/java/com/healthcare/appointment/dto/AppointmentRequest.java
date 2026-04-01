package com.healthcare.appointment.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentRequest(
        @NotNull(message = "Patient ID is required")
        Long patientId,

        @NotNull(message = "Doctor ID is required")
        Long doctorId,

        @NotNull(message = "Appointment date is required")
        @FutureOrPresent(message = "Appointment date must be in the present or future")
        LocalDate appointmentDate,

        @NotNull(message = "Start time is required")
        LocalTime startTime,

        @NotNull(message = "End time is required")
        LocalTime endTime,

        String notes
) {
}
