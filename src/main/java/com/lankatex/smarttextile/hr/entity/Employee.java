package com.lankatex.smarttextile.hr.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "employee_code", nullable = false)
    private String employeeCode;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "nic_no", nullable = false)
    private String nicNo;

    @Column(name = "department_id")
    private Long departmentId;

    @Column(name = "designation", nullable = false)
    private String designation;

    @Column(name = "employment_type", nullable = false)
    private String employmentType;

    @Column(name = "status")
    private String status;
}