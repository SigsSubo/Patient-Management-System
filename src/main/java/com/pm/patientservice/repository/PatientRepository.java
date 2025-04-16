package com.pm.patientservice.repository;

import com.pm.patientservice.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
// @Repository annotates classes at the persistence layer, which will act as a database repository.
@Repository
// By extending this interface, you can easily perform CRUD operations, pagination, sorting, and even custom queries, all without writing SQL code
public interface PatientRepository extends JpaRepository<Patient, UUID> { }
// JpaRepository provides operations such as "save" and "findAll"