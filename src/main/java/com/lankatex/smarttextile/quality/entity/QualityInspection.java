package com.lankatex.smarttextile.quality.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "quality_inspections")
@Getter
@Setter
@NoArgsConstructor
public class QualityInspection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inspection_id")
    private Long inspectionId;

    @Column(name = "plan_id", nullable = false)
    private Long planId;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "stage_name", nullable = false)
    private String stageName;

    @Column(name = "inspection_date", nullable = false)
    private LocalDate inspectionDate;

    @Column(name = "inspector_id")
    private Long inspectorId;

    @Column(name = "inspected_qty")
    private BigDecimal inspectedQty = BigDecimal.ZERO;

    @Column(name = "accepted_qty")
    private BigDecimal acceptedQty = BigDecimal.ZERO;

    @Column(name = "rejected_qty")
    private BigDecimal rejectedQty = BigDecimal.ZERO;

    @Column(name = "pending_qty")
    private BigDecimal pendingQty = BigDecimal.ZERO;

    @Column(name = "status")
    private String status = "PENDING";

    @PrePersist
    @PreUpdate
    @PostLoad
    private void normalizeValues() {
        if (inspectedQty == null) inspectedQty = BigDecimal.ZERO;
        if (acceptedQty == null) acceptedQty = BigDecimal.ZERO;
        if (rejectedQty == null) rejectedQty = BigDecimal.ZERO;
        if (pendingQty == null) pendingQty = BigDecimal.ZERO;
        if (status == null || status.isBlank()) status = "PENDING";
    }

    @Transient
    public String getInspectionCode() {
        return inspectionId == null
                ? "QIN-NEW"
                : String.format("QIN-%03d", inspectionId);
    }
}