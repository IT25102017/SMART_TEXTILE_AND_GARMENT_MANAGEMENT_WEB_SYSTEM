package com.lankatex.smarttextile.customer.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "customer_orders")
@Getter
@Setter
@NoArgsConstructor
public class CustomerOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @NotNull(message = "Customer ID is required")
    @Positive(message = "Customer ID must be a valid positive number")
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Positive(message = "Quotation ID must be a valid positive number")
    @Column(name = "quotation_id")
    private Long quotationId;

    @NotNull(message = "Order date is required")
    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    @NotNull(message = "Required delivery date is required")
    @Column(name = "required_delivery_date", nullable = false)
    private LocalDate requiredDeliveryDate;

    @NotBlank(message = "Priority is required")
    @Column(name = "priority", nullable = false)
    private String priority;

    /// new
    @Column(name = "garment_type")
    private String garmentType;

    @Column(name = "colour")
    private String colour;

    @Column(name = "size")
    private String size;

    @NotNull(message = "Order quantity is required")
    @DecimalMin(value = "0.01", message = "Order quantity must be greater than zero")
    @Column(name = "order_qty", nullable = true)
    private BigDecimal orderQty;

    @Column(name = "status")
    private String status = "PENDING";

    @Positive(message = "Approved by User ID must be a positive number")
    @Column(name = "approved_by")
    private Long approvedBy;

    /// new
    @Transient
    public String getOrderCode() {
        return orderId == null ? "ORD-NEW" : String.format("ORD-%03d", orderId);
    }
}
