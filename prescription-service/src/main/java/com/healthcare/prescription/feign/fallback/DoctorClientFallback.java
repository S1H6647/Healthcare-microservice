package com.healthcare.prescription.feign.fallback;

import com.healthcare.prescription.dto.DoctorResponse;
import com.healthcare.prescription.feign.DoctorClient;
import org.springframework.stereotype.Component;

@Component
public class DoctorClientFallback implements DoctorClient {

    @Override
    public DoctorResponse getMyDoctor(String email) {
        return null;
    }
}
