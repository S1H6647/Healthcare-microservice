package com.healthcare.receptionist.service;

import com.healthcare.receptionist.dto.ReceptionistRequest;
import com.healthcare.receptionist.dto.ReceptionistResponse;
import com.healthcare.receptionist.dto.UpdateReceptionistProfileRequest;
import com.healthcare.receptionist.entity.Receptionist;
import com.healthcare.receptionist.exception.DuplicateEmailException;
import com.healthcare.receptionist.exception.ResourceNotFoundException;
import com.healthcare.receptionist.repository.ReceptionistRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReceptionistService {

    private final ReceptionistRepository receptionistRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ReceptionistResponse registerReceptionist(ReceptionistRequest request) {
        if (receptionistRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException(
                    "Receptionist with email " + request.email() + " already exists"
            );
        }

        Receptionist receptionist = Receptionist.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .phone(request.phone())
                .address(request.address())
                .build();

        return ReceptionistResponse.from(receptionistRepository.save(receptionist));
    }

    @Transactional(readOnly = true)
    public ReceptionistResponse getReceptionistById(Long id) {
        Receptionist receptionist = receptionistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receptionist not found with id: " + id));
        return ReceptionistResponse.from(receptionist);
    }

    @Transactional(readOnly = true)
    public List<ReceptionistResponse> getAllReceptionists() {
        return receptionistRepository.findAll().stream()
                .map(ReceptionistResponse::from)
                .toList();
    }

    @Transactional
    public void deleteReceptionist(Long id) {
        if (!receptionistRepository.existsById(id)) {
            throw new ResourceNotFoundException("Receptionist not found with id: " + id);
        }
        receptionistRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public ReceptionistResponse getReceptionistByEmail(String email) {
        Receptionist receptionist = receptionistRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Receptionist not found with email: " + email));
        return ReceptionistResponse.from(receptionist);
    }

    public ReceptionistResponse updateReceptionist(String email, @Valid UpdateReceptionistProfileRequest request) {
        Receptionist receptionist = receptionistRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Receptionist not found with email: " + email));

        receptionist.setFirstName(request.firstName());
        receptionist.setLastName(request.lastName());
        receptionist.setPhone(request.phone());
        receptionist.setAddress(request.address());

        return ReceptionistResponse.from(receptionistRepository.save(receptionist));
    }
}
