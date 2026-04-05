package com.healthcare.auth.service;

import com.healthcare.auth.dto.*;
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
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException(
                    String.format("User with email %s already exists", request.email()));
        }

        if (request.phone() != null && userRepository.existsByPhone(request.phone())) {
            throw new DuplicateResourceException(
                    String.format("User with phone %s already exists", request.phone()));
        }

        User user = User.builder()
                .name(fullname(request.firstName(), request.lastName()))
                .email(request.email())
                .phone(request.phone())
                .password(passwordEncoder.encode(request.password()))
                .role(UserStatus.PATIENT) // Force PATIENT status for public registration
                .build();

        var saved = userRepository.save(user);

        return RegisterResponse.from(saved);
    }

    public RegisterResponse registerReceptionist(@Valid RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException(
                    String.format("User with email %s already exists", request.email()));
        }

        if (request.phone() != null && userRepository.existsByPhone(request.phone())) {
            throw new DuplicateResourceException(
                    String.format("User with phone %s already exists", request.phone()));
        }

        User user = User.builder()
                .name(fullname(request.firstName(), request.lastName()))
                .email(request.email())
                .phone(request.phone())
                .password(passwordEncoder.encode(request.password()))
                .role(UserStatus.RECEPTIONIST)
                .build();

        var saved = userRepository.save(user);

        return RegisterResponse.from(saved);
    }

    public LoginResponse login(@Valid LoginRequest request) {
        User user = userRepository.findUserByEmail(request.identifier())
                .or(() -> userRepository.findUserByPhone(request.identifier()))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), request.password())
        );

        return LoginResponse.from(user, jwtService.generateToken(user));
    }

    public RegisterResponse registerDoctor(@Valid RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException(
                    String.format("User with email %s already exists", request.email()));
        }

        if (request.phone() != null && userRepository.existsByPhone(request.phone())) {
            throw new DuplicateResourceException(
                    String.format("User with phone %s already exists", request.phone()));
        }

        User user = User.builder()
                .name(fullname(request.firstName(), request.lastName()))
                .email(request.email())
                .phone(request.phone())
                .password(passwordEncoder.encode(request.password()))
                .role(UserStatus.DOCTOR)
                .build();

        var saved = userRepository.save(user);

        return RegisterResponse.from(saved);
    }

    // Helper method
    private String fullname(String firstname, String lastname) {
        return firstname + " " + lastname;
    }

    public void changePassword(String email, ChangePasswordRequest request) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }
}
