package com.lankatex.smarttextile.common.designpattern.factory;

import com.lankatex.smarttextile.common.designpattern.strategy.LowStockStrategy;
import com.lankatex.smarttextile.common.designpattern.strategy.NormalStockStrategy;
import com.lankatex.smarttextile.common.designpattern.strategy.OutOfStockStrategy;
import com.lankatex.smarttextile.common.designpattern.strategy.StockStatusStrategy;

import java.math.BigDecimal;

// Factory class that selects the correct stock strategy
public final class StockStatusStrategyFactory {

    // Prevent creating factory objects
    private StockStatusStrategyFactory() {
    }

    // Creates the correct strategy using quantity and reorder level
    public static StockStatusStrategy createStrategy(
            BigDecimal availableQty,
            BigDecimal reorderLevel) {

        // Check required values
        if (availableQty == null || reorderLevel == null) {
            throw new IllegalArgumentException(
                    "Available quantity and reorder level are required."
            );
        }

        // No stock available
        if (availableQty.compareTo(BigDecimal.ZERO) <= 0) {
            return new OutOfStockStrategy();
        }

        // Stock is at or below reorder level
        if (availableQty.compareTo(reorderLevel) <= 0) {
            return new LowStockStrategy();
        }

        // Stock is above reorder level
        return new NormalStockStrategy();
    }
}