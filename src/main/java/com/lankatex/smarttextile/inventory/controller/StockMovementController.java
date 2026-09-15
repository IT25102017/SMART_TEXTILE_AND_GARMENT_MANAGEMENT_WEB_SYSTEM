package com.lankatex.smarttextile.inventory.controller;

import com.lankatex.smarttextile.inventory.entity.StockMovement;
import com.lankatex.smarttextile.inventory.entity.MovementType;
import com.lankatex.smarttextile.inventory.repository.MaterialBatchRepository;
import com.lankatex.smarttextile.inventory.service.StockMovementService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/inventory/movements")
public class StockMovementController {

    private final StockMovementService movementService;
    private final MaterialBatchRepository batchRepository;

    public StockMovementController(
            StockMovementService movementService,
            MaterialBatchRepository batchRepository) {

        this.movementService = movementService;
        this.batchRepository = batchRepository;
    }

    @GetMapping
    public String list(Model model) {

        model.addAttribute(
                "movements",
                movementService.getAll()
        );

        return "inventory/movements";
    }

    @GetMapping("/new")
    public String newForm(Model model) {

        model.addAttribute(
                "movement",
                new StockMovement()
        );

        model.addAttribute(
                "batches",
                batchRepository
                        .findAllByOrderByReceivedDateDesc()
        );

        model.addAttribute(
                "movementTypes",
                MovementType.values()
        );

        return "inventory/movement-form";
    }

    @PostMapping("/save")
    public String save(
            @ModelAttribute StockMovement movement,
            @RequestParam Long batchId,
            Model model) {

        movement.setBatch(
                batchRepository
                        .findById(batchId)
                        .orElseThrow()
        );

        try {

            movementService
                    .recordMovement(movement);

        } catch (IllegalArgumentException e) {

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            model.addAttribute(
                    "movement",
                    movement
            );

            model.addAttribute(
                    "batches",
                    batchRepository
                            .findAllByOrderByReceivedDateDesc()
            );

            model.addAttribute(
                    "movementTypes",
                    MovementType.values()
            );

            return "inventory/movement-form";
        }

        return "redirect:/inventory/movements";
    }
}