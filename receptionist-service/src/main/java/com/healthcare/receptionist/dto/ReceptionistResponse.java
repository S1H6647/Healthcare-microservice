package com.healthcare.receptionist.dto;

import com.healthcare.receptionist.entity.Receptionist;
import java.time.LocalDateTime;

public record ReceptionistResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        String address,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ReceptionistResponse from(Receptionist receptionist) {
        return new ReceptionistResponse(
                receptionist.getId(),
                receptionist.getFirstName(),
                receptionist.getLastName(),
                receptionist.getEmail(),
                receptionist.getPhone(),
                receptionist.getAddress(),
                receptionist.getCreatedAt(),
                receptionist.getUpdatedAt()
        );
    }
}
