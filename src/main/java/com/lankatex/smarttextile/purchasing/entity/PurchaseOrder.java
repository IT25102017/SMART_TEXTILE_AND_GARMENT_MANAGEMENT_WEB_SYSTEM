package com.lankatex.smarttextile.purchasing.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "purchase_orders")
@Getter
@Setter
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "po_id")
    private Long poId;

    @Column(name = "supplier_id", nullable = false)
    private Long supplierId;

    @Column(name = "request_id")
    private Long requestId;

    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    @Column(name = "expected_delivery_date", nullable = false)
    private LocalDate expectedDeliveryDate;

    @Column(name = "status")
    private String status;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "approved_by")
    private Long approvedBy;

    // User-friendly display code.
    // This value is calculated from poId and is not stored in the database.
    @Transient
    public String getPoCode() {

        if (poId == null) {
            return "PO-NEW";
        }

        return String.format(
                "PO-%03d",
                poId
        );
    }
}