package com.lankatex.smarttextile.production.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "production_progress")
@Getter
@Setter
@NoArgsConstructor
public class ProductionProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_id")
    private Long progressId;

    @NotNull(message = "Production Plan is required")
    @Column(name = "plan_id", nullable = false)
    private Long planId;

    @NotNull(message = "Progress date is required")
    @Column(name = "progress_date", nullable = false)
    private LocalDate progressDate;

    @NotNull(message = "Completed quantity is required")
    @DecimalMin(value = "0.0", message = "Completed quantity cannot be negative")
    @Column(name = "completed_qty", nullable = false)
    private BigDecimal completedQty = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Rejected quantity cannot be negative")
    @Column(name = "rejected_qty")
    private BigDecimal rejectedQty = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Reworked quantity cannot be negative")
    @Column(name = "reworked_qty")
    private BigDecimal reworkedQty = BigDecimal.ZERO;

    @NotNull
    @DecimalMin(value = "0.0", message = "Remaining quantity cannot be negative")
    @Column(name = "remaining_qty", nullable = false)
    private BigDecimal remainingQty = BigDecimal.ZERO;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Transient
    public String getProgressCode() {
        if (progressId == null) {
            return "PRG-NEW";
        }
        return String.format("PRG-%03d", progressId);
    }

    @PrePersist
    @PreUpdate
    private void normalizeQuantities() {
        if (completedQty == null) completedQty = BigDecimal.ZERO;
        if (rejectedQty == null) rejectedQty = BigDecimal.ZERO;
        if (reworkedQty == null) reworkedQty = BigDecimal.ZERO;
        if (remainingQty == null) remainingQty = BigDecimal.ZERO;
    }
}