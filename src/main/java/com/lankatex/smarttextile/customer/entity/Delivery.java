package com.lankatex.smarttextile.customer.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "deliveries")
@Getter
@Setter
@NoArgsConstructor
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Long deliveryId;

    @NotNull(message = "Order ID is required")
    @Positive(message = "Order ID must be a valid positive number")
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @NotNull(message = "Delivery date is required")
    @Column(name = "delivery_date", nullable = false)
    private LocalDate deliveryDate;

    @NotBlank(message = "Delivery address is required")
    @Size(min = 5, max = 1000, message = "Delivery address must be between 5 and 1000 characters")
    @Column(name = "delivery_address", nullable = false, length = 1000)
    private String deliveryAddress;

    @NotNull(message = "Delivered quantity is required")
    @DecimalMin(value = "0.01", message = "Delivered quantity must be greater than 0")
    @Column(name = "delivered_qty", nullable = false)
    private BigDecimal deliveredQty;

    @Size(max = 100, message = "Receiver name cannot exceed 100 characters")
    @Column(name = "receiver_name")
    private String receiverName;

    @Column(name = "status")
    private String status;

    @Size(max = 1000, message = "Remarks cannot exceed 1000 characters")
    @Column(name = "remarks", length = 1000)
    private String remarks;
}
