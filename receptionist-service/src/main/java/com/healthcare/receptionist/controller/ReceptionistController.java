package com.healthcare.receptionist.controller;

import com.healthcare.receptionist.dto.ReceptionistRequest;
import com.healthcare.receptionist.dto.ReceptionistResponse;
import com.healthcare.receptionist.dto.UpdateReceptionistProfileRequest;
import com.healthcare.receptionist.service.ReceptionistService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/receptionists")
@RequiredArgsConstructor
@Tag(name = "Receptionists", description = "Receptionist management")
public class ReceptionistController {

    private final ReceptionistService receptionistService;

    @PostMapping("/register")
    public ResponseEntity<ReceptionistResponse> registerReceptionist(
            @Valid @RequestBody ReceptionistRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(receptionistService.registerReceptionist(request));
    }

    @GetMapping("/me")
    public ResponseEntity<ReceptionistResponse> getMyProfile(
            @RequestHeader("X-User-Email") String email) {
        return ResponseEntity.ok(receptionistService.getReceptionistByEmail(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReceptionistResponse> getReceptionist(@PathVariable Long id) {
        return ResponseEntity.ok(receptionistService.getReceptionistById(id));
    }

    @GetMapping
    public ResponseEntity<List<ReceptionistResponse>> getAllReceptionists() {
        return ResponseEntity.ok(receptionistService.getAllReceptionists());
    }

    @PutMapping
    public ResponseEntity<ReceptionistResponse> updateReceptionist(
            @RequestHeader("X-User-Email") String email,
            @Valid @RequestBody UpdateReceptionistProfileRequest request) {
        return ResponseEntity.ok(receptionistService.updateReceptionist(email, request));
    }
}
