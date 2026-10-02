package com.lankatex.smarttextile.production.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    @NotNull(message = "Material Request is required")
    @Column(name = "material_request_id", nullable = false)
    private Long materialRequestId;

    @NotNull(message = "Production Plan is required")
    @Column(name = "plan_id", nullable = false)
    private Long planId;

    @NotNull(message = "Material is required")
    @Column(name = "material_id", nullable = false)
    private Long materialId;

    @NotNull(message = "Material Batch is required")
    @Column(name = "batch_id", nullable = false)
    private Long batchId;

    @NotNull(message = "Issued quantity is required")
    @DecimalMin(value = "0.01", message = "Issued quantity must be greater than zero")
    @Column(name = "issued_qty", nullable = false)
    private BigDecimal issuedQty;

    @NotNull(message = "Issue date is required")
    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "issued_by")
    private Long issuedBy;

    @Transient
    public String getIssueCode() {
        if (issueId == null) {
            return "MIN-NEW";
        }
        return String.format("MIN-%03d", issueId);
    }
}