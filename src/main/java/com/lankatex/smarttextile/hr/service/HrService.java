package com.lankatex.smarttextile.hr.service;

import com.lankatex.smarttextile.hr.dto.HrDashboardStats;
import com.lankatex.smarttextile.hr.entity.AttendanceRecord;
import com.lankatex.smarttextile.hr.entity.Employee;
import com.lankatex.smarttextile.hr.entity.LeaveRequest;
import com.lankatex.smarttextile.hr.entity.Shift;
import com.lankatex.smarttextile.hr.repository.AttendanceRecordRepository;
import com.lankatex.smarttextile.hr.repository.EmployeeRepository;
import com.lankatex.smarttextile.hr.repository.LeaveRequestRepository;
import com.lankatex.smarttextile.hr.repository.ShiftRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class HrService {

    private final EmployeeRepository employeeRepository;
    private final ShiftRepository shiftRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    public HrService(
            EmployeeRepository employeeRepository,
            ShiftRepository shiftRepository,
            AttendanceRecordRepository attendanceRecordRepository,
            LeaveRequestRepository leaveRequestRepository) {

        this.employeeRepository = employeeRepository;
        this.shiftRepository = shiftRepository;
        this.attendanceRecordRepository = attendanceRecordRepository;
        this.leaveRequestRepository = leaveRequestRepository;
    }


    // Dashboard statistics
    public HrDashboardStats getDashboardStats() {

        return new HrDashboardStats(
                employeeRepository.count(),
                shiftRepository.count(),
                attendanceRecordRepository.count(),
                leaveRequestRepository.count()
        );
    }


    // =========================================================
    // EMPLOYEE MANAGEMENT
    // =========================================================

    public List<Employee> getAllEmployees() {

        return employeeRepository.findAll(
                Sort.by(Sort.Direction.DESC, "employeeId")
        );
    }


    public Employee getEmployee(Long id) {

        return employeeRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Employee not found: " + id
                        )
                );
    }


    public Employee saveEmployee(Employee employee) {

        if (employee.getEmployeeCode() == null
                || employee.getEmployeeCode().isBlank()) {

            throw new IllegalArgumentException(
                    "Employee code is required"
            );
        }

        if (employee.getStatus() == null
                || employee.getStatus().isBlank()) {

            employee.setStatus("Active");
        }

        return employeeRepository.save(employee);
    }


    public void archiveEmployee(Long id) {

        Employee employee = getEmployee(id);

        employee.setStatus("Archived");

        employeeRepository.save(employee);
    }


    public void restoreEmployee(Long id) {

        Employee employee = getEmployee(id);

        employee.setStatus("Active");

        employeeRepository.save(employee);
    }


    public void deleteEmployee(Long id) {

        Employee employee = getEmployee(id);

        employeeRepository.delete(employee);
    }


    // =========================================================
    // SHIFT MANAGEMENT
    // =========================================================

    public List<Shift> getAllShifts() {

        return shiftRepository.findAll(
                Sort.by(Sort.Direction.DESC, "shiftId")
        );
    }


    public Shift getShift(Long id) {

        return shiftRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Shift not found: " + id
                        )
                );
    }


    public Shift saveShift(Shift shift) {

        if (shift.getShiftName() == null
                || shift.getShiftName().isBlank()) {

            throw new IllegalArgumentException(
                    "Shift name is required"
            );
        }

        if (shift.getStatus() == null
                || shift.getStatus().isBlank()) {

            shift.setStatus("Active");
        }

        return shiftRepository.save(shift);
    }


    public void archiveShift(Long id) {

        Shift shift = getShift(id);

        shift.setStatus("Archived");

        shiftRepository.save(shift);
    }


    public void restoreShift(Long id) {

        Shift shift = getShift(id);

        shift.setStatus("Active");

        shiftRepository.save(shift);
    }


    public void deleteShift(Long id) {

        Shift shift = getShift(id);

        shiftRepository.delete(shift);
    }


    // =========================================================
    // ATTENDANCE MANAGEMENT
    // =========================================================

    public List<AttendanceRecord> getAllAttendanceRecords() {

        return attendanceRecordRepository.findAll(
                Sort.by(Sort.Direction.DESC, "attendanceId")
        );
    }


    public AttendanceRecord getAttendanceRecord(Long id) {

        return attendanceRecordRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Attendance Record not found: " + id
                        )
                );
    }


    public AttendanceRecord saveAttendanceRecord(
            AttendanceRecord attendanceRecord) {

        if (attendanceRecord.getEmployeeId() == null) {

            throw new IllegalArgumentException(
                    "Employee ID is required"
            );
        }

        if (attendanceRecord.getStatus() == null
                || attendanceRecord.getStatus().isBlank()) {

            attendanceRecord.setStatus("Pending");
        }

        return attendanceRecordRepository.save(attendanceRecord);
    }


    public void archiveAttendanceRecord(Long id) {

        AttendanceRecord attendanceRecord =
                getAttendanceRecord(id);

        attendanceRecord.setStatus("Archived");

        attendanceRecordRepository.save(attendanceRecord);
    }


    public void restoreAttendanceRecord(Long id) {

        AttendanceRecord attendanceRecord =
                getAttendanceRecord(id);

        attendanceRecord.setStatus("Pending");

        attendanceRecordRepository.save(attendanceRecord);
    }


    public void deleteAttendanceRecord(Long id) {

        AttendanceRecord attendanceRecord =
                getAttendanceRecord(id);

        attendanceRecordRepository.delete(attendanceRecord);
    }


    // =========================================================
    // LEAVE REQUEST MANAGEMENT
    // =========================================================

    public List<LeaveRequest> getAllLeaveRequests() {

        return leaveRequestRepository.findAll(
                Sort.by(Sort.Direction.DESC, "leaveId")
        );
    }


    public LeaveRequest getLeaveRequest(Long id) {

        return leaveRequestRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Leave Request not found: " + id
                        )
                );
    }


    public LeaveRequest saveLeaveRequest(
            LeaveRequest leaveRequest) {

        if (leaveRequest.getEmployeeId() == null) {

            throw new IllegalArgumentException(
                    "Employee ID is required"
            );
        }

        if (leaveRequest.getStatus() == null
                || leaveRequest.getStatus().isBlank()) {

            leaveRequest.setStatus("Pending");
        }

        return leaveRequestRepository.save(leaveRequest);
    }


    public void archiveLeaveRequest(Long id) {

        LeaveRequest leaveRequest =
                getLeaveRequest(id);

        leaveRequest.setStatus("Archived");

        leaveRequestRepository.save(leaveRequest);
    }


    public void restoreLeaveRequest(Long id) {

        LeaveRequest leaveRequest =
                getLeaveRequest(id);

        leaveRequest.setStatus("Pending");

        leaveRequestRepository.save(leaveRequest);
    }


    public void deleteLeaveRequest(Long id) {

        LeaveRequest leaveRequest =
                getLeaveRequest(id);

        leaveRequestRepository.delete(leaveRequest);
    }
}