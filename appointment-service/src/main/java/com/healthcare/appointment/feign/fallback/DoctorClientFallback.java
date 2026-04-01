package com.healthcare.appointment.feign.fallback;

import com.healthcare.appointment.dto.DoctorResponse;
import com.healthcare.appointment.feign.DoctorClient;
import org.springframework.stereotype.Component;

@Component
public class DoctorClientFallback implements DoctorClient {
    @Override
    public DoctorResponse getDoctorById(Long id) {
        return null;
    }
}
