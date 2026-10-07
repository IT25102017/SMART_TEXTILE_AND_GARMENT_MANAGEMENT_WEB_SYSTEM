package com.lankatex.smarttextile.hr.repository;

import com.lankatex.smarttextile.hr.entity.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {

    // All records, newest date first; if dates are equal, newest ID first
    List<AttendanceRecord> findAllByOrderByAttendanceDateDescAttendanceIdDesc();

    // true if any attendance row uses this employee (used to block deleting an employee with history)
    boolean existsByEmployeeId(Long employeeId);

    // true if any attendance row uses this shift (used to block deleting a shift with history)
    boolean existsByShiftId(Long shiftId);

    // true if this employee already has a record on this date (duplicate check helper)
    boolean existsByEmployeeIdAndAttendanceDate(Long employeeId, LocalDate attendanceDate);
}