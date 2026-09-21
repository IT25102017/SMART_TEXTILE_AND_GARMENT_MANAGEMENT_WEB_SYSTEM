package com.lankatex.smarttextile.customer.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "status")
    private String status;

    @Positive(message = "Approved by User ID must be a positive number")
    @Column(name = "approved_by")
    private Long approvedBy;
}
