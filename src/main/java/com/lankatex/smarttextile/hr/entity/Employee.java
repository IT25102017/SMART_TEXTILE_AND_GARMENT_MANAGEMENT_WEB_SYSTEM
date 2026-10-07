// Package = the folder this file lives in. Must match the folder path exactly.
package com.lankatex.smarttextile.hr.entity;

// Imports: bring in classes from libraries so we can use them below.
import jakarta.persistence.*;                          // JPA annotations (@Entity, @Table, @Id, @Column...) that map this class to a DB table
import jakarta.validation.constraints.NotBlank;        // Validation: field cannot be null or empty text
import lombok.Getter;                                  // Lombok: auto-generates getX() methods
import lombok.NoArgsConstructor;                       // Lombok: auto-generates an empty constructor (JPA needs one)
import lombok.Setter;                                  // Lombok: auto-generates setX() methods

@Entity                                                // Tells Spring/Hibernate: this class represents a database table
@Table(name = "employees")                             // The table name in SQL Server is "employees"
@Getter                                                // Generate getters for all fields (no need to write them by hand)
@Setter                                                // Generate setters for all fields
@NoArgsConstructor                                     // Generate "new Employee()" constructor
public class Employee {

    @Id                                                // This field is the primary key of the table
    @GeneratedValue(strategy = GenerationType.IDENTITY) // The database auto-increments the ID (1, 2, 3...)
    @Column(name = "employee_id")                      // Maps to the column "employee_id"
    private Long employeeId;                           // Internal ID (users never type this; they see employeeCode instead)

    @NotBlank(message = "Employee code is required")  // Form fails with this message if empty
    @Column(name = "employee_code", nullable = false)  // Column cannot be NULL in the database
    private String employeeCode;                       // Readable code like EMP-001

    @NotBlank(message = "Full name is required")
    @Column(name = "full_name", nullable = false)
    private String fullName;                           // Employee's full name

    @NotBlank(message = "NIC number is required")
    @Column(name = "nic_no", nullable = false)
    private String nicNo;                              // National ID card number

    @Column(name = "department_id")                    // Optional (no @NotBlank, nullable by default)
    private Long departmentId;                         // Raw reference to a department (master table comes later)

    @NotBlank(message = "Designation is required")
    @Column(name = "designation", nullable = false)
    private String designation;                        // Job title, e.g. HR Officer

    @NotBlank(message = "Employment type is required")
    @Column(name = "employment_type", nullable = false)
    private String employmentType;                     // FULL_TIME, PART_TIME, CONTRACT or INTERN

    @Column(name = "status")
    private String status = "ACTIVE";                  // Default value is ACTIVE; can become INACTIVE or ARCHIVED

    @PrePersist                                        // Runs automatically just BEFORE a new row is inserted
    @PreUpdate                                         // Also runs just BEFORE an existing row is updated
    private void normalize() {                         // Cleans the data before saving
        if (employeeCode != null) {                    // Only clean if a value exists (avoids NullPointerException)
            employeeCode = employeeCode.trim().toUpperCase(); // Remove spaces, make uppercase: " emp-001 " -> "EMP-001"
        }
        if (nicNo != null) {
            nicNo = nicNo.trim().toUpperCase();        // Same cleanup for NIC
        }
        if (status == null || status.isBlank()) {      // If status is missing or empty...
            status = "ACTIVE";                         // ...default it to ACTIVE
        }
    }
}