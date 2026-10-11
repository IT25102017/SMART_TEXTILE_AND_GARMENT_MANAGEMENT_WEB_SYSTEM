package com.lankatex.smarttextile.common.designpattern.strategy;

import java.math.BigDecimal;

// Strategy used when there is no available stock
public class OutOfStockStrategy implements StockStatusStrategy {

    @Override
    public String getStatus() {
        return "OUT_OF_STOCK";
    }

    // Returns the action required for an out of stock material
    @Override
    public String evaluate(BigDecimal availableQty, BigDecimal reorderLevel) {
        return "Material is out of stock. Immediate purchasing action is required.";
    }
}