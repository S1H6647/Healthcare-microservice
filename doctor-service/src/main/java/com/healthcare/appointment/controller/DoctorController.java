package com.healthcare.appointment.controller;

import com.healthcare.appointment.dto.DoctorRequest;
import com.healthcare.appointment.dto.DoctorResponse;
import com.healthcare.appointment.service.DoctorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.healthcare.appointment.service.StorageService;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctors", description = "Doctor management")
public class DoctorController {

    private final DoctorService doctorService;
    private final StorageService storageService;

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DoctorResponse> registerDoctor(
            @RequestPart("doctor") @Valid DoctorRequest request,
            @RequestPart("qualificationFile") MultipartFile file) {
        String qualificationUrl = storageService.uploadFile(file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(doctorService.registerDoctor(request, qualificationUrl));
    }

    @GetMapping("/me")
    public ResponseEntity<DoctorResponse> getMyProfile(
            @RequestHeader(value = "X-User-Email")
            String email
    ) {
        return ResponseEntity.ok(doctorService.getDoctorByEmail(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponse> getDoctor(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    @GetMapping
    public ResponseEntity<List<DoctorResponse>> getAllDoctors(
            @RequestParam(required = false)
            String specialization
    ) {
        if (specialization != null && !specialization.isBlank()) {
            return ResponseEntity.ok(doctorService.getDoctorsBySpecialization(specialization));
        }
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DoctorResponse> updateDoctor(
            @PathVariable Long id,
            @RequestPart("doctor") @Valid DoctorRequest request,
            @RequestPart(value = "qualificationFile", required = false) MultipartFile file) {
        String qualificationUrl = null;
        if (file != null && !file.isEmpty()) {
            qualificationUrl = storageService.uploadFile(file);
        }
        return ResponseEntity.ok(doctorService.updateDoctor(id, request, qualificationUrl));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }


}