package com.lankatex.smarttextile.inventory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "goods_receipts")
@Getter
@Setter
@NoArgsConstructor
public class GoodsReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receipt_id")
    private Long receiptId;

    /*
     * Purchase Order belongs to Purchasing module.
     */
    @Column(name = "po_id")
    private Long poId;

    @Column(name = "supplier_id")
    private Long supplierId;

    @NotNull(message = "Received date is required")
    @Column(name = "received_date", nullable = false)
    private LocalDate receivedDate;

    @Column(name = "received_by")
    private Long receivedBy;

    @Column(name = "status", nullable = false)
    private String status = "RECEIVED";

    @Column(name = "remarks", length = 500)
    private String remarks;
}