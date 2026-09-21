package com.lankatex.smarttextile.inventory.controller;

import com.lankatex.smarttextile.inventory.entity.MaterialBatch;
import com.lankatex.smarttextile.inventory.service.MaterialBatchService;
import com.lankatex.smarttextile.inventory.service.MaterialService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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


    // =====================================================
    // LIST
    // =====================================================

    @GetMapping
    public String list(Model model) {

        model.addAttribute(
                "batches",
                batchService.getAll()
        );

        return "inventory/batches";
    }


    // =====================================================
    // CREATE FORM
    // =====================================================

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

        model.addAttribute(
                "isEdit",
                false
        );

        return "inventory/batch-form";
    }


    // =====================================================
    // EDIT FORM
    // =====================================================

    @GetMapping("/edit/{id}")
    public String editForm(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "batch",
                batchService.getById(id)
        );

        model.addAttribute(
                "materials",
                materialService
                        .getActiveMaterials(null)
        );

        model.addAttribute(
                "isEdit",
                true
        );

        return "inventory/batch-form";
    }


    // =====================================================
    // CREATE / UPDATE
    // =====================================================

    @PostMapping("/save")
    public String save(
            @Valid
            @ModelAttribute("batch")
            MaterialBatch batch,
            BindingResult result,
            @RequestParam Long materialId,
            Model model,
            RedirectAttributes redirectAttributes) {

        boolean editing =
                batch.getBatchId() != null;


        /*
         * Set material so validation and the form
         * have the correct Material object.
         */
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

            model.addAttribute(
                    "isEdit",
                    editing
            );

            return "inventory/batch-form";
        }


        try {

            if (editing) {

                batchService.updateBatch(
                        batch
                );

                redirectAttributes
                        .addFlashAttribute(
                                "message",
                                "Material batch updated successfully."
                        );

            } else {

                batchService.createBatch(
                        batch
                );

                redirectAttributes
                        .addFlashAttribute(
                                "message",
                                "Material batch received successfully."
                        );
            }


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

            model.addAttribute(
                    "isEdit",
                    editing
            );

            return "inventory/batch-form";
        }


        return "redirect:/inventory/batches";
    }


    // =====================================================
    // ARCHIVE
    // =====================================================

    @PostMapping("/archive/{id}")
    public String archive(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            batchService.archive(id);

            redirectAttributes
                    .addFlashAttribute(
                            "message",
                            "Material batch archived successfully."
                    );

        } catch (IllegalArgumentException e) {

            redirectAttributes
                    .addFlashAttribute(
                            "error",
                            e.getMessage()
                    );
        }


        return "redirect:/inventory/batches";
    }


    // =====================================================
    // RESTORE
    // =====================================================

    @PostMapping("/restore/{id}")
    public String restore(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            batchService.restore(id);

            redirectAttributes
                    .addFlashAttribute(
                            "message",
                            "Material batch restored successfully."
                    );

        } catch (IllegalArgumentException e) {

            redirectAttributes
                    .addFlashAttribute(
                            "error",
                            e.getMessage()
                    );
        }


        return "redirect:/inventory/batches";
    }


    // =====================================================
    // SAFE DELETE
    // =====================================================

    @PostMapping("/delete/{id}")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            batchService.delete(id);

            redirectAttributes
                    .addFlashAttribute(
                            "message",
                            "Material batch permanently deleted successfully."
                    );

        } catch (IllegalArgumentException e) {

            redirectAttributes
                    .addFlashAttribute(
                            "error",
                            e.getMessage()
                    );
        }


        return "redirect:/inventory/batches";
    }
}