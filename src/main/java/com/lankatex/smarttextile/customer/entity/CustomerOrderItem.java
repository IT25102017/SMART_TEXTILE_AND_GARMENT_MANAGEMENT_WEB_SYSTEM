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
 * Entity representing CUSTOMER_ORDER_ITEM.
 * Schema: CUSTOMER_ORDER_ITEM(CustomerOrderItemID PK, CustomerOrderID FK -> CUSTOMER_ORDER.CustomerOrderID, GarmentType, Colour, Size, Quantity, UnitPrice)
 */
@Entity
@Table(name = "customer_order_items")
@Getter
@Setter
@NoArgsConstructor
public class CustomerOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_order_item_id")
    private Long customerOrderItemId;

    @NotNull(message = "Customer Order ID is required")
    @Column(name = "customer_order_id", nullable = false)
    private Long customerOrderId;

    @NotBlank(message = "Garment type is required")
    @Column(name = "garment_type", nullable = false)
    private String garmentType;

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
