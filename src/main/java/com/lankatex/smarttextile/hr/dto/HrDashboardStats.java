// Package = the folder this file lives in. Must match the folder path.
package com.lankatex.smarttextile.hr.dto;

// A "record" is a short way to write a class that only carries data (DTO = Data Transfer Object).
// Java automatically creates the constructor, getters and toString for the 4 values below.
// The getters are named after the fields: activeEmployeeCount(), activeShiftCount(), ...
public record HrDashboardStats(
        long activeEmployeeCount,   // How many employees are not archived
        long activeShiftCount,      // How many shifts are not archived
        long todayAttendanceCount,  // How many attendance records exist for today's date
        long pendingLeaveCount) {   // How many leave requests are still PENDING
}