package com.healthcare.prescription.feign;

import com.healthcare.prescription.dto.DoctorResponse;
import com.healthcare.prescription.feign.fallback.DoctorClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "doctor-service",
        fallback = DoctorClientFallback.class
)
public interface DoctorClient {

    @GetMapping("/api/doctors/me")
    DoctorResponse getMyDoctor(@RequestHeader("X-User-Email") String email);
}
