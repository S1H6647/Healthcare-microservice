package com.healthcare.appointment.feign.fallback;

import com.healthcare.appointment.dto.PatientResponse;
import com.healthcare.appointment.feign.PatientClient;
import org.springframework.stereotype.Component;

@Component
public class PatientClientFallback implements PatientClient {
    @Override
    public PatientResponse getPatientById(Long id) {
        return null;
    }
}
