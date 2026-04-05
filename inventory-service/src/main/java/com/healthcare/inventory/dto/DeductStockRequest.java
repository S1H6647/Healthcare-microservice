package com.healthcare.inventory.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DeductStockRequest(
        @NotNull Long medicineId,
        @NotNull @Positive(message = "Quantity to deduct must be greater than zero") Integer quantityToDeduct
) {
}
