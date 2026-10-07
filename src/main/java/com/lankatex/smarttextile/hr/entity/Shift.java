// Package = the folder this file lives in. Must match the folder path.
package com.lankatex.smarttextile.hr.entity;

import jakarta.persistence.*;                          // JPA annotations that map this class to a DB table
import jakarta.validation.constraints.NotBlank;        // Validation: text cannot be null or empty
import jakarta.validation.constraints.NotNull;         // Validation: value cannot be null (used for times, since they are not text)
import lombok.Getter;                                  // Lombok: auto-generates getters
import lombok.NoArgsConstructor;                       // Lombok: auto-generates an empty constructor
import lombok.Setter;                                  // Lombok: auto-generates setters

import java.time.LocalTime;                            // Java type for a time of day (08:00), with no date

@Entity                                                // This class represents a database table
@Table(name = "shifts")                                // The table name in SQL Server is "shifts"
@Getter                                                // Generate getters for all fields
@Setter                                                // Generate setters for all fields
@NoArgsConstructor                                     // Generate "new Shift()" constructor
public class Shift {

    @Id                                                // Primary key of the table
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Database auto-increments the ID
    @Column(name = "shift_id")                         // Maps to column "shift_id"
    private Long shiftId;                              // Internal ID (the UI shows it as SHIFT-001)

    @NotBlank(message = "Shift name is required")      // Form error if empty
    @Column(name = "shift_name", nullable = false)     // Column cannot be NULL in the database
    private String shiftName;                          // e.g. "Morning Shift"

    @NotNull(message = "Start time is required")       // Form error if no start time is chosen
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;                       // When the shift starts, e.g. 08:00 (used to detect LATE)

    @NotNull(message = "End time is required")
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;                         // When the shift ends, e.g. 17:00

    @Column(name = "break_duration")                   // Optional column
    private Integer breakDuration = 0;                 // Break length in minutes; default 0 (subtracted from working hours)

    @Column(name = "working_days")
    private String workingDays;                        // Text like "MON,TUE,WED,THU,FRI"

    @Column(name = "status")
    private String status = "ACTIVE";                  // Default ACTIVE; can become INACTIVE or ARCHIVED

    @PrePersist                                        // Runs just BEFORE a new row is inserted
    @PreUpdate                                         // Runs just BEFORE an existing row is updated
    private void normalize() {                         // Cleans the data before saving
        if (breakDuration == null || breakDuration < 0) { // If the break is missing or negative...
            breakDuration = 0;                         // ...reset it to 0
        }
        if (status == null || status.isBlank()) {      // If status is missing or empty...
            status = "ACTIVE";                         // ...default it to ACTIVE
        }
    }
}