package com.lankatex.smarttextile.inventory.controller;

import com.lankatex.smarttextile.inventory.entity.MaterialCategory;
import com.lankatex.smarttextile.inventory.service.MaterialCategoryService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/inventory/categories")
public class MaterialCategoryController {

    private final MaterialCategoryService categoryService;

    public MaterialCategoryController(
            MaterialCategoryService categoryService) {

        this.categoryService = categoryService;
    }

    // READ
    @GetMapping
    public String list(Model model) {

        model.addAttribute(
                "categories",
                categoryService.getAllCategories()
        );

        return "inventory/categories";
    }

    // CREATE FORM
    @GetMapping("/new")
    public String newForm(Model model) {

        model.addAttribute(
                "category",
                new MaterialCategory()
        );

        return "inventory/category-form";
    }

    // UPDATE FORM
    @GetMapping("/edit/{id}")
    public String edit(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "category",
                categoryService.getById(id)
        );

        return "inventory/category-form";
    }

    // CREATE / UPDATE
    @PostMapping("/save")
    public String save(
            @Valid
            @ModelAttribute("category")
            MaterialCategory category,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {

            return "inventory/category-form";
        }

        try {

            categoryService.save(category);

        } catch (IllegalArgumentException e) {

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            return "inventory/category-form";
        }

        return "redirect:/inventory/categories";
    }

    // SOFT DELETE / ARCHIVE
    @PostMapping("/archive/{id}")
    public String archive(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            categoryService.archive(id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Category archived successfully."
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/inventory/categories";
    }

    // HARD DELETE
    @PostMapping("/delete/{id}")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            categoryService.delete(id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Category permanently deleted successfully."
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/inventory/categories";
    }
}