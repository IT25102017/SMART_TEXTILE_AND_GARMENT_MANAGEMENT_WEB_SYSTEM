package com.lankatex.smarttextile.purchasing.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "purchase_requests")
@Getter
@Setter
public class PurchaseRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long requestId;

    @Column(name = "request_date", nullable = false)
    private LocalDate requestDate;

    @Column(name = "requested_by", nullable = false)
    private Long requestedBy;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "reason", nullable = false, length = 1000)
    private String reason;

    @Column(name = "status")
    private String status;

    // User-friendly display code.
    // This value is calculated from requestId and is not stored in the database.
    @Transient
    public String getRequestCode() {

        if (requestId == null) {
            return "PR-NEW";
        }

        return String.format(
                "PR-%03d",
                requestId
        );
    }
}