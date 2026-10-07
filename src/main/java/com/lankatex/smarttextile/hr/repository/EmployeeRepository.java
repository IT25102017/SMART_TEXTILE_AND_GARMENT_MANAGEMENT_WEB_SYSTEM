package com.lankatex.smarttextile.hr.repository;

import com.lankatex.smarttextile.hr.entity.Employee;               // The entity this repository manages
import org.springframework.data.jpa.repository.JpaRepository;      // Gives ready-made methods: save, findById, findAll, delete...

import java.util.List;                                             // For methods that return many rows
import java.util.Optional;                                         // A box that may or may not contain a value (avoids null)

// JpaRepository<Employee, Long> = manages Employee entities whose primary key type is Long
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // Name is parsed by Spring: find all, ORDER BY employeeId DESC (newest employee first)
    List<Employee> findAllByOrderByEmployeeIdDesc();

    // WHERE employee_code = ? ignoring upper/lower case. Used to block duplicate codes.
    Optional<Employee> findByEmployeeCodeIgnoreCase(String employeeCode);

    // WHERE nic_no = ? ignoring case. Used to block duplicate NICs.
    Optional<Employee> findByNicNoIgnoreCase(String nicNo);
}