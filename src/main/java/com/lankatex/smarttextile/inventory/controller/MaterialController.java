package com.lankatex.smarttextile.inventory.controller;

import com.lankatex.smarttextile.inventory.entity.Material;
import com.lankatex.smarttextile.inventory.service.MaterialCategoryService;
import com.lankatex.smarttextile.inventory.service.MaterialService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public String list(
            @RequestParam(required = false)
            String keyword,
            Model model) {

        model.addAttribute(
                "materials",
                materialService
                        .getActiveMaterials(keyword)
        );

        model.addAttribute(
                "keyword",
                keyword
        );

        return "inventory/materials";
    }

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
                        .getActiveCategories()
        );

        return "inventory/material-form";
    }

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
                            .getActiveCategories()
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
                            .getActiveCategories()
            );

            return "inventory/material-form";
        }

        return "redirect:/inventory/materials";
    }

    @PostMapping("/archive/{id}")
    public String archive(
            @PathVariable Long id) {

        materialService.archive(id);

        return "redirect:/inventory/materials";
    }

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