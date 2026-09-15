package com.lankatex.smarttextile.hr.dto;

public record HrDashboardStats(
        long employeeCount,
        long shiftCount,
        long attendanceRecordCount,
        long leaveRequestCount
) {
}