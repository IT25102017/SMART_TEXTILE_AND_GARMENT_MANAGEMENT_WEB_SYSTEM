package com.lankatex.smarttextile.hr.service;

// DTO and entities we work with
import com.lankatex.smarttextile.hr.dto.HrDashboardStats;
import com.lankatex.smarttextile.hr.entity.AttendanceRecord;
import com.lankatex.smarttextile.hr.entity.Employee;
import com.lankatex.smarttextile.hr.entity.LeaveRequest;
import com.lankatex.smarttextile.hr.entity.Shift;
// Repositories = our connection to the database tables
import com.lankatex.smarttextile.hr.repository.AttendanceRecordRepository;
import com.lankatex.smarttextile.hr.repository.EmployeeRepository;
import com.lankatex.smarttextile.hr.repository.LeaveRequestRepository;
import com.lankatex.smarttextile.hr.repository.ShiftRepository;
import org.springframework.stereotype.Service;                         // Marks this class as a Spring service (business logic layer)
import org.springframework.transaction.annotation.Transactional;      // Wraps a method in a DB transaction (all-or-nothing)

import java.math.BigDecimal;                                          // Exact decimal numbers (for working hours)
import java.math.RoundingMode;                                        // How to round decimals (HALF_UP = 2.345 -> 2.35)
import java.time.Duration;                                            // Time difference between two times
import java.time.LocalDate;                                           // A date without time
import java.time.LocalTime;                                           // A time without date
import java.util.LinkedHashMap;                                       // A map that keeps insertion order
import java.util.List;
import java.util.Map;
import java.util.Objects;                                             // Objects.equals() compares safely even if a value is null
import java.util.function.Function;                                   // Used by Function.identity() below
import java.util.stream.Collectors;                                   // Used to turn lists into maps

@Service                                                              // Spring creates one instance of this class and manages it
public class HrService {

    // The 4 repositories. "final" = assigned once in the constructor and never changed.
    private final EmployeeRepository employeeRepository;
    private final ShiftRepository shiftRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    // Constructor injection: Spring automatically passes in the 4 repositories when it creates HrService.
    public HrService(
            EmployeeRepository employeeRepository,
            ShiftRepository shiftRepository,
            AttendanceRecordRepository attendanceRecordRepository,
            LeaveRequestRepository leaveRequestRepository) {
        this.employeeRepository = employeeRepository;                 // "this.x = x" stores the parameter in the field
        this.shiftRepository = shiftRepository;
        this.attendanceRecordRepository = attendanceRecordRepository;
        this.leaveRequestRepository = leaveRequestRepository;
    }

    // =====================================================
    // DASHBOARD
    // =====================================================

    // Calculates the 4 numbers shown on the HR dashboard cards.
    public HrDashboardStats getDashboardStats() {

        // Count employees that are not archived
        long activeEmployees = getAllEmployees().stream()             // stream() = process the list item by item
                .filter(this::isEmployeeActive)                       // keep only active employees
                .count();                                             // count what is left

        // Count shifts that are not archived
        long activeShifts = getAllShifts().stream()
                .filter(this::isShiftActive)
                .count();

        // Count attendance records dated today and not archived
        long todayAttendance = getAllAttendanceRecords().stream()
                .filter(record -> Objects.equals(record.getAttendanceDate(), LocalDate.now())) // date equals today
                .filter(record -> !"ARCHIVED".equalsIgnoreCase(record.getStatus()))            // and not archived
                .count();

        // Count leave requests still waiting for a decision
        long pendingLeaves = getAllLeaveRequests().stream()
                .filter(leave -> "PENDING".equalsIgnoreCase(leave.getStatus()))
                .count();

        // Pack the 4 numbers into the DTO and return it to the controller
        return new HrDashboardStats(
                activeEmployees,
                activeShifts,
                todayAttendance,
                pendingLeaves
        );
    }

    // =====================================================
    // EMPLOYEES
    // =====================================================

