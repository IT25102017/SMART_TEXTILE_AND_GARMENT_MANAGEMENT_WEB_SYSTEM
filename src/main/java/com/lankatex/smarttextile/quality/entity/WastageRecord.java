package com.lankatex.smarttextile.quality.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    private String approvalStatus;
    @Column(name = "recorded_by")
    private Long recordedBy;
}