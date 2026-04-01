package com.healthcare.appointment.service;

import com.healthcare.appointment.dto.DoctorRequest;
import com.healthcare.appointment.dto.DoctorResponse;
import com.healthcare.appointment.entity.Doctor;
import com.healthcare.appointment.exception.DuplicateResourceException;
import com.healthcare.appointment.exception.ResourceNotFoundException;
import com.healthcare.appointment.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;

    @Transactional
    public DoctorResponse registerDoctor(DoctorRequest request, String qualificationUrl) {
        if (doctorRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException(
                    "Doctor with email " + request.email() + " already exists"
            );
        }
        if (doctorRepository.existsByLicenseNumber(request.licenseNumber())) {
            throw new DuplicateResourceException(
                    "License number " + request.licenseNumber() + " is already registered"
            );
        }

        Doctor doctor = Doctor.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .phone(request.phone())
                .specialization(request.specialization())
                .licenseNumber(request.licenseNumber())
                .availableDays(request.availableDays())
                .consultationFee(request.consultationFee())
                .qualification(request.qualification())
                .qualificationUrl(qualificationUrl)
                .build();

        return DoctorResponse.from(doctorRepository.save(doctor));
    }

    @Transactional(readOnly = true)
    public DoctorResponse getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .map(DoctorResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor not found with id: " + id
                ));
    }

    @Transactional(readOnly = true)
    public List<DoctorResponse> getAllDoctors() {
        return doctorRepository.findAll()
                .stream()
                .map(DoctorResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DoctorResponse> getDoctorsBySpecialization(String specialization) {
        return doctorRepository.findBySpecialization(specialization)
                .stream()
                .map(DoctorResponse::from)
                .toList();
    }

    @Transactional
    public DoctorResponse updateDoctor(Long id, DoctorRequest request, String qualificationUrl) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor not found with id: " + id
                ));

        if (!doctor.getEmail().equals(request.email())
                && doctorRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException(
                    "Email " + request.email() + " is already in use"
            );
        }

        if (!doctor.getLicenseNumber().equals(request.licenseNumber())
                && doctorRepository.existsByLicenseNumber(request.licenseNumber())) {
            throw new DuplicateResourceException(
                    "License number " + request.licenseNumber() + " is already registered"
            );
        }

        doctor.setFirstName(request.firstName());
        doctor.setLastName(request.lastName());
        doctor.setEmail(request.email());
        doctor.setPhone(request.phone());
        doctor.setSpecialization(request.specialization());
        doctor.setLicenseNumber(request.licenseNumber());
        doctor.setAvailableDays(request.availableDays());
        doctor.setConsultationFee(request.consultationFee());
        doctor.setQualification(request.qualification());
        if (qualificationUrl != null) {
            doctor.setQualificationUrl(qualificationUrl);
        }

        return DoctorResponse.from(doctorRepository.save(doctor));
    }

    @Transactional
    public void deleteDoctor(Long id) {
        if (!doctorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Doctor not found with id: " + id);
        }
        doctorRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public DoctorResponse getDoctorByEmail(String email) {
        Doctor doctor = doctorRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with email: " + email));

        return DoctorResponse.from(doctor);
    }
}