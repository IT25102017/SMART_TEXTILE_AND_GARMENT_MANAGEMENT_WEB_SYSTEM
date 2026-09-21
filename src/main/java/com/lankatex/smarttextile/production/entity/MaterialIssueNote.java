package com.lankatex.smarttextile.production.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Table(name = "material_issue_notes")
@Getter
@Setter
@NoArgsConstructor
public class MaterialIssueNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "issue_id")
    private Long issueId;
    @Column(name = "material_request_id", nullable = false)
    private Long materialRequestId;
    @Column(name = "plan_id", nullable = false)
    private Long planId;
    @Column(name = "material_id", nullable = false)
    private Long materialId;
    @Column(name = "batch_id", nullable = false)
    private Long batchId;
    @Column(name = "issued_qty", nullable = false)
    private BigDecimal issuedQty;
    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;
    @Column(name = "issued_by")
    private Long issuedBy;
}


