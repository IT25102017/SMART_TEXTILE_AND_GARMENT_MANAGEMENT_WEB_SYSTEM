package com.lankatex.smarttextile.inventory.controller;

import com.lankatex.smarttextile.inventory.entity.Material;
import com.lankatex.smarttextile.inventory.service.MaterialCategoryService;
import com.lankatex.smarttextile.inventory.service.MaterialService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/inventory/materials")
public class MaterialController {

    private final MaterialService materialService;
    private final MaterialCategoryService categoryService;

    public MaterialController(
            MaterialService materialService,
            MaterialCategoryService categoryService) {

        this.materialService = materialService;
        this.categoryService = categoryService;
    }


    // =====================================================
    // READ / SEARCH
    // Shows ACTIVE + ARCHIVED materials
    // =====================================================

    @GetMapping
    public String list(
            @RequestParam(required = false)
            String keyword,
            Model model) {

        model.addAttribute(
                "materials",
                materialService.getMaterials(keyword)
        );

        model.addAttribute(
                "keyword",
                keyword
        );

        return "inventory/materials";
    }


    // =====================================================
    // CREATE FORM
    // =====================================================

    @GetMapping("/new")
    public String newForm(Model model) {

        model.addAttribute(
                "material",
                new Material()
        );

        model.addAttribute(
                "categories",
                categoryService
                        .getActiveCategories()
        );

        return "inventory/material-form";
    }


    // =====================================================
    // UPDATE FORM
    // =====================================================

    @GetMapping("/edit/{id}")
    public String edit(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "material",
                materialService.getById(id)
        );

        model.addAttribute(
                "categories",
                categoryService
                        .getAllCategories()
        );

        return "inventory/material-form";
    }


    // =====================================================
    // CREATE / UPDATE SAVE
    // =====================================================

    @PostMapping("/save")
    public String save(
            @Valid
            @ModelAttribute("material")
            Material material,
            BindingResult result,
            @RequestParam Long categoryId,
            Model model) {

        material.setCategory(
                categoryService
                        .getById(categoryId)
        );

        if (result.hasErrors()) {

            model.addAttribute(
                    "categories",
                    categoryService
                            .getAllCategories()
            );

            return "inventory/material-form";
        }

        try {

            materialService.save(material);

        } catch (IllegalArgumentException e) {

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            model.addAttribute(
                    "categories",
                    categoryService
                            .getAllCategories()
            );

            return "inventory/material-form";
        }

        return "redirect:/inventory/materials";
    }


    // =====================================================
    // ARCHIVE
    // =====================================================

    @PostMapping("/archive/{id}")
    public String archive(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            materialService.archive(id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Material archived successfully."
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/inventory/materials";
    }


    // =====================================================
    // RESTORE
    // =====================================================

    @PostMapping("/restore/{id}")
    public String restore(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            materialService.restore(id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Material restored successfully."
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/inventory/materials";
    }


    // =====================================================
    // HARD DELETE
    // =====================================================

    @PostMapping("/delete/{id}")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            materialService.delete(id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Material permanently deleted successfully."
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/inventory/materials";
    }


    // =====================================================
    // LOW STOCK
    // =====================================================

    @GetMapping("/low-stock")
    public String lowStock(Model model) {

        model.addAttribute(
                "materials",
                materialService
                        .getLowStockMaterials()
        );

        return "inventory/low-stock";
    }
}