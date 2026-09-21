package com.lankatex.smarttextile.inventory.controller;

import com.lankatex.smarttextile.inventory.entity.GoodsReceipt;
import com.lankatex.smarttextile.inventory.service.GoodsReceiptService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/inventory/receipts")
public class GoodsReceiptController {

    private final GoodsReceiptService receiptService;


    public GoodsReceiptController(
            GoodsReceiptService receiptService) {

        this.receiptService = receiptService;
    }


    // =====================================================
    // LIST
    // =====================================================

    @GetMapping
    public String list(Model model) {

        model.addAttribute(
                "receipts",
                receiptService.getAll()
        );

        return "inventory/receipts";
    }


    // =====================================================
    // CREATE FORM
    // =====================================================

    @GetMapping("/new")
    public String newForm(Model model) {

        model.addAttribute(
                "receipt",
                new GoodsReceipt()
        );

        model.addAttribute(
                "isEdit",
                false
        );

        return "inventory/receipt-form";
    }


    // =====================================================
    // EDIT FORM
    // =====================================================

    @GetMapping("/edit/{id}")
    public String editForm(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "receipt",
                receiptService.getById(id)
        );

        model.addAttribute(
                "isEdit",
                true
        );

        return "inventory/receipt-form";
    }


    // =====================================================
    // CREATE / UPDATE
    // =====================================================

    @PostMapping("/save")
    public String save(
            @Valid
            @ModelAttribute("receipt")
            GoodsReceipt receipt,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        boolean editing =
                receipt.getReceiptId() != null;


        if (result.hasErrors()) {

            model.addAttribute(
                    "isEdit",
                    editing
            );

            return "inventory/receipt-form";
        }


        try {

            if (editing) {

                receiptService.update(receipt);

                redirectAttributes
                        .addFlashAttribute(
                                "message",
                                "Goods receipt updated successfully."
                        );

            } else {

                receiptService.create(receipt);

                redirectAttributes
                        .addFlashAttribute(
                                "message",
                                "Goods receipt created successfully."
                        );
            }

        } catch (IllegalArgumentException e) {

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            model.addAttribute(
                    "isEdit",
                    editing
            );

            return "inventory/receipt-form";
        }


        return "redirect:/inventory/receipts";
    }


    // =====================================================
    // ARCHIVE
    // =====================================================

    @PostMapping("/archive/{id}")
    public String archive(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            receiptService.archive(id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Goods receipt archived successfully."
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }


        return "redirect:/inventory/receipts";
    }


    // =====================================================
    // RESTORE
    // =====================================================

    @PostMapping("/restore/{id}")
    public String restore(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            receiptService.restore(id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Goods receipt restored successfully."
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }


        return "redirect:/inventory/receipts";
    }


    // =====================================================
    // DELETE
    // =====================================================

    @PostMapping("/delete/{id}")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            receiptService.delete(id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Goods receipt permanently deleted successfully."
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }


        return "redirect:/inventory/receipts";
    }
}