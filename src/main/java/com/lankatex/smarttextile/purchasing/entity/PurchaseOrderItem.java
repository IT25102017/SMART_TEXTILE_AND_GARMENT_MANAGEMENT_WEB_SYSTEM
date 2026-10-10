package com.lankatex.smarttextile.purchasing.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "purchase_order_items")
@Getter
@Setter
public class PurchaseOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "po_item_id")
    private Long poItemId;

    @Column(name = "po_id", nullable = false)
    private Long poId;

    @Column(name = "material_id", nullable = false)
    private Long materialId;

    @Column(name = "quantity", nullable = false)
    private BigDecimal quantity;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "received_qty")
    private BigDecimal receivedQty;

    // User-friendly display code.
    // This value is calculated from poItemId and is not stored in the database.
    @Transient
    public String getPoItemCode() {

        if (poItemId == null) {
            return "POI-NEW";
        }

        return String.format(
                "POI-%03d",
                poItemId
        );
    }
}