package com.lankatex.smarttextile.production.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    @Column(name = "plan_id", nullable = false)

    private Long planId;
    @Column(name = "progress_date", nullable = false)
    private LocalDate progressDate;
    @Column(name = "completed_qty", nullable = false)
    private BigDecimal completedQty;
    @Column(name = "rejected_qty")
    private BigDecimal rejectedQty;
    @Column(name = "reworked_qty")
    private BigDecimal reworkedQty;
    @Column(name = "remaining_qty", nullable = false)
    private BigDecimal remainingQty;
    @Column(name = "updated_by")
    private Long updatedBy;
}
