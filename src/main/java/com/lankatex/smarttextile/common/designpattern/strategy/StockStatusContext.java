package com.lankatex.smarttextile.common.designpattern.strategy;

import java.math.BigDecimal;

public class StockStatusContext {

    private StockStatusStrategy strategy;

    public void setStrategy(StockStatusStrategy strategy) {
        this.strategy = strategy;
    }

    public String evaluate(BigDecimal availableQty, BigDecimal reorderLevel) {

        if (strategy == null) {
            throw new IllegalStateException(
                    "Stock status strategy has not been selected."
            );
        }

        return strategy.evaluate(availableQty, reorderLevel);
    }

    public String getStatus() {

        if (strategy == null) {
            throw new IllegalStateException(
                    "Stock status strategy has not been selected."
            );
        }

        return strategy.getStatus();
    }
}