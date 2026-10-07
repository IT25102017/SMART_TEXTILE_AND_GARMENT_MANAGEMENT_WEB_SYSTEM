package com.lankatex.smarttextile.customer.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entity representing QUOTATION_ITEM.
 * Schema: QUOTATION_ITEM(QuotationItemID PK, QuotationID FK -> QUOTATION.QuotationID, GarmentType, Specifications, Colour, Size, Quantity, UnitPrice)
 */
@Entity
@Table(name = "quotation_items")
@Getter
@Setter
@NoArgsConstructor
public class QuotationItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quotation_item_id")
    private Long quotationItemId;

    @NotNull(message = "Quotation ID is required")
    @Column(name = "quotation_id", nullable = false)
    private Long quotationId;

    @NotBlank(message = "Garment type is required")
    @Column(name = "garment_type", nullable = false)
    private String garmentType;

    @Column(name = "specifications", length = 1000)
    private String specifications;

    @Column(name = "colour")
    private String colour;

    @Column(name = "size")
    private String size;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.01", message = "Quantity must be greater than zero")
    @Column(name = "quantity", nullable = false)
    private BigDecimal quantity;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", message = "Unit price cannot be negative")
    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Transient
    public BigDecimal getItemTotal() {
        if (quantity != null && unitPrice != null) {
            return quantity.multiply(unitPrice);
        }
        return BigDecimal.ZERO;
    }
}
