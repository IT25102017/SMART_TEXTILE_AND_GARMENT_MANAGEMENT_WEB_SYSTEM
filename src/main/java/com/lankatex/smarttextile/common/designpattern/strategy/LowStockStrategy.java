package com.lankatex.smarttextile.common.designpattern.strategy;

import java.math.BigDecimal;

// Strategy used when stock reaches the reorder level
public class LowStockStrategy implements StockStatusStrategy {

    @Override
    public String getStatus() {
        return "LOW_STOCK";
    }

    // Returns the action required for a low stock material
    @Override
    public String evaluate(BigDecimal availableQty, BigDecimal reorderLevel) {
        return "Material stock is at or below the reorder level. Purchasing should be considered.";
    }
}