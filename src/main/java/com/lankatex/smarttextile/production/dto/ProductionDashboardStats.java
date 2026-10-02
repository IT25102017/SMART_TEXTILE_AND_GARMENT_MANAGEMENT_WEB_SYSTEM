package com.lankatex.smarttextile.production.dto;

public record ProductionDashboardStats(
        long activePlanCount,
        long pendingMaterialRequestCount,
        long materialIssueCount,
        long delayedPlanCount) {
}