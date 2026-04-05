package com.healthcare.patient.feign;

import com.healthcare.patient.dto.AuthRegisterRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service")
public interface AuthClient {

    @PostMapping("/api/auth/register")
    void register(@RequestBody AuthRegisterRequest request);
}
