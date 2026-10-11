package com.lankatex.smarttextile.common.designpattern.strategy;

import java.math.BigDecimal;

public class OutOfStockStrategy implements StockStatusStrategy {

    @Override
    public String getStatus() {
        return "OUT_OF_STOCK";
    }

    @Override
    public String evaluate(BigDecimal availableQty, BigDecimal reorderLevel) {
        return "Material is out of stock. Immediate purchasing action is required.";
    }
}