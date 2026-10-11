package com.lankatex.smarttextile.common.designpattern.strategy;

import java.math.BigDecimal;

// Common interface for all stock status strategies
public interface StockStatusStrategy {

    // Returns the stock status
    String getStatus();

    // Returns the message for the selected stock status
    String evaluate(BigDecimal availableQty, BigDecimal reorderLevel);
}