package com.pm.patientservice.repository;


import com.pm.patientservice.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
    boolean existsByEmail(String email); // returns true if email is in patient table
    boolean existsByEmailAndIdNot(String email, UUID id); // returns true if there is email addresses used by a different patient.
}
