package com.lankatex.smarttextile.customer.dto;

public record CustomerDashboardStats(long customerCount, long quotationCount, long pendingOrderCount, long activeOrderCount,long delayedOrderCount,long deliveredOrderCount) {
}
