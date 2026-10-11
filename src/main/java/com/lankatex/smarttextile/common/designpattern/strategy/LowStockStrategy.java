package com.lankatex.smarttextile.common.designpattern.strategy;

import java.math.BigDecimal;

public class LowStockStrategy implements StockStatusStrategy {

    @Override
    public String getStatus() {
        return "LOW_STOCK";
    }

    @Override
    public String evaluate(BigDecimal availableQty, BigDecimal reorderLevel) {
        return "Material stock is at or below the reorder level. Purchasing should be considered.";
    }
}