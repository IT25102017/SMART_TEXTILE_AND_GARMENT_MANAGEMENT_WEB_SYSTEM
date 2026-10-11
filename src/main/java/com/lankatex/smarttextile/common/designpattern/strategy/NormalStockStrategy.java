package com.lankatex.smarttextile.common.designpattern.strategy;

import java.math.BigDecimal;

// Strategy used when the stock level is normal
public class NormalStockStrategy implements StockStatusStrategy {

    @Override
    public String getStatus() {
        return "NORMAL_STOCK";
    }

    // Returns the message for a healthy stock level
    @Override
    public String evaluate(BigDecimal availableQty, BigDecimal reorderLevel) {
        return "Material stock level is healthy. No purchasing action is required.";
    }
}