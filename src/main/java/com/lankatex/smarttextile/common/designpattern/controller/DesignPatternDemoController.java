package com.lankatex.smarttextile.common.designpattern.controller;

import com.lankatex.smarttextile.common.designpattern.factory.StockStatusStrategyFactory;
import com.lankatex.smarttextile.common.designpattern.singleton.AuditLogger;
import com.lankatex.smarttextile.common.designpattern.strategy.StockStatusContext;
import com.lankatex.smarttextile.common.designpattern.strategy.StockStatusStrategy;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/design-pattern-demo")
public class DesignPatternDemoController {

    @GetMapping("/stock-status")
    public Map<String, Object> evaluateStockStatus(
            @RequestParam BigDecimal availableQty,
            @RequestParam BigDecimal reorderLevel) {

        if (availableQty.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Available quantity cannot be negative."
            );
        }

        if (reorderLevel.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Reorder level cannot be negative."
            );
        }

        // Factory Pattern
        StockStatusStrategy strategy =
                StockStatusStrategyFactory.createStrategy(
                        availableQty,
                        reorderLevel
                );

        // Strategy Pattern
        StockStatusContext context = new StockStatusContext();
        context.setStrategy(strategy);

        String message =
                context.evaluate(availableQty, reorderLevel);

        // Singleton Pattern
        AuditLogger logger = AuditLogger.getInstance();

        logger.log(
                "Stock status evaluated as "
                        + context.getStatus()
                        + " for available quantity "
                        + availableQty
                        + " and reorder level "
                        + reorderLevel
        );

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("availableQuantity", availableQty);
        response.put("reorderLevel", reorderLevel);
        response.put("stockStatus", context.getStatus());
        response.put(
                "selectedStrategy",
                strategy.getClass().getSimpleName()
        );
        response.put("message", message);
        response.put(
                "singletonLoggerInstance",
                System.identityHashCode(logger)
        );
        response.put(
                "designPatternFlow",
                "Factory -> Strategy -> Singleton"
        );

        return response;
    }

    @GetMapping("/singleton-check")
    public Map<String, Object> checkSingleton() {

        AuditLogger firstLogger =
                AuditLogger.getInstance();

        AuditLogger secondLogger =
                AuditLogger.getInstance();

        Map<String, Object> response = new LinkedHashMap<>();

        response.put(
                "firstInstance",
                System.identityHashCode(firstLogger)
        );

        response.put(
                "secondInstance",
                System.identityHashCode(secondLogger)
        );

        response.put(
                "sameInstance",
                firstLogger == secondLogger
        );

        return response;
    }
}