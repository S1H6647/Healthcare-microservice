package com.healthcare.prescription.feign;

import com.healthcare.prescription.dto.DeductStockRequest;
import com.healthcare.prescription.feign.fallback.InventoryClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(
        name = "inventory-service",
        fallback = InventoryClientFallback.class
)
public interface InventoryClient {

    @PostMapping("/api/medicines/deduct")
    void deductStock(@RequestBody List<DeductStockRequest> request);
}
