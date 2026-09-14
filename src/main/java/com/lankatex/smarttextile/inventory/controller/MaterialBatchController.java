package com.lankatex.smarttextile.inventory.controller;

import com.lankatex.smarttextile.inventory.entity.MaterialBatch;
import com.lankatex.smarttextile.inventory.service.MaterialBatchService;
import com.lankatex.smarttextile.inventory.service.MaterialService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/inventory/batches")
public class MaterialBatchController {

    private final MaterialBatchService batchService;
    private final MaterialService materialService;

    public MaterialBatchController(
            MaterialBatchService batchService,
            MaterialService materialService) {

        this.batchService = batchService;
        this.materialService = materialService;
    }

    @GetMapping
    public String list(Model model) {

        model.addAttribute(
                "batches",
                batchService.getAll()
        );

        return "inventory/batches";
    }

    @GetMapping("/new")
    public String newForm(Model model) {

        model.addAttribute(
                "batch",
                new MaterialBatch()
        );

        model.addAttribute(
                "materials",
                materialService
                        .getActiveMaterials(null)
        );

        return "inventory/batch-form";
    }

    @PostMapping("/save")
    public String save(
            @Valid
            @ModelAttribute("batch")
            MaterialBatch batch,
            BindingResult result,
            @RequestParam Long materialId,
            Model model) {

        batch.setMaterial(
                materialService
                        .getById(materialId)
        );

        if (result.hasErrors()) {

            model.addAttribute(
                    "materials",
                    materialService
                            .getActiveMaterials(null)
            );

            return "inventory/batch-form";
        }

        try {

            batchService.createBatch(batch);

        } catch (IllegalArgumentException e) {

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            model.addAttribute(
                    "materials",
                    materialService
                            .getActiveMaterials(null)
            );

            return "inventory/batch-form";
        }

        return "redirect:/inventory/batches";
    }
}