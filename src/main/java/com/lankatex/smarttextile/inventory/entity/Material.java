package com.lankatex.smarttextile.inventory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(
        name = "materials",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "material_code")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "material_id")
    private Long materialId;

    @NotBlank(message = "Material code is required")
    @Column(name = "material_code", nullable = false)
    private String materialCode;

    @NotBlank(message = "Material name is required")
    @Column(name = "material_name", nullable = false)
    private String materialName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private MaterialCategory category;

    @Column(name = "colour")
    private String colour;

    @NotBlank(message = "Unit is required")
    @Column(name = "unit", nullable = false)
    private String unit;

    @DecimalMin(value = "0.0", message = "Reorder level cannot be negative")
    @Column(name = "reorder_level", nullable = false)
    private BigDecimal reorderLevel = BigDecimal.ZERO;

    @Column(name = "status", nullable = false)
    private String status = "ACTIVE";

    /*
     * This value is not stored as a column.
     * It is calculated from material_batches.available_qty.
     */
    @Transient
    private BigDecimal currentQuantity = BigDecimal.ZERO;
}