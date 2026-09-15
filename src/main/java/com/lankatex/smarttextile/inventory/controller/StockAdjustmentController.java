package com.lankatex.smarttextile.inventory.controller;

import com.lankatex.smarttextile.inventory.entity.StockAdjustment;
import com.lankatex.smarttextile.inventory.repository.MaterialBatchRepository;
import com.lankatex.smarttextile.inventory.service.StockAdjustmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/inventory/adjustments")
public class StockAdjustmentController {

    private final StockAdjustmentService adjustmentService;
    private final MaterialBatchRepository batchRepository;

    public StockAdjustmentController(
            StockAdjustmentService adjustmentService,
            MaterialBatchRepository batchRepository) {

        this.adjustmentService = adjustmentService;
        this.batchRepository = batchRepository;
    }

    @GetMapping
    public String list(Model model) {

        model.addAttribute(
                "adjustments",
                adjustmentService.getAll()
        );

        return "inventory/adjustments";
    }

    @GetMapping("/new")
    public String newForm(Model model) {

        model.addAttribute(
                "adjustment",
                new StockAdjustment()
        );

        model.addAttribute(
                "batches",
                batchRepository
                        .findAllByOrderByReceivedDateDesc()
        );

        return "inventory/adjustment-form";
    }

    @PostMapping("/save")
    public String save(
            @ModelAttribute StockAdjustment adjustment,
            @RequestParam Long batchId,
            Model model) {

        adjustment.setBatch(
                batchRepository
                        .findById(batchId)
                        .orElseThrow()
        );

        try {

            adjustmentService.create(adjustment);

        } catch (IllegalArgumentException e) {

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            model.addAttribute(
                    "batches",
                    batchRepository
                            .findAllByOrderByReceivedDateDesc()
            );

            return "inventory/adjustment-form";
        }

        return "redirect:/inventory/adjustments";
    }

    @PostMapping("/approve/{id}")
    public String approve(
            @PathVariable Long id,
            @RequestParam Long approvedBy) {

        adjustmentService.approve(
                id,
                approvedBy
        );

        return "redirect:/inventory/adjustments";
    }
}