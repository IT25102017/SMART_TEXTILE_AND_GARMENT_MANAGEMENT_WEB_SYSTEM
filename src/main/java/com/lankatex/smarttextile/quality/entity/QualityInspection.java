package com.lankatex.smarttextile.quality.entity;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Table(name = "quality_inspections")
@Getter
@Setter
@NoArgsConstructor
public class QualityInspection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inspection_id")
    private Long inspectionId;
    @Column(name = "plan_id", nullable = false)
    private Long planId;
    @Column(name = "order_id")
    private Long orderId;
    @Column(name = "stage_name", nullable = false)
    private String stageName;

    @Column(name = "inspection_date", nullable = false)
    private LocalDate inspectionDate;
    @Column(name = "inspector_id")
    private Long inspectorId;
    @Column(name = "status")
    private String status;
}
