package com.lankatex.smarttextile.inventory.controller;

import com.lankatex.smarttextile.inventory.service.MaterialService;
import com.lankatex.smarttextile.inventory.service.StockMovementService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InventoryDashboardController {

    private final MaterialService materialService;
    private final StockMovementService movementService;

    public InventoryDashboardController(
            MaterialService materialService,
            StockMovementService movementService) {

        this.materialService = materialService;
        this.movementService = movementService;
    }

    @GetMapping("/inventory")
    public String dashboard(Model model) {

        model.addAttribute(
                "materials",
                materialService
                        .getActiveMaterials(null)
        );

        model.addAttribute(
                "lowStock",
                materialService
                        .getLowStockMaterials()
        );

        model.addAttribute(
                "movements",
                movementService.getAll()
        );

        return "inventory/dashboard";
    }
}