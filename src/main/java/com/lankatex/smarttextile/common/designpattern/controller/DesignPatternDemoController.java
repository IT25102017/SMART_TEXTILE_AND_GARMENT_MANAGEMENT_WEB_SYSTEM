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

// Controller used to test the implemented design patterns
@RestController
@RequestMapping("/design-pattern-demo")
public class DesignPatternDemoController {

    // Tests Factory, Strategy and Singleton patterns together
    @GetMapping("/stock-status")
    public Map<String, Object> evaluateStockStatus(
            @RequestParam BigDecimal availableQty,
            @RequestParam BigDecimal reorderLevel) {

        // Prevent negative available quantities
        if (availableQty.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Available quantity cannot be negative."
            );
        }

        // Prevent negative reorder levels
        if (reorderLevel.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Reorder level cannot be negative."
            );
        }

        // Factory selects the correct strategy
        StockStatusStrategy strategy =
                StockStatusStrategyFactory.createStrategy(
                        availableQty,
                        reorderLevel
                );

        // Context uses the selected strategy
        StockStatusContext context = new StockStatusContext();
        context.setStrategy(strategy);

        // Execute the selected strategy
        String message =
                context.evaluate(availableQty, reorderLevel);

        // Get the single AuditLogger instance
        AuditLogger logger = AuditLogger.getInstance();

        // Record the stock status result
        logger.log(
                "Stock status evaluated as "
                        + context.getStatus()
                        + " for available quantity "
                        + availableQty
                        + " and reorder level "
                        + reorderLevel
        );

        // Prepare the result shown in the browser
        Map<String, Object> response = new LinkedHashMap<>();

        response.put("availableQuantity", availableQty);
        response.put("reorderLevel", reorderLevel);
        response.put("stockStatus", context.getStatus());

        // Shows which strategy was selected
        response.put(
                "selectedStrategy",
                strategy.getClass().getSimpleName()
        );

        response.put("message", message);

        // Shows the Singleton logger instance
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

    // Checks whether AuditLogger returns the same instance
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

        // True means both variables contain the same object
        response.put(
                "sameInstance",
                firstLogger == secondLogger
        );

        return response;
    }
}