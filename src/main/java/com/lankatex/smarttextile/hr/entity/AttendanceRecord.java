// Package = the folder this file lives in. Must match the folder path.
package com.lankatex.smarttextile.hr.entity;

import jakarta.persistence.*;                          // JPA annotations that map this class to a DB table
import jakarta.validation.constraints.NotNull;         // Validation: value cannot be null
import lombok.Getter;                                  // Lombok: auto-generates getters
import lombok.NoArgsConstructor;                       // Lombok: auto-generates an empty constructor
import lombok.Setter;                                  // Lombok: auto-generates setters

import java.math.BigDecimal;                           // Exact decimal number type (good for hours like 8.50)
import java.time.LocalDate;                            // Java type for a date (2026-10-08), with no time
import java.time.LocalTime;                            // Java type for a time of day (08:15), with no date

@Entity                                                // This class represents a database table
@Table(name = "attendance_records")                    // The table name in SQL Server is "attendance_records"
@Getter                                                // Generate getters for all fields
@Setter                                                // Generate setters for all fields
@NoArgsConstructor                                     // Generate "new AttendanceRecord()" constructor
public class AttendanceRecord {

    @Id                                                // Primary key of the table
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Database auto-increments the ID
    @Column(name = "attendance_id")                    // Maps to column "attendance_id"
    private Long attendanceId;                         // Internal ID (the UI shows it as ATT-001)

    @NotNull(message = "Employee is required")         // Form error if no employee is selected
    @Column(name = "employee_id", nullable = false)    // Column cannot be NULL in the database
    private Long employeeId;                           // Which employee this record belongs to (chosen from a dropdown)

    @NotNull(message = "Shift is required")            // Form error if no shift is selected
    @Column(name = "shift_id", nullable = false)
    private Long shiftId;                              // Which shift the employee worked (chosen from a dropdown)

    @NotNull(message = "Attendance date is required")  // Form error if no date is chosen
    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;                  // The day this record is for

    @Column(name = "check_in")                         // Optional: empty means the employee did not come in
    private LocalTime checkIn;                         // Time the employee arrived

    @Column(name = "check_out")                        // Optional: empty means not checked out yet
    private LocalTime checkOut;                        // Time the employee left

    @Column(name = "status")
    private String status;                             // PRESENT / LATE / INCOMPLETE / ABSENT / ARCHIVED (set by the service, not typed by the user)

    @Column(name = "working_hours")
    private BigDecimal workingHours;                   // Calculated by the service: (check-out - check-in - break) in hours
}