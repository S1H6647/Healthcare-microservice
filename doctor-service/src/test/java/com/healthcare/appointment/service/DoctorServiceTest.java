package com.healthcare.appointment.service;

import com.healthcare.appointment.dto.DoctorRequest;
import com.healthcare.appointment.dto.DoctorResponse;
import com.healthcare.appointment.entity.Doctor;
import com.healthcare.appointment.exception.DuplicateResourceException;
import com.healthcare.appointment.exception.ResourceNotFoundException;
import com.healthcare.appointment.repository.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorService doctorService;

    private DoctorRequest doctorRequest;
    private Doctor doctor;

    @BeforeEach
    void setUp() {
        doctorRequest = new DoctorRequest(
                "Jane",
                "Smith",
                "jane.smith@example.com",
                "9876543210",
                "Cardiology",
                "LIC-12345",
                "Mon,Wed,Fri",
                new BigDecimal("500.00"),
                LocalDateTime.now()
        );

        doctor = Doctor.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .phone("9876543210")
                .specialization("Cardiology")
                .licenseNumber("LIC-12345")
                .availableDays("Mon,Wed,Fri")
                .consultationFee(new BigDecimal("500.00"))
                .build();
    }

    @Test
    void registerDoctor_ShouldSaveDoctor_WhenValidRequest() {
        when(doctorRepository.existsByEmail(anyString())).thenReturn(false);
        when(doctorRepository.existsByLicenseNumber(anyString())).thenReturn(false);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);

        DoctorResponse response = doctorService.registerDoctor(doctorRequest);

        assertThat(response).isNotNull();
        assertThat(response.email()).isEqualTo(doctorRequest.email());
        verify(doctorRepository, times(1)).save(any(Doctor.class));
    }

    @Test
    void registerDoctor_ShouldThrowException_WhenEmailAlreadyExists() {
        when(doctorRepository.existsByEmail(doctorRequest.email())).thenReturn(true);

        assertThatThrownBy(() -> doctorService.registerDoctor(doctorRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already exists");

        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void getDoctorById_ShouldReturnDoctor_WhenIdExists() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        DoctorResponse response = doctorService.getDoctorById(1L);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
    }

    @Test
    void getDoctorById_ShouldThrowException_WhenIdDoesNotExist() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> doctorService.getDoctorById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Doctor not found");
    }

    @Test
    void deleteDoctor_ShouldCallRepository_WhenIdExists() {
        when(doctorRepository.existsById(1L)).thenReturn(true);
        doNothing().when(doctorRepository).deleteById(1L);

        doctorService.deleteDoctor(1L);

        verify(doctorRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteDoctor_ShouldThrowException_WhenIdDoesNotExist() {
        when(doctorRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> doctorService.deleteDoctor(1L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(doctorRepository, never()).deleteById(anyLong());
    }
}
