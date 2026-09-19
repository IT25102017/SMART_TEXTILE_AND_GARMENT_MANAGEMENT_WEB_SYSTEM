package com.lankatex.smarttextile.production.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Table(name = "production_plans")
@Getter
@Setter
@NoArgsConstructor
public class ProductionPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Long planId;
    @Column(name = "order_id", nullable = false)
    private Long orderId;
    @Column(name = "planned_qty", nullable = false)
    private BigDecimal plannedQty;
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    @Column(name = "end_date", nullable = false)

    private LocalDate endDate;
    @Column(name = "production_line", nullable = false)
    private String productionLine;
    @Column(name = "supervisor_id")
    private Long supervisorId;
    @Column(name = "status")
    private String status;
}