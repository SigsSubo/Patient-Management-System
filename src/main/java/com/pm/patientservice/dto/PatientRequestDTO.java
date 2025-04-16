package com.pm.patientservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.aspectj.bridge.IMessage;
import org.springframework.format.annotation.DateTimeFormat;

public class PatientRequestDTO {


    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 chars")
    private String name;
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    @NotBlank(message = "Address is required")
    private String address;
    @NotBlank(message = "Date of Birth is required")
    private String dateOfBirth = "1970-01-01";
    @NotBlank(message = "Registered Date is required")
    private String registeredDate = "1970-01-01";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(String registeredDate) {
        this.registeredDate = registeredDate;
    }
}
