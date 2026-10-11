package com.lankatex.smarttextile.common.designpattern.strategy;

import java.math.BigDecimal;

public class NormalStockStrategy implements StockStatusStrategy {

    @Override
    public String getStatus() {
        return "NORMAL_STOCK";
    }

    @Override
    public String evaluate(BigDecimal availableQty, BigDecimal reorderLevel) {
        return "Material stock level is healthy. No purchasing action is required.";
    }
}