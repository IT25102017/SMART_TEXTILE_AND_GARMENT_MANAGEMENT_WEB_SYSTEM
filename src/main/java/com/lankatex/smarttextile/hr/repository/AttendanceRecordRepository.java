package com.lankatex.smarttextile.hr.repository;

import com.lankatex.smarttextile.hr.entity.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {
}