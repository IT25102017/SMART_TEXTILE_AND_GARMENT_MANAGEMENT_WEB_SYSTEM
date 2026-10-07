package com.lankatex.smarttextile.hr.repository;

import com.lankatex.smarttextile.hr.entity.Shift;                  // The entity this repository manages
import org.springframework.data.jpa.repository.JpaRepository;      // Ready-made CRUD methods

import java.util.List;

public interface ShiftRepository extends JpaRepository<Shift, Long> {

    // All shifts, newest first (ORDER BY shiftId DESC)
    List<Shift> findAllByOrderByShiftIdDesc();
}