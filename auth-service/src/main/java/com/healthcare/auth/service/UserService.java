package com.healthcare.auth.service;

import com.healthcare.auth.dto.LoginRequest;
import com.healthcare.auth.dto.LoginResponse;
import com.healthcare.auth.dto.RegisterRequest;
import com.healthcare.auth.dto.RegisterResponse;
import com.healthcare.auth.entity.User;
import com.healthcare.auth.entity.UserStatus;
import com.healthcare.auth.exception.DuplicateResourceException;
import com.healthcare.auth.exception.ResourceNotFoundException;
import com.healthcare.auth.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public RegisterResponse register(@Valid RegisterRequest request) {
        boolean exists = userRepository.existsByEmail(request.email());

        if (exists) {
            throw new DuplicateResourceException(
                    String.format("User with email %s already exists", request.email()));
        }

        User user = User.builder()
                .name(fullname(request.firstName(), request.lastName()))
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(UserStatus.valueOf(request.role()))
                .build();

        var saved = userRepository.save(user);

        return RegisterResponse.from(saved);
    }

    public LoginResponse login(@Valid LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findUserByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return LoginResponse.from(user, jwtService.generateToken(user));
    }

    // Helper method
    private String fullname(String firstname, String lastname) {
        return firstname + " " + lastname;
    }
}
