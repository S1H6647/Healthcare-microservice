package com.healthcare.prescription.feign.fallback;

import com.healthcare.prescription.dto.DeductStockRequest;
import com.healthcare.prescription.feign.InventoryClient;

import java.util.List;

public class InventoryClientFallback implements InventoryClient {
    @Override
    public void deductStock(List<DeductStockRequest> request) {

    }
}
