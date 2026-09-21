package com.lankatex.smarttextile.customer.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "quotations")
@Getter
@Setter
@NoArgsConstructor
public class Quotation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quotation_id")
    private Long quotationId;

    @NotNull(message = "Customer ID is required")
    @Positive(message = "Customer ID must be a valid positive number")
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @NotNull(message = "Quotation date is required")
    @Column(name = "quotation_date", nullable = false)
    private LocalDate quotationDate;

    @NotNull(message = "Valid until date is required")
    @Column(name = "valid_until", nullable = false)
    private LocalDate validUntil;

    @Column(name = "status")
    private String status;

    @Positive(message = "Approved by User ID must be a positive number")
    @Column(name = "approved_by")
    private Long approvedBy;

    @NotNull(message = "Total value is required")
    @DecimalMin(value = "0.01", message = "Total value must be greater than 0.00")
    @Column(name = "total_value", nullable = false)
    private BigDecimal totalValue;
}
