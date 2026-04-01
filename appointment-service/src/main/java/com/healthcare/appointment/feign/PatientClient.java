package com.healthcare.appointment.feign;

import com.healthcare.appointment.dto.PatientResponse;
import com.healthcare.appointment.feign.fallback.PatientClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "patient-service",
        fallback = PatientClientFallback.class
)
public interface PatientClient {

    @GetMapping("/api/patients/{id}")
    PatientResponse getPatientById(@PathVariable Long id);
}
