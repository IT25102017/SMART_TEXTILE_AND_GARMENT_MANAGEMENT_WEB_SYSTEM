package com.lankatex.smarttextile.production.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

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

    @NotNull(message = "Production Plan is required")
    @Column(name = "plan_id", nullable = false)
    private Long planId;

    @NotNull(message = "Requested By User ID is required")
    @Column(name = "requested_by", nullable = false)
    private Long requestedBy;

    @NotNull(message = "Request date is required")
    @Column(name = "request_date", nullable = false)
    private LocalDate requestDate;

    @Column(name = "status")
    private String status = "PENDING";

    @Column(name = "approved_by")
    private Long approvedBy;

    @Transient
    public String getRequestCode() {
        if (materialRequestId == null) {
            return "MR-NEW";
        }
        return String.format("MR-%03d", materialRequestId);
    }

    @PrePersist
    @PreUpdate
    private void normalize() {
        if (status == null || status.isBlank()) {
            status = "PENDING";
        }
        status = status.toUpperCase();
    }
}