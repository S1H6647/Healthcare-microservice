package com.healthcare.prescription.service;

import com.healthcare.prescription.dto.*;
import com.healthcare.prescription.entity.MedicineSummary;
import com.healthcare.prescription.entity.Prescription;
import com.healthcare.prescription.exception.AccessDeniedException;
import com.healthcare.prescription.exception.ResourceNotFoundException;
import com.healthcare.prescription.feign.DoctorClient;
import com.healthcare.prescription.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final DoctorClient doctorClient;

    @Transactional
    public PrescriptionResponse createPrescription(PrescriptionRequest request) {
        Prescription prescription = Prescription.builder()
                .patientId(request.patientId())
                .patientName(request.patientName())
                .doctorId(request.doctorId())
                .doctorName(request.doctorName())
                .visitDate(request.visitDate())
                .symptoms(request.symptoms())
                .diagnosis(request.diagnosis())
                .note(request.note())
                .build();

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
//        prescription.setMedicineName(request.medicineName());
//        prescription.setShortDescription(request.shortDescription());
//        prescription.setMedicineType(request.medicineType());
//        prescription.setInstruction(request.instruction());
//        prescription.setQuantity(request.quantity());
//        prescription.setIntakeDuration(request.intakeDuration());
        prescription.setVisitDate(request.visitDate());
        prescription.setSymptoms(request.symptoms());
        prescription.setDiagnosis(request.diagnosis());
        prescription.setNote(request.note());

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

        if (doctor.id().equals(prescription.getDoctorId())) {
            throw new AccessDeniedException("Access denied! Only doctors can create prescriptions");
        }

        MedicineSummary medicineSummary = MedicineSummary.builder()
                .drugName(request.drugName())
                .dosage(request.dosage())
                .frequency(request.frequency())
                .durationDays(request.durationDays())
                .route(request.route())
                .instructions(request.instructions())
                .prescription(prescription)
                .build();

        return MedicineItemResponse.from(medicineSummary);
    }
}
