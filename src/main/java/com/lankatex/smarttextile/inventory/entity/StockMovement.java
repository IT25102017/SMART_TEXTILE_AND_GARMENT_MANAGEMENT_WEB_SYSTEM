package com.lankatex.smarttextile.inventory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movements")
@Getter
@Setter
@NoArgsConstructor
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movement_id")
    private Long movementId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "batch_id")
    private MaterialBatch batch;

    @NotNull(message = "Movement type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false)
    private MovementType movementType;

    @NotNull
    @DecimalMin(value = "0.01", message = "Quantity must be greater than zero")
    @Column(name = "quantity", nullable = false)
    private BigDecimal quantity;

    @Column(name = "reference_type")
    private String referenceType;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "movement_date", nullable = false)
    private LocalDateTime movementDate;

    /*
     * Shared users module will later provide the real user relationship.
     */
    @Column(name = "created_by")
    private Long createdBy;
}