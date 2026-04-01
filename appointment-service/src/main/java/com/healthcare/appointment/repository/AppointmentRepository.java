package com.healthcare.appointment.repository;

import com.healthcare.appointment.entity.Appointment;
import com.healthcare.appointment.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("""
            SELECT COUNT(a) > 0
            FROM Appointment a
            WHERE a.doctorId = :doctorId
              AND a.appointmentDate = :date
              AND a.status NOT IN :excludedStatuses
              AND (a.startTime < :endTime AND a.endTime > :startTime)
            """)
    boolean existsConflict(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("excludedStatuses") List<AppointmentStatus> excludedStatuses
    );

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByDoctorId(Long doctorId);
}
