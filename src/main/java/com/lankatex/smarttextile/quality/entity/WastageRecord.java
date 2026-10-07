package com.lankatex.smarttextile.quality.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "wastage_records")
@Getter
@Setter
@NoArgsConstructor
public class WastageRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wastage_id")
    private Long wastageId;

    @Column(name = "plan_id")
    private Long planId;

    @Column(name = "material_id")
    private Long materialId;

    @Column(name = "quantity", nullable = false)
    private BigDecimal quantity;

    @Column(name = "unit", nullable = false)
    private String unit;

    @Column(name = "reason", nullable = false, length = 1000)
    private String reason;

    @Column(name = "approval_status")
    private String approvalStatus = "PENDING";

    @Column(name = "recorded_by")
    private Long recordedBy;

    @PrePersist
    @PreUpdate
    @PostLoad
    private void normalizeStatus() {
        if (approvalStatus == null || approvalStatus.isBlank()) {
            approvalStatus = "PENDING";
        }
    }

    @Transient
    public String getWastageCode() {
        return wastageId == null
                ? "WST-NEW"
                : String.format("WST-%03d", wastageId);
    }
}