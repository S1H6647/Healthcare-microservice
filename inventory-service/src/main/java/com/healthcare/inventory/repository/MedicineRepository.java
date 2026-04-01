package com.healthcare.inventory.repository;

import com.healthcare.inventory.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {
}
