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

    /// new
    @NotNull(message = "Requested delivery date is required")
    @Column(name = "requested_delivery_date", nullable = true)
    private LocalDate requestedDeliveryDate;

    /// Line items (persisted in quotation_items table, transient here for form binding)
    @Transient
    private String garmentType;

    @Transient
    private String colour;

    @Transient
    private String size;

    @Transient
    private BigDecimal quantity;

    @Transient
    private BigDecimal unitPrice;

    @Transient
    private BigDecimal totalValue = BigDecimal.ZERO;

    @Column(name = "status")
    private String status = "PENDING";

    @Column(name = "approved_by")
    private Long approvedBy;

    public void calculateTotal() {
        if (quantity != null && unitPrice != null) {
            totalValue = quantity.multiply(unitPrice);
        }
    }

    public LocalDate getValidityPeriod() {
        return validUntil;
    }

    public void setValidityPeriod(LocalDate validityPeriod) {
        this.validUntil = validityPeriod;
    }

    public LocalDate getDeliveryPeriod() {
        return requestedDeliveryDate;
    }

    public void setDeliveryPeriod(LocalDate deliveryPeriod) {
        this.requestedDeliveryDate = deliveryPeriod;
    }

    @Transient
    public String getQuotationCode() {
        return quotationId == null ? "QTN-NEW" : String.format("QTN-%03d", quotationId);
    }
}
