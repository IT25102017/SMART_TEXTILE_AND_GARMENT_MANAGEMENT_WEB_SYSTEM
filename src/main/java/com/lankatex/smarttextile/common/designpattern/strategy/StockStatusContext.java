package com.lankatex.smarttextile.common.designpattern.strategy;

import java.math.BigDecimal;

// Context class that uses the selected strategy
public class StockStatusContext {

    private StockStatusStrategy strategy;

    // Sets the strategy selected by the factory
    public void setStrategy(StockStatusStrategy strategy) {
        this.strategy = strategy;
    }

    // Executes the selected strategy
    public String evaluate(BigDecimal availableQty, BigDecimal reorderLevel) {

        if (strategy == null) {
            throw new IllegalStateException(
                    "Stock status strategy has not been selected."
            );
        }

        return strategy.evaluate(availableQty, reorderLevel);
    }

    // Gets the status from the selected strategy
    public String getStatus() {

        if (strategy == null) {
            throw new IllegalStateException(
                    "Stock status strategy has not been selected."
            );
        }

        return strategy.getStatus();
    }
}