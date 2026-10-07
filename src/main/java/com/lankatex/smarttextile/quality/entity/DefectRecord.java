package com.lankatex.smarttextile.quality.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "defect_records")
@Getter
@Setter
@NoArgsConstructor
public class DefectRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "defect_id")
    private Long defectId;

    @Column(name = "inspection_id", nullable = false)
    private Long inspectionId;

    @Column(name = "defect_type", nullable = false)
    private String defectType;

    @Column(name = "severity", nullable = false)
    private String severity;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "responsible_department_id")
    private Long responsibleDepartmentId;

    @Column(name = "responsible_stage")
    private String responsibleStage;

    @Column(name = "remarks", length = 1000)
    private String remarks;

    @Column(name = "status")
    private String status = "ACTIVE";

    @PrePersist
    @PreUpdate
    @PostLoad
    private void normalizeStatus() {
        if (status == null || status.isBlank()) {
            status = "ACTIVE";
        }
    }

    @Transient
    public String getDefectCode() {
        return defectId == null
                ? "DEF-NEW"
                : String.format("DEF-%03d", defectId);
    }
}