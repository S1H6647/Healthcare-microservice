package com.healthcare.prescription.service;

import com.healthcare.prescription.dto.*;
import com.healthcare.prescription.entity.MedicineSummary;
import com.healthcare.prescription.entity.Prescription;
import com.healthcare.prescription.entity.PrescriptionStatus;
import com.healthcare.prescription.exception.AccessDeniedException;
import com.healthcare.prescription.exception.ResourceNotFoundException;
import com.healthcare.prescription.feign.DoctorClient;
import com.healthcare.prescription.feign.InventoryClient;
import com.healthcare.prescription.repository.MedicineSummaryRepository;
import com.healthcare.prescription.repository.PrescriptionRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private static final Logger log = LoggerFactory.getLogger(PrescriptionService.class);
    private final PrescriptionRepository prescriptionRepository;
    private final DoctorClient doctorClient;
    private final MedicineSummaryRepository medicineSummaryRepository;
    private final InventoryClient inventoryClient;

    @Transactional
    public PrescriptionResponse createPrescription(PrescriptionRequest request) {
        Prescription prescription = Prescription.builder()
                .patientId(request.patientId())
                .patientName(request.patientName())
                .doctorId(request.doctorId())
                .doctorName(request.doctorName())
                .instruction(request.instruction())
                .visitDate(request.visitDate())
                .symptoms(request.symptoms())
                .diagnosis(request.diagnosis())
                .note(request.note())
                .status(PrescriptionStatus.PENDING)
                .build();

        if (request.items() != null && !request.items().isEmpty()) {
            List<MedicineSummary> medicines = request.items().stream()
                    .map(item -> MedicineSummary.builder()
                            .medicineId(item.medicineId())
                            .drugName(item.drugName())
                            .dosage(item.dosage())
                            .frequency(item.frequency())
                            .durationDays(item.durationDays())
                            .route(item.route())
                            .instructions(item.instructions())
                            .prescription(prescription)
                            .build())
                    .toList();
            prescription.setMedicines(medicines);
        }

        return PrescriptionResponse.from(prescriptionRepository.save(prescription));
    }

    @Transactional(readOnly = true)
    public PrescriptionResponse getPrescriptionById(Long id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Prescription not found with id: " + id
                ));
        return PrescriptionResponse.from(prescription);
    }

    @Transactional(readOnly = true)
    public List<PrescriptionResponse> getAllPrescriptions() {
        return prescriptionRepository.findAll().stream()
                .map(PrescriptionResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PrescriptionResponse> getPrescriptionsByPatientId(Long patientId) {
        return prescriptionRepository.findByPatientId(patientId).stream()
                .map(PrescriptionResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PrescriptionResponse> getPrescriptionsByDoctorId(Long doctorId) {
        return prescriptionRepository.findByDoctorId(doctorId).stream()
                .map(PrescriptionResponse::from)
                .toList();
    }

    @Transactional
    public PrescriptionResponse updatePrescription(Long id, PrescriptionRequest request) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Prescription not found with id: " + id
                ));

        prescription.setPatientId(request.patientId());
        prescription.setPatientName(request.patientName());
        prescription.setDoctorId(request.doctorId());
        prescription.setDoctorName(request.doctorName());
        prescription.setInstruction(request.instruction());
        prescription.setVisitDate(request.visitDate());
        prescription.setSymptoms(request.symptoms());
        prescription.setDiagnosis(request.diagnosis());
        prescription.setNote(request.note());

        if (request.items() != null) {
            prescription.getMedicines().clear();
            List<MedicineSummary> medicines = request.items().stream()
                    .map(item -> MedicineSummary.builder()
                            .medicineId(item.medicineId())
                            .drugName(item.drugName())
                            .dosage(item.dosage())
                            .frequency(item.frequency())
                            .durationDays(item.durationDays())
                            .route(item.route())
                            .instructions(item.instructions())
                            .prescription(prescription)
                            .build())
                    .toList();
            prescription.getMedicines().addAll(medicines);
        }

        return PrescriptionResponse.from(prescriptionRepository.save(prescription));
    }

    @Transactional
    public void deletePrescription(Long id) {
        if (!prescriptionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Prescription not found with id: " + id);
        }
        prescriptionRepository.deleteById(id);
    }

    public MedicineItemResponse addMedicine(Long id, MedicineItemRequest request, String email) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found for id: " + id));

        DoctorResponse doctor = doctorClient.getMyDoctor(email);

        if (!doctor.id().equals(prescription.getDoctorId())) {
            throw new AccessDeniedException("Access denied! Only doctors can create prescriptions");
        }

        MedicineSummary medicineSummary = MedicineSummary.builder()
                .medicineId(request.medicineId())
                .drugName(request.drugName())
                .dosage(request.dosage())
                .frequency(request.frequency())
                .durationDays(request.durationDays())
                .route(request.route())
                .instructions(request.instructions())
                .prescription(prescription)
                .build();

        medicineSummaryRepository.save(medicineSummary);
        return MedicineItemResponse.from(medicineSummary);
    }

    @Transactional
    public PrescriptionResponse updateStatus(Long id, PrescriptionStatus status) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found for id: " + id));

        prescription.setStatus(status);

        if (status == PrescriptionStatus.DISPENSED) {
            List<DeductStockRequest> stockRequests = prescription.getMedicines().stream()
                    .map(medicine ->
                            new DeductStockRequest(
                                    medicine.getMedicineId(),
                                    calculateTotalPills(medicine.getFrequency(), medicine.getDurationDays())))
                    .toList();
            log.debug("Deducting stock for prescription id {}: {}", id, stockRequests);
            inventoryClient.deductStock(stockRequests);
        }

        return PrescriptionResponse.from(prescriptionRepository.save(prescription));
    }

    private @NotNull Integer calculateTotalPills(String frequency, Integer durationDays) {
        int pillsPerDay = switch (frequency.toUpperCase()) {
            case "OD" -> 1;          // Once Daily
            case "BID" -> 2;          // Twice Daily
            case "TID" -> 3;          // Three Times Daily
            case "QID" -> 4;          // Four Times Daily
            case "PRN" -> 0;          // As Needed — indeterminate, return 0 or handle separately
            case "STAT" -> 1;          // One-time immediate dose, not multiplied by days
            default -> throw new IllegalArgumentException("Unknown frequency: " + frequency);
        };

        // STAT is a single one-time dose regardless of durationDays
        if (frequency.equalsIgnoreCase("STAT")) return 1;

        // PRN has no fixed count
        if (frequency.equalsIgnoreCase("PRN")) return 0;

        return pillsPerDay * durationDays;
    }

}
