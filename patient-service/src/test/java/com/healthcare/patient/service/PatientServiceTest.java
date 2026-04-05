package com.healthcare.patient.service;

import com.healthcare.patient.dto.PatientRequest;
import com.healthcare.patient.dto.PatientResponse;
import com.healthcare.patient.entity.Patient;
import com.healthcare.patient.exception.DuplicateEmailException;
import com.healthcare.patient.exception.ResourceNotFoundException;
import com.healthcare.patient.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private AuthClient authClient;

    @InjectMocks
    private PatientService patientService;

    private PatientRequest patientRequest;
    private Patient patient;

    @BeforeEach
    void setUp() {
        patientRequest = new PatientRequest(
                "John",
                "Doe",
                "john.doe@example.com",
                "1234567890",
                LocalDate.of(1990, 1, 1),
                "Male",
                "123 Main St"
        );

        patient = Patient.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .gender("Male")
                .address("123 Main St")
                .build();
    }

    @Test
    void registerPatient_ShouldSavePatient_WhenEmailDoesNotExist() {
        when(patientRepository.existsByEmail(patientRequest.email())).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        PatientResponse response = patientService.registerPatient(patientRequest);

        assertThat(response).isNotNull();
        assertThat(response.email()).isEqualTo(patientRequest.email());
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    void registerPatient_ShouldThrowException_WhenEmailAlreadyExists() {
        when(patientRepository.existsByEmail(patientRequest.email())).thenReturn(true);

        assertThatThrownBy(() -> patientService.registerPatient(patientRequest))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("already exists");

        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void getPatientById_ShouldReturnPatient_WhenIdExists() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        PatientResponse response = patientService.getPatientById(1L);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
    }

    @Test
    void getPatientById_ShouldThrowException_WhenIdDoesNotExist() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.getPatientById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Patient not found");
    }

    @Test
    void deletePatient_ShouldCallRepository_WhenIdExists() {
        when(patientRepository.existsById(1L)).thenReturn(true);
        doNothing().when(patientRepository).deleteById(1L);

        patientService.deletePatient(1L);

        verify(patientRepository, times(1)).deleteById(1L);
    }

    @Test
    void deletePatient_ShouldThrowException_WhenIdDoesNotExist() {
        when(patientRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> patientService.deletePatient(1L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(patientRepository, never()).deleteById(anyLong());
    }
}
