package com.lankatex.smarttextile.common.designpattern.factory;

import com.lankatex.smarttextile.common.designpattern.strategy.LowStockStrategy;
import com.lankatex.smarttextile.common.designpattern.strategy.NormalStockStrategy;
import com.lankatex.smarttextile.common.designpattern.strategy.OutOfStockStrategy;
import com.lankatex.smarttextile.common.designpattern.strategy.StockStatusStrategy;

import java.math.BigDecimal;

public final class StockStatusStrategyFactory {

    private StockStatusStrategyFactory() {
    }

    public static StockStatusStrategy createStrategy(
            BigDecimal availableQty,
            BigDecimal reorderLevel) {

        if (availableQty == null || reorderLevel == null) {
            throw new IllegalArgumentException(
                    "Available quantity and reorder level are required."
            );
        }

        if (availableQty.compareTo(BigDecimal.ZERO) <= 0) {
            return new OutOfStockStrategy();
        }

        if (availableQty.compareTo(reorderLevel) <= 0) {
            return new LowStockStrategy();
        }

        return new NormalStockStrategy();
    }
}