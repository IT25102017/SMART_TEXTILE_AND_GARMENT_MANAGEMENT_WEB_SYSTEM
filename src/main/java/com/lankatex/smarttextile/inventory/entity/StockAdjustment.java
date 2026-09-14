package com.lankatex.smarttextile.inventory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "stock_adjustments")
@Getter
@Setter
@NoArgsConstructor
public class StockAdjustment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "adjustment_id")
    private Long adjustmentId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "batch_id", nullable = false)
    private MaterialBatch batch;

    @NotNull
    @DecimalMin("0.0")
    @Column(name = "previous_qty", nullable = false)
    private BigDecimal previousQty;

    @NotNull
    @DecimalMin(value = "0.0", message = "Adjusted quantity cannot be negative")
    @Column(name = "adjusted_qty", nullable = false)
    private BigDecimal adjustedQty;

    @NotBlank(message = "Reason is required")
    @Column(name = "reason", nullable = false)
    private String reason;

    @Column(name = "approval_status", nullable = false)
    private String approvalStatus = "PENDING";

    @Column(name = "approved_by")
    private Long approvedBy;
}