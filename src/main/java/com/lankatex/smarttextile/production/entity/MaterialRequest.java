package com.lankatex.smarttextile.production.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Table(name = "material_requests")
@Getter
@Setter
@NoArgsConstructor
public class MaterialRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "material_request_id")
    private Long materialRequestId;
    @Column(name = "plan_id", nullable = false)
    private Long planId;
    @Column(name = "requested_by", nullable = false)
    private Long requestedBy;
    @Column(name = "request_date", nullable = false)
    private LocalDate requestDate;
    @Column(name = "status")
    private String status;
    @Column(name = "approved_by")
    private Long approvedBy;
}


