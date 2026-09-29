package com.lankatex.smarttextile.quality.dto;

public record QualityDashboardStats(
        long inspectionCount,
        long pendingInspectionCount,
        long defectCount,
        long pendingReworkCount,
        long wastageRecordCount,
        long activeHoldCount,
        double rejectionRate
) {
}