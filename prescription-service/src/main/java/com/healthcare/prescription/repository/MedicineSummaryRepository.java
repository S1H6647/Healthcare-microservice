package com.healthcare.prescription.repository;

import com.healthcare.prescription.entity.MedicineSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicineSummaryRepository extends JpaRepository<MedicineSummary, Long> {
}
