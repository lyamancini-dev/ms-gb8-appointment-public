package com.example.medical.repository;

import com.example.medical.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    // Spring сам создаст SQL запрос на основе имени этого метода
    Patient findByFullName(String fullName);
}