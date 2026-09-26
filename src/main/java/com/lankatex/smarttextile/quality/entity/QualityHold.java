package com.lankatex.smarttextile.quality.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "quality_holds")
@Getter
@Setter
@NoArgsConstructor
public class QualityHold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hold_id")
    private Long holdId;

    @Column(name = "plan_id")
    private Long planId;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "inspection_id", nullable = false)
    private Long inspectionId;

    @Column(name = "reason", nullable = false, length = 1000)
    private String reason;

    @Column(name = "status")
    private String status = "ACTIVE";

    @Column(name = "released_by")
    private Long releasedBy;

    @Column(name = "released_at")
    private LocalDateTime releasedAt;

    @PrePersist
    @PreUpdate
    @PostLoad
    private void normalizeStatus() {
        if (status == null || status.isBlank()) {
            status = "ACTIVE";
        }
    }

    @Transient
    public String getHoldCode() {
        return holdId == null
                ? "HLD-NEW"
                : String.format("HLD-%03d", holdId);
    }
}