package com.lankatex.smarttextile.common.designpattern.strategy;

import java.math.BigDecimal;

public interface StockStatusStrategy {

    String getStatus();

    String evaluate(BigDecimal availableQty, BigDecimal reorderLevel);
}