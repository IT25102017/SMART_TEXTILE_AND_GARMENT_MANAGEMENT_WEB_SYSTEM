// Package = the folder this file lives in. Must match the folder path.
package com.lankatex.smarttextile.hr.entity;

import jakarta.persistence.*;                          // JPA annotations that map this class to a DB table
import jakarta.validation.constraints.NotBlank;        // Validation: text cannot be null or empty
import jakarta.validation.constraints.NotNull;         // Validation: value cannot be null
import lombok.Getter;                                  // Lombok: auto-generates getters
import lombok.NoArgsConstructor;                       // Lombok: auto-generates an empty constructor
import lombok.Setter;                                  // Lombok: auto-generates setters

import java.time.LocalDate;                            // Java type for a date (2026-10-08), with no time

@Entity                                                // This class represents a database table
@Table(name = "leave_requests")                        // The table name in SQL Server is "leave_requests"
@Getter                                                // Generate getters for all fields
@Setter                                                // Generate setters for all fields
@NoArgsConstructor                                     // Generate "new LeaveRequest()" constructor
public class LeaveRequest {

    @Id                                                // Primary key of the table
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Database auto-increments the ID
    @Column(name = "leave_id")                         // Maps to column "leave_id"
    private Long leaveId;                              // Internal ID (the UI shows it as LEV-001)

    @NotNull(message = "Employee is required")         // Form error if no employee is selected
    @Column(name = "employee_id", nullable = false)    // Column cannot be NULL in the database
    private Long employeeId;                           // The employee asking for leave (chosen from a dropdown)

    @NotBlank(message = "Leave type is required")      // Form error if empty
    @Column(name = "leave_type", nullable = false)
    private String leaveType;                          // ANNUAL, CASUAL, MEDICAL or NO_PAY

    @NotNull(message = "Start date is required")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;                       // First day of leave

    @NotNull(message = "End date is required")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;                         // Last day of leave (the service checks it is not before startDate)

    @NotBlank(message = "Reason is required")
    @Column(name = "reason", nullable = false, length = 1000) // length = 1000 allows up to 1000 characters in the database
    private String reason;                             // Why the employee needs leave

    @Column(name = "status")
    private String status = "PENDING";                 // Starts as PENDING; becomes APPROVED, REJECTED or ARCHIVED

    @Column(name = "approved_by")                      // Empty until someone approves or rejects
    private Long approvedBy;                           // Employee ID of the approver (chosen from a dropdown)
}