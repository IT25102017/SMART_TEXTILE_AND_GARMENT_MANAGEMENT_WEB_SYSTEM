package com.lankatex.smarttextile.customer.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entity representing DELIVERY_ITEM.
 * Schema: DELIVERY_ITEM(DeliveryItemID PK, DeliveryID FK -> DELIVERY.DeliveryID, CustomerOrderItemID FK -> CUSTOMER_ORDER_ITEM.CustomerOrderItemID, QuantityDelivered)
 */
@Entity
@Table(name = "delivery_items")
@Getter
@Setter
@NoArgsConstructor
public class DeliveryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_item_id")
    private Long deliveryItemId;

    @NotNull(message = "Delivery ID is required")
    @Column(name = "delivery_id", nullable = false)
    private Long deliveryId;

    @NotNull(message = "Customer Order Item ID is required")
    @Column(name = "customer_order_item_id", nullable = false)
    private Long customerOrderItemId;

    @NotNull(message = "Delivered quantity is required")
    @DecimalMin(value = "0.01", message = "Delivered quantity must be greater than zero")
    @Column(name = "quantity_delivered", nullable = false)
    private BigDecimal quantityDelivered;

    public DeliveryItem(Long deliveryId, Long customerOrderItemId, BigDecimal quantityDelivered) {
        this.deliveryId = deliveryId;
        this.customerOrderItemId = customerOrderItemId;
        this.quantityDelivered = quantityDelivered;
    }
}
