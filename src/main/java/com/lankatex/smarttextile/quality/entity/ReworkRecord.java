package com.lankatex.smarttextile.quality.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "rework_records")
@Getter
@Setter
@NoArgsConstructor
public class ReworkRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rework_id")
    private Long reworkId;

    @Column(name = "inspection_id", nullable = false)
    private Long inspectionId;

    @Column(name = "defect_id", nullable = false)
    private Long defectId;

    @Column(name = "quantity_sent", nullable = false)
    private Integer quantitySent;

    @Column(name = "status")
    private String status = "PENDING";

    @Column(name = "target_date")
    private LocalDate targetDate;

    @Column(name = "verified_by")
    private Long verifiedBy;

    @Column(name = "reinspection_result")
    private String reinspectionResult = "PENDING";

    @PrePersist
    @PreUpdate
    @PostLoad
    private void normalizeValues() {
        if (status == null || status.isBlank()) status = "PENDING";
        if (reinspectionResult == null || reinspectionResult.isBlank()) {
            reinspectionResult = "PENDING";
        }
    }

    @Transient
    public String getReworkCode() {
        return reworkId == null
                ? "RWK-NEW"
                : String.format("RWK-%03d", reworkId);
    }
}