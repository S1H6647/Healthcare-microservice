package com.healthcare.appointment.messaging;

import com.healthcare.appointment.entity.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentEvent(
        Long appointmentId,
        String patientName,
        String patientEmail,
        String doctorName,
        String specialization,
        LocalDate appointmentDate,
        LocalTime startTime,
        LocalTime endTime,
        String status
) {
    public static AppointmentEvent from(Appointment appointment) {
        return new AppointmentEvent(
                appointment.getId(),
                appointment.getPatientName(),
                appointment.getPatientEmail(),
                appointment.getDoctorName(),
                appointment.getSpecialization(),
                appointment.getAppointmentDate(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.getStatus().name()
        );
    }
}
