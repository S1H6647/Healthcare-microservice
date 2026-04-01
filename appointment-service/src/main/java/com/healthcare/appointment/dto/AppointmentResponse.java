package com.healthcare.appointment.dto;

import com.healthcare.appointment.entity.Appointment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record AppointmentResponse(
        Long id,
        Long patientId,
        Long doctorId,
        String patientName,
        String patientEmail,
        String doctorName,
        String specialization,
        LocalDate appointmentDate,
        LocalTime startTime,
        LocalTime endTime,
        String status,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static AppointmentResponse from(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getPatientId(),
                appointment.getDoctorId(),
                appointment.getPatientName(),
                appointment.getPatientEmail(),
                appointment.getDoctorName(),
                appointment.getSpecialization(),
                appointment.getAppointmentDate(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.getStatus().name(),
                appointment.getNotes(),
                appointment.getCreatedAt(),
                appointment.getUpdatedAt()
        );
    }
}
