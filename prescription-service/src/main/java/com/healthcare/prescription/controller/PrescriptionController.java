package com.healthcare.prescription.controller;

import com.healthcare.prescription.dto.MedicineItemRequest;
import com.healthcare.prescription.dto.MedicineItemResponse;
import com.healthcare.prescription.dto.PrescriptionRequest;
import com.healthcare.prescription.dto.PrescriptionResponse;
import com.healthcare.prescription.entity.PrescriptionStatus;
import com.healthcare.prescription.exception.ForbiddenException;
import com.healthcare.prescription.service.PrescriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping
    public ResponseEntity<PrescriptionResponse> createPrescription(
            @Valid @RequestBody PrescriptionRequest request) {
        return new ResponseEntity<>(
                prescriptionService.createPrescription(request),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrescriptionResponse> getPrescription(@PathVariable Long id) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionById(id));
    }

    @GetMapping
    public ResponseEntity<List<PrescriptionResponse>> getAllPrescriptions() {
        return ResponseEntity.ok(prescriptionService.getAllPrescriptions());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PrescriptionResponse>> getPrescriptionsByPatient(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByPatientId(patientId));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<PrescriptionResponse>> getPrescriptionsByDoctor(
            @PathVariable Long doctorId) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByDoctorId(doctorId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PrescriptionResponse> updatePrescription(
            @PathVariable Long id,
            @Valid @RequestBody PrescriptionRequest request) {
        return ResponseEntity.ok(prescriptionService.updatePrescription(id, request));
    }

    @PreAuthorize("hasRole('PHARMACIST')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<PrescriptionResponse> updatePrescriptionStatus(
            @PathVariable Long id,
            @RequestParam PrescriptionStatus status) {
        return ResponseEntity.ok(prescriptionService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrescription(@PathVariable Long id) {
        prescriptionService.deletePrescription(id);
        return ResponseEntity.noContent().build();
    }

    // Medicines
    @PostMapping("/{id}/items")
    public ResponseEntity<MedicineItemResponse> addMedicine(
            @PathVariable Long id,
            @RequestBody @Valid MedicineItemRequest request,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Email") String email
    ) {
        if (!"DOCTOR".equals(role)) {
            throw new ForbiddenException("Only doctors can create prescriptions");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(prescriptionService.addMedicine(id, request, email));
    }
}
