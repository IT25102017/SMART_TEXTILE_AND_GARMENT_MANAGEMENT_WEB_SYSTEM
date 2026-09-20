package com.lankatex.smarttextile.purchasing.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "purchase_requests")
@Getter
@Setter
@NoArgsConstructor
public class PurchaseRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long requestId;

    @Column(name = "requested_by", nullable = false)
    private Long requestedBy;

    @Column(name = "request_date", nullable = false)
    private LocalDate requestDate;

    @Column(name = "reason", nullable = false, length = 1000)
    private String reason;

    @Column(name = "status")
    private String status;

    @Column(name = "approved_by")
    private Long approvedBy;
}
