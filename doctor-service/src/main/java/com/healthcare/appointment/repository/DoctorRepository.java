package com.healthcare.appointment.repository;

import com.healthcare.appointment.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByLicenseNumber(String licenseNumber);

    List<Doctor> findBySpecialization(String specialization);
}