    // All employees, newest first (used for the employee table)
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAllByOrderByEmployeeIdDesc();
    }

    // Only employees that are not archived (used for dropdowns)
    public List<Employee> getActiveEmployees() {
        return getAllEmployees().stream()
                .filter(this::isEmployeeActive)
                .toList();                                            // convert the stream back into a list
    }

    // Builds a lookup table: employeeId -> Employee.
    // The HTML uses it to show "EMP-001 - Name" instead of a raw number.
    public Map<Long, Employee> getEmployeeMap() {
        return employeeRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Employee::getEmployeeId,                      // key = the employee's ID
                        Function.identity(),                          // value = the employee object itself
                        (first, second) -> first,                     // if two items have the same key, keep the first
                        LinkedHashMap::new                            // use a map that keeps order
                ));
    }

    // Finds one employee by ID, or throws an error if it doesn't exist
    public Employee getEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found."));
    }

    @Transactional                                                    // If anything fails inside, the DB changes are rolled back
    public Employee saveEmployee(Employee employee) {
        validateEmployee(employee);                                   // 1) check the required fields

        // 2) Block a duplicate employee code
        employeeRepository.findByEmployeeCodeIgnoreCase(employee.getEmployeeCode())
                .filter(existing -> !Objects.equals(existing.getEmployeeId(), employee.getEmployeeId())) // ignore the same employee when editing
                .ifPresent(existing -> {                              // if another employee has this code...
                    throw new IllegalArgumentException("Employee code already exists.");
                });

        // 3) Block a duplicate NIC number (same idea)
        employeeRepository.findByNicNoIgnoreCase(employee.getNicNo())
                .filter(existing -> !Objects.equals(existing.getEmployeeId(), employee.getEmployeeId()))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("NIC number already exists.");
                });

        // 4) Default the status to ACTIVE if it is empty
        if (employee.getStatus() == null || employee.getStatus().isBlank()) {
            employee.setStatus("ACTIVE");
        }

        return employeeRepository.save(employee);                     // 5) save: INSERT if new, UPDATE if it already has an ID
    }

    // Archive = soft delete. The row stays in the DB with status ARCHIVED.
    @Transactional
    public void archiveEmployee(Long id) {
        Employee employee = getEmployee(id);
        employee.setStatus("ARCHIVED");
        employeeRepository.save(employee);
    }

    // Restore = bring an archived employee back to ACTIVE
    @Transactional
    public void restoreEmployee(Long id) {
        Employee employee = getEmployee(id);
        employee.setStatus("ACTIVE");
        employeeRepository.save(employee);
    }

    // Permanent delete, but only if the employee has no attendance or leave history
    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = getEmployee(id);
        if (attendanceRecordRepository.existsByEmployeeId(id)
                || leaveRequestRepository.existsByEmployeeId(id)) {
            throw new IllegalArgumentException(
                    "Cannot permanently delete this employee because attendance or leave history already exists. Archive the employee instead."
            );
        }
        employeeRepository.delete(employee);
    }

    // Checks every required field is filled (a second safety layer behind the @NotBlank annotations)
    private void validateEmployee(Employee employee) {
        if (employee.getEmployeeCode() == null || employee.getEmployeeCode().isBlank()) {
            throw new IllegalArgumentException("Employee code is required.");
        }
        if (employee.getFullName() == null || employee.getFullName().isBlank()) {
            throw new IllegalArgumentException("Full name is required.");
        }
        if (employee.getNicNo() == null || employee.getNicNo().isBlank()) {
            throw new IllegalArgumentException("NIC number is required.");
        }
        if (employee.getDesignation() == null || employee.getDesignation().isBlank()) {
            throw new IllegalArgumentException("Designation is required.");
        }
        if (employee.getEmploymentType() == null || employee.getEmploymentType().isBlank()) {
            throw new IllegalArgumentException("Employment type is required.");
        }
    }

    // An employee counts as active if the status is empty or anything other than ARCHIVED
    private boolean isEmployeeActive(Employee employee) {
        return employee.getStatus() == null
                || !"ARCHIVED".equalsIgnoreCase(employee.getStatus());
    }


    // =====================================================
    // SHIFTS
    // =====================================================

    // All shifts, newest first (used for the shift table)
    public List<Shift> getAllShifts() {
        return shiftRepository.findAllByOrderByShiftIdDesc();
    }

    // Only shifts that are not archived (used for the attendance dropdown)
    public List<Shift> getActiveShifts() {
        return getAllShifts().stream()
                .filter(this::isShiftActive)
                .toList();
    }

    // Lookup table: shiftId -> Shift (so the HTML can show the shift name instead of a number)
    public Map<Long, Shift> getShiftMap() {
        return shiftRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Shift::getShiftId,                            // key = shift ID
                        Function.identity(),                          // value = the shift itself
                        (first, second) -> first,                     // on duplicate keys keep the first
                        LinkedHashMap::new                            // keep order
                ));
    }

    // Finds one shift by ID, or throws an error if it doesn't exist
    public Shift getShift(Long id) {
        return shiftRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Shift not found."));
    }

    @Transactional
    public Shift saveShift(Shift shift) {
        // Required fields
        if (shift.getShiftName() == null || shift.getShiftName().isBlank()) {
            throw new IllegalArgumentException("Shift name is required.");
        }
        if (shift.getStartTime() == null || shift.getEndTime() == null) {
            throw new IllegalArgumentException("Shift start time and end time are required.");
        }
        // A shift that starts and ends at the same time makes no sense
        if (shift.getStartTime().equals(shift.getEndTime())) {
            throw new IllegalArgumentException("Shift start time and end time cannot be the same.");
        }
        // Break cannot be negative
        if (shift.getBreakDuration() != null && shift.getBreakDuration() < 0) {
            throw new IllegalArgumentException("Break duration cannot be negative.");
        }
        // Default status
        if (shift.getStatus() == null || shift.getStatus().isBlank()) {
            shift.setStatus("ACTIVE");
        }
        return shiftRepository.save(shift);                           // INSERT or UPDATE
    }

    // Soft delete: keep the row, mark it ARCHIVED
    @Transactional
    public void archiveShift(Long id) {
        Shift shift = getShift(id);
        shift.setStatus("ARCHIVED");
        shiftRepository.save(shift);
    }

    // Bring an archived shift back to ACTIVE
    @Transactional
    public void restoreShift(Long id) {
        Shift shift = getShift(id);
        shift.setStatus("ACTIVE");
        shiftRepository.save(shift);
    }

    // Permanent delete, only if no attendance record uses this shift
    @Transactional
    public void deleteShift(Long id) {
        Shift shift = getShift(id);
        if (attendanceRecordRepository.existsByShiftId(id)) {
            throw new IllegalArgumentException(
                    "Cannot permanently delete this shift because attendance history already uses it. Archive the shift instead."
            );
        }
        shiftRepository.delete(shift);
    }

    // A shift is active if the status is empty or anything other than ARCHIVED
    private boolean isShiftActive(Shift shift) {
        return shift.getStatus() == null
                || !"ARCHIVED".equalsIgnoreCase(shift.getStatus());
    }

    // =====================================================
    // ATTENDANCE
    // =====================================================

    // All attendance records, newest date first
    public List<AttendanceRecord> getAllAttendanceRecords() {
        return attendanceRecordRepository.findAllByOrderByAttendanceDateDescAttendanceIdDesc();
    }

    // Finds one attendance record by ID, or throws an error
    public AttendanceRecord getAttendanceRecord(Long id) {
        return attendanceRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Attendance record not found."));
    }

    @Transactional
    public AttendanceRecord saveAttendanceRecord(AttendanceRecord record) {
        // 1) Required fields
        if (record.getEmployeeId() == null) {
            throw new IllegalArgumentException("Employee is required.");
        }
        if (record.getShiftId() == null) {
            throw new IllegalArgumentException("Shift is required.");
        }
        if (record.getAttendanceDate() == null) {
            throw new IllegalArgumentException("Attendance date is required.");
        }

        // 2) Load the employee and shift (throws "not found" if the ID is invalid)
        Employee employee = getEmployee(record.getEmployeeId());
        Shift shift = getShift(record.getShiftId());

        // 3) Archived employees and shifts cannot be used
        if (!isEmployeeActive(employee)) {
            throw new IllegalArgumentException("Archived employees cannot be used for attendance.");
        }
        if (!isShiftActive(shift)) {
            throw new IllegalArgumentException("Archived shifts cannot be used for attendance.");
        }

        // 4) Block a second record for the same employee on the same date
        boolean duplicate = getAllAttendanceRecords().stream()
                .anyMatch(existing ->
                        Objects.equals(existing.getEmployeeId(), record.getEmployeeId())          // same employee
                                && Objects.equals(existing.getAttendanceDate(), record.getAttendanceDate()) // same date
                                && !Objects.equals(existing.getAttendanceId(), record.getAttendanceId())    // not the record we are editing
                                && !"ARCHIVED".equalsIgnoreCase(existing.getStatus())                       // archived records don't count
                );
        if (duplicate) {
            throw new IllegalArgumentException(
                    "An attendance record already exists for this employee on the selected date."
            );
        }

        // 5) Check-out without check-in is invalid
        if (record.getCheckIn() == null && record.getCheckOut() != null) {
            throw new IllegalArgumentException("Check-in time is required before check-out time.");
        }

        // 6) Auto-calculate working hours (check-out - check-in - shift break)
        record.setWorkingHours(
                calculateWorkingHours(record.getCheckIn(), record.getCheckOut(), shift.getBreakDuration())
        );

        // 7) Auto-decide the status: PRESENT / LATE / INCOMPLETE / ABSENT
        record.setStatus(
                determineAttendanceStatus(record, shift)
        );

        return attendanceRecordRepository.save(record);
    }

    // Soft delete for attendance
    @Transactional
    public void archiveAttendanceRecord(Long id) {
        AttendanceRecord record = getAttendanceRecord(id);
        record.setStatus("ARCHIVED");
        attendanceRecordRepository.save(record);
    }

    // Restore: recalculate the real status instead of just setting ACTIVE
    @Transactional
    public void restoreAttendanceRecord(Long id) {
        AttendanceRecord record = getAttendanceRecord(id);
        Shift shift = getShift(record.getShiftId());
        record.setStatus(determineAttendanceStatus(record, shift));
        attendanceRecordRepository.save(record);
    }

    // Working hours = (checkOut - checkIn - break minutes) / 60, rounded to 2 decimals
    private BigDecimal calculateWorkingHours(
            LocalTime checkIn,
            LocalTime checkOut,
            Integer breakDurationMinutes) {

        // No check-in or no check-out yet: nothing to calculate
        if (checkIn == null || checkOut == null) {
            return null;
        }

        // Minutes between the two times
        long minutes = Duration.between(checkIn, checkOut).toMinutes();

        // If the result is negative, the shift passed midnight (22:00 -> 06:00), so add 24 hours
        if (minutes < 0) {
            minutes += 24L * 60L;
        }

        // Subtract the break (treat null as 0)
        int breakMinutes = breakDurationMinutes == null ? 0 : breakDurationMinutes;
        minutes = Math.max(0, minutes - breakMinutes);                // Math.max stops it going below 0

        // Convert minutes to hours: 510 minutes -> 8.50
        return BigDecimal.valueOf(minutes)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }

    // Decides the attendance status
    private String determineAttendanceStatus(AttendanceRecord record, Shift shift) {
        if (record.getCheckIn() == null) {
            return "ABSENT";                                          // no check-in = absent
        }
        if (record.getCheckOut() == null) {
            return "INCOMPLETE";                                      // came in but has not checked out
        }
        if (shift.getStartTime() != null
                && record.getCheckIn().isAfter(shift.getStartTime())) {
            return "LATE";                                            // arrived after the shift start time
        }
        return "PRESENT";                                             // on time and checked out
    }


    // =====================================================
    // LEAVE REQUESTS
    // =====================================================

    // All leave requests, newest first
    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRequestRepository.findAllByOrderByLeaveIdDesc();
    }

    // Finds one leave request by ID, or throws an error
    public LeaveRequest getLeaveRequest(Long id) {
        return leaveRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found."));
    }

    @Transactional
    public LeaveRequest saveLeaveRequest(LeaveRequest leaveRequest) {
        // 1) Required fields
        if (leaveRequest.getEmployeeId() == null) {
            throw new IllegalArgumentException("Employee is required.");
        }
        if (leaveRequest.getLeaveType() == null || leaveRequest.getLeaveType().isBlank()) {
            throw new IllegalArgumentException("Leave type is required.");
        }
        if (leaveRequest.getStartDate() == null || leaveRequest.getEndDate() == null) {
            throw new IllegalArgumentException("Leave start date and end date are required.");
        }
        // 2) End date cannot come before the start date
        if (leaveRequest.getEndDate().isBefore(leaveRequest.getStartDate())) {
            throw new IllegalArgumentException("Leave end date cannot be before the start date.");
        }
        if (leaveRequest.getReason() == null || leaveRequest.getReason().isBlank()) {
            throw new IllegalArgumentException("Leave reason is required.");
        }

        // 3) The employee must exist and must not be archived
        Employee employee = getEmployee(leaveRequest.getEmployeeId());
        if (!isEmployeeActive(employee)) {
            throw new IllegalArgumentException("Archived employees cannot create leave requests.");
        }

        // 4) New request vs editing an existing one
        if (leaveRequest.getLeaveId() == null) {
            // New request: always starts as PENDING with no approver
            leaveRequest.setStatus("PENDING");
            leaveRequest.setApprovedBy(null);
        } else {
            // Editing: only allowed while the saved request is still PENDING
            LeaveRequest existing = getLeaveRequest(leaveRequest.getLeaveId());
            if (!"PENDING".equalsIgnoreCase(existing.getStatus())) {
                throw new IllegalArgumentException(
                        "Approved or rejected leave requests are locked."
                );
            }
            leaveRequest.setStatus("PENDING");
            leaveRequest.setApprovedBy(null);
        }

        return leaveRequestRepository.save(leaveRequest);
    }

    // Approve: calls the shared decision method with "APPROVED"
    @Transactional
    public void approveLeave(Long id, Long approvedBy) {
        updateLeaveDecision(id, approvedBy, "APPROVED");
    }

    // Reject: calls the shared decision method with "REJECTED"
    @Transactional
    public void rejectLeave(Long id, Long approvedBy) {
        updateLeaveDecision(id, approvedBy, "REJECTED");
    }

    // Shared logic for approve and reject
    private void updateLeaveDecision(Long id, Long approvedBy, String decision) {
        // An approver must be chosen
        if (approvedBy == null) {
            throw new IllegalArgumentException("Approver is required.");
        }
        // The approver must exist and must not be archived
        Employee approver = getEmployee(approvedBy);
        if (!isEmployeeActive(approver)) {
            throw new IllegalArgumentException("Archived employees cannot approve leave requests.");
        }

        LeaveRequest leaveRequest = getLeaveRequest(id);

        // Only PENDING requests can be decided
        if (!"PENDING".equalsIgnoreCase(leaveRequest.getStatus())) {
            throw new IllegalArgumentException("Only pending leave requests can be approved or rejected.");
        }

        leaveRequest.setApprovedBy(approvedBy);                       // record who decided
        leaveRequest.setStatus(decision);                             // APPROVED or REJECTED
        leaveRequestRepository.save(leaveRequest);
    }

    // Soft delete for leave requests
    @Transactional
    public void archiveLeaveRequest(Long id) {
        LeaveRequest leaveRequest = getLeaveRequest(id);
        leaveRequest.setStatus("ARCHIVED");
        leaveRequestRepository.save(leaveRequest);
    }

    // Restore goes back to PENDING and clears the approver
    @Transactional
    public void restoreLeaveRequest(Long id) {
        LeaveRequest leaveRequest = getLeaveRequest(id);
        leaveRequest.setStatus("PENDING");
        leaveRequest.setApprovedBy(null);
        leaveRequestRepository.save(leaveRequest);
    }

    // Permanent delete: only PENDING or ARCHIVED requests. APPROVED/REJECTED are audit records.
    @Transactional
    public void deleteLeaveRequest(Long id) {
        LeaveRequest leaveRequest = getLeaveRequest(id);
        if (!"PENDING".equalsIgnoreCase(leaveRequest.getStatus())
                && !"ARCHIVED".equalsIgnoreCase(leaveRequest.getStatus())) {
            throw new IllegalArgumentException(
                    "Approved or rejected leave requests are audit records and cannot be permanently deleted."
            );
        }
        leaveRequestRepository.delete(leaveRequest);
    }
}   // <-- closing brace of the HrService class