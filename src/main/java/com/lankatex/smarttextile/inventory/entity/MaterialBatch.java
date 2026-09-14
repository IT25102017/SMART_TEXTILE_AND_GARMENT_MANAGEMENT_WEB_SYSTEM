package com.lankatex.smarttextile.inventory.entity;

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
@Table(name = "material_batches")
@Getter
@Setter
@NoArgsConstructor
public class MaterialBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_id")
    private Long batchId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    /*
     * Supplier module is developed by another member.
     * For now we store the shared supplier ID.
     */
    @Column(name = "supplier_id")
    private Long supplierId;

    @NotBlank(message = "Batch number is required")
    @Column(name = "batch_no", nullable = false)
    private String batchNo;

    @NotNull(message = "Received date is required")
    @Column(name = "received_date", nullable = false)
    private LocalDate receivedDate;

    @DecimalMin(value = "0.0", message = "Unit cost cannot be negative")
    @Column(name = "unit_cost")
    private BigDecimal unitCost = BigDecimal.ZERO;

    @NotNull
    @DecimalMin(value = "0.0", message = "Available quantity cannot be negative")
    @Column(name = "available_qty", nullable = false)
    private BigDecimal availableQty = BigDecimal.ZERO;

    @Column(name = "location")
    private String location;
}