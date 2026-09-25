package com.lankatex.smarttextile.production.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "production_plans")
@Getter
@Setter
@NoArgsConstructor
public class ProductionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Long planId;

    @NotNull(message = "Customer Order is required")
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @NotNull(message = "Planned quantity is required")
    @DecimalMin(value = "0.01", message = "Planned quantity must be greater than zero")
    @Column(name = "planned_qty", nullable = false)
    private BigDecimal plannedQty;

    @NotNull(message = "Start date is required")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @NotBlank(message = "Production line is required")
    @Column(name = "production_line", nullable = false)
    private String productionLine;

    @Column(name = "supervisor_id")
    private Long supervisorId;

    @Column(name = "status")
    private String status = "PENDING";

    @Transient
    public String getPlanCode() {
        if (planId == null) {
            return "PP-NEW";
        }
        return String.format("PP-%03d", planId);
    }

    @PrePersist
    @PreUpdate
    private void normalize() {
        if (productionLine != null) {
            productionLine = productionLine.trim();
        }
        if (status == null || status.isBlank()) {
            status = "PENDING";
        }
        status = status.toUpperCase();
    }
}