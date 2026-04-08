package com.healthcare.patient.feign;

import com.healthcare.patient.dto.AuthRegisterRequest;

public class AuthClientFallback implements AuthClient {
    @Override
    public void register(AuthRegisterRequest request) {
    }
}
