package com.lankatex.smarttextile.hr.repository;

import com.lankatex.smarttextile.hr.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}