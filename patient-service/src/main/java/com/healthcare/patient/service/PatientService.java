package com.healthcare.patient.service;

import com.healthcare.patient.dto.AuthRegisterRequest;
import com.healthcare.patient.dto.PatientRequest;
import com.healthcare.patient.dto.PatientResponse;
import com.healthcare.patient.entity.Patient;
import com.healthcare.patient.exception.DuplicateEmailException;
import com.healthcare.patient.exception.ResourceNotFoundException;
import com.healthcare.patient.feign.AuthClient;
import com.healthcare.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final AuthClient authClient;

    @Transactional
    public PatientResponse registerPatient(PatientRequest request) {
        if (request.email() != null && patientRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException(
                    "Patient with email " + request.email() + " already exists"
            );
        }

        boolean isComplete = isProfileComplete(request);

        Patient patient = Patient.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .phone(request.phone())
                .dateOfBirth(request.dateOfBirth())
                .gender(request.gender())
                .address(request.address())
                .isProfileCompleted(isComplete)
                .build();

        Patient saved = patientRepository.save(patient);

        // Sync with Auth Service
        authClient.register(AuthRegisterRequest.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email() != null ? request.email() : request.phone() + "@healthcare.com")
                .phone(request.phone())
                .password(request.phone()) // Use phone as default password
                .build());

        return PatientResponse.from(saved);
    }

    private boolean isProfileComplete(PatientRequest request) {
        return request.email() != null && !request.email().isBlank()
                && request.phone() != null && !request.phone().isBlank()
                && request.dateOfBirth() != null
                && request.gender() != null && !request.gender().isBlank();
    }

    @Transactional(readOnly = true)
    public PatientResponse getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
        return PatientResponse.from(patient);
    }

    @Transactional
    public List<PatientResponse> getAllPatients() {
        List<Patient> patientList = patientRepository.findAll();
        return patientList.stream()
                .map(PatientResponse::from)
                .toList();
    }

    @Transactional
    public PatientResponse updatePatient(Long id, PatientRequest request) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));

        if (request.email() != null && !request.email().equals(patient.getEmail())
                && patientRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException(
                    "Email " + request.email() + " is already in use"
            );
        }

        patient.setFirstName(request.firstName());
        patient.setLastName(request.lastName());
        patient.setEmail(request.email());
        patient.setPhone(request.phone());
        patient.setDateOfBirth(request.dateOfBirth());
        patient.setGender(request.gender());
        patient.setAddress(request.address());
        patient.setProfileCompleted(isProfileComplete(request));

        return PatientResponse.from(patientRepository.save(patient));
    }

    @Transactional
    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Patient not found with id: " + id);
        }
        patientRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public PatientResponse getPatientByEmail(String email) {
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with email: " + email));

        return PatientResponse.from(patient);
    }
}
