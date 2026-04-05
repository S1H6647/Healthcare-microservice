package com.healthcare.inventory.service;

import com.healthcare.inventory.dto.DeductStockRequest;
import com.healthcare.inventory.dto.MedicineRequest;
import com.healthcare.inventory.dto.MedicineResponse;
import com.healthcare.inventory.entity.Medicine;
import com.healthcare.inventory.exception.ResourceNotFoundException;
import com.healthcare.inventory.repository.MedicineRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicineService {

    private final MedicineRepository medicineRepository;
    private final StorageService storageService;

    @Transactional
    public MedicineResponse addMedicine(MedicineRequest request, MultipartFile file) {

        String imageUrl = storageService.uploadImage(file);

        Medicine medicine = Medicine.builder()
                .medicineName(request.medicineName())
                .shortDescription(request.shortDescription())
                .quantity(request.quantity())
                .medicineType(request.medicineType())
                .dosage(request.dosage())
                .dosageType(request.dosageType())
                .imageUrl(imageUrl)
                .build();

        return MedicineResponse.from(medicineRepository.save(medicine));
    }

    @Transactional(readOnly = true)
    public List<MedicineResponse> getAllMedicines() {
        return medicineRepository.findAll()
                .stream()
                .map(MedicineResponse::from)
                .toList();
    }

    @Transactional
    public MedicineResponse updateMedicine(Long id, MedicineRequest request, MultipartFile file) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Medicine not found with id: " + id
                ));

        String imageUrl = storageService.uploadImage(file);

        medicine.setMedicineName(request.medicineName());
        medicine.setShortDescription(request.shortDescription());
        medicine.setQuantity(request.quantity());
        medicine.setMedicineType(request.medicineType());
        medicine.setDosage(request.dosage());
        medicine.setDosageType(request.dosageType());
        if (imageUrl != null) {
            medicine.setImageUrl(imageUrl);
        }

        return MedicineResponse.from(medicineRepository.save(medicine));
    }

    @Transactional
    public void deleteMedicine(Long id) {
        if (!medicineRepository.existsById(id)) {
            throw new ResourceNotFoundException("Medicine not found with id: " + id);
        }
        medicineRepository.deleteById(id);
    }

    @Transactional
    public void deductStock(@Valid List<DeductStockRequest> request) {
        for (DeductStockRequest stockRequest : request) {

            Medicine medicine = medicineRepository.findById(stockRequest.medicineId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Medicine not found with id: " + stockRequest.medicineId()
                    ));

            if (medicine.getQuantity() < stockRequest.quantityToDeduct()) {
                throw new IllegalArgumentException(
                        "Insufficient stock for medicine: " + medicine.getMedicineName()
                );
            }

            medicine.setQuantity(medicine.getQuantity() - stockRequest.quantityToDeduct());
            medicineRepository.save(medicine);
        }
    }
}
