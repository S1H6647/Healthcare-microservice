package com.healthcare.appointment.service;

import com.healthcare.appointment.dto.AppointmentRequest;
import com.healthcare.appointment.dto.AppointmentResponse;
import com.healthcare.appointment.dto.DoctorResponse;
import com.healthcare.appointment.dto.PatientResponse;
import com.healthcare.appointment.entity.Appointment;
import com.healthcare.appointment.entity.AppointmentStatus;
import com.healthcare.appointment.exception.AppointmentConflictException;
import com.healthcare.appointment.exception.ResourceNotFoundException;
import com.healthcare.appointment.feign.DoctorClient;
import com.healthcare.appointment.feign.PatientClient;
import com.healthcare.appointment.messaging.AppointmentEvent;
import com.healthcare.appointment.messaging.AppointmentEventPublisher;
import com.healthcare.appointment.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorClient doctorClient;
    private final PatientClient patientClient;
    private final AppointmentEventPublisher publisher;

    @Transactional
    public AppointmentResponse bookAppointment(AppointmentRequest request) {

        PatientResponse patient = patientClient.getPatientById(request.patientId());
        if (patient == null) {
            throw new ResourceNotFoundException(
                    "Patient not found or patient-service is unavailable. ID: " + request.patientId()
            );
        }

        DoctorResponse doctor = doctorClient.getDoctorById(request.doctorId());
        if (doctor == null) {
            throw new ResourceNotFoundException(
                    "Doctor not found or doctor-service is unavailable. ID: " + request.doctorId()
            );
        }

        boolean conflict = appointmentRepository.existsConflict(
                request.doctorId(),
                request.appointmentDate(),
                request.startTime(),
                request.endTime(),
                List.of(AppointmentStatus.CANCELLED)
        );
        if (conflict) {
            throw new AppointmentConflictException(
                    "Doctor is not available at the requested time slot"
            );
        }

        Appointment saved = Appointment.builder()
                .patientId(request.patientId())
                .doctorId(request.doctorId())
                .patientName(constructFullname(patient.firstName(), patient.lastName()))
                .patientEmail(patient.email())
                .doctorName(constructFullname(doctor.firstName(), doctor.lastName()))
                .specialization(doctor.specialization())
                .appointmentDate(request.appointmentDate())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .status(AppointmentStatus.PENDING)
                .notes(request.notes())
                .build();

        appointmentRepository.save(saved);
        publisher.publishAppointmentEvent(AppointmentEvent.from(saved));

        return AppointmentResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public AppointmentResponse getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .map(AppointmentResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found with id: " + id
                ));
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId)
                .stream()
                .map(AppointmentResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId)
                .stream()
                .map(AppointmentResponse::from)
                .toList();
    }

    @Transactional
    public AppointmentResponse updateStatus(Long id, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found with id: " + id
                ));

        appointment.setStatus(status);
        publisher.publishAppointmentEvent(AppointmentEvent.from(appointment));
        return AppointmentResponse.from(appointmentRepository.save(appointment));
    }

    @Transactional
    public void cancelAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found with id: " + id
                ));

        appointment.setStatus(AppointmentStatus.CANCELLED);
        publisher.publishAppointmentEvent(AppointmentEvent.from(appointment));
        appointmentRepository.save(appointment);
    }

    // Helper methods
    private String constructFullname(String firstName, String lastName) {
        return firstName + " " + lastName;
    }
}
