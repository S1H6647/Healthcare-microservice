package com.healthcare.inventory.controller;

import com.healthcare.inventory.dto.DeductStockRequest;
import com.healthcare.inventory.dto.MedicineRequest;
import com.healthcare.inventory.dto.MedicineResponse;
import com.healthcare.inventory.service.MedicineService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
@RequiredArgsConstructor
@Tag(name = "Medicines", description = "Medicine inventory management")
public class MedicineController {

    private final MedicineService medicineService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MedicineResponse> addMedicine(
            @RequestPart("request") @Valid MedicineRequest request,
            @RequestPart("file") MultipartFile file
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(medicineService.addMedicine(request, file));
    }

    @GetMapping
    public ResponseEntity<List<MedicineResponse>> getAllMedicines() {
        return ResponseEntity.ok(medicineService.getAllMedicines());
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MedicineResponse> updateMedicine(
            @PathVariable Long id,
            @RequestPart(value = "request") @Valid MedicineRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        return ResponseEntity.ok(medicineService.updateMedicine(id, request, file));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedicine(@PathVariable Long id) {
        medicineService.deleteMedicine(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/deduct")
    public ResponseEntity<Void> deductStock(
            @RequestBody @Valid List<DeductStockRequest> request
    ) {
        medicineService.deductStock(request);
        return ResponseEntity.ok().build();
    }
}
