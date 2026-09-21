package com.lankatex.smarttextile.inventory.controller;

import com.lankatex.smarttextile.inventory.entity.GoodsReceipt;
import com.lankatex.smarttextile.inventory.service.GoodsReceiptService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

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

        /*
         * Lookup maps are used to display readable
         * Purchasing information instead of raw IDs.
         */
        model.addAttribute(
                "purchaseOrderMap",
                receiptService.getPurchaseOrderMap()
        );

        model.addAttribute(
                "supplierMap",
                receiptService.getSupplierMap()
        );

        return "inventory/receipts";
    }


    // =====================================================
    // CREATE FORM
    // =====================================================

    @GetMapping("/new")
    public String newForm(Model model) {

        GoodsReceipt receipt =
                new GoodsReceipt();

        receipt.setReceivedDate(
                LocalDate.now()
        );

        receipt.setStatus(
                "RECEIVED"
        );


        prepareForm(
                model,
                receipt,
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

        prepareForm(
                model,
                receiptService.getById(id),
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

            prepareForm(
                    model,
                    receipt,
                    editing
            );

            return "inventory/receipt-form";
        }


        try {

            if (editing) {

                receiptService.update(
                        receipt
                );

                redirectAttributes
                        .addFlashAttribute(
                                "message",
                                "Goods receipt updated successfully."
                        );

            } else {

                receiptService.create(
                        receipt
                );

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

            prepareForm(
                    model,
                    receipt,
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


    // =====================================================
    // FORM DATA
    //
    // Supplies Purchase Orders and Suppliers to the
    // Goods Receipt form dropdowns.
    // =====================================================

    private void prepareForm(
            Model model,
            GoodsReceipt receipt,
            boolean isEdit) {

        model.addAttribute(
                "receipt",
                receipt
        );

        model.addAttribute(
                "purchaseOrders",
                receiptService.getAvailablePurchaseOrders()
        );

        model.addAttribute(
                "suppliers",
                receiptService.getActiveSuppliers()
        );

        model.addAttribute(
                "isEdit",
                isEdit
        );
    }
}