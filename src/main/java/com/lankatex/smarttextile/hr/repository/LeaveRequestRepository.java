package com.lankatex.smarttextile.hr.repository;

import com.lankatex.smarttextile.hr.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    // All leave requests, newest first (ORDER BY leaveId DESC)
    List<LeaveRequest> findAllByOrderByLeaveIdDesc();

    // true if this employee has any leave request (used to block deleting an employee with history)
    boolean existsByEmployeeId(Long employeeId);
}