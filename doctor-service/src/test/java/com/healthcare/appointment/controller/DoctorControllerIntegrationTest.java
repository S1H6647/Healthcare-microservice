package com.healthcare.appointment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthcare.appointment.dto.DoctorRequest;
import com.healthcare.appointment.entity.Doctor;
import com.healthcare.appointment.repository.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DoctorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        doctorRepository.deleteAll();
    }

    @Test
    void registerDoctor_ShouldReturnCreated_WhenValidRequest() throws Exception {
        DoctorRequest request = new DoctorRequest(
                "Jane",
                "Smith",
                "jane.smith@example.com",
                "9876543210",
                "Cardiology",
                "LIC-99999",
                "Mon,Wed,Fri",
                new BigDecimal("500.00"),
                LocalDateTime.now()
        );

        mockMvc.perform(post("/api/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.licenseNumber").value("LIC-99999"));

        assertThat(doctorRepository.findAll()).hasSize(1);
    }

    @Test
    void getDoctor_ShouldReturnDoctor_WhenExists() throws Exception {
        Doctor doctor = Doctor.builder()
                .firstName("Alice")
                .lastName("Wong")
                .email("alice.wong@example.com")
                .phone("9800001111")
                .specialization("Neurology")
                .licenseNumber("LIC-11111")
                .availableDays("SUNDAY, MONDAY, THURSDAY")
                .consultationFee(new BigDecimal("1000.00"))
                .build();
        Doctor saved = doctorRepository.save(doctor);

        mockMvc.perform(get("/api/doctors/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.specialization").value("Neurology"));
    }

    @Test
    void getDoctor_ShouldReturnNotFound_WhenDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/doctors/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteDoctor_ShouldReturnNoContent_WhenExists() throws Exception {
        Doctor doctor = Doctor.builder()
                .firstName("Bob")
                .lastName("Brown")
                .email("bob.brown@example.com")
                .phone("9822223333")
                .specialization("Ortho")
                .licenseNumber("LIC-22222")
                .consultationFee(new BigDecimal("600.00"))
                .build();
        Doctor saved = doctorRepository.save(doctor);

        mockMvc.perform(delete("/api/doctors/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        assertThat(doctorRepository.existsById(saved.getId())).isFalse();
    }
}
