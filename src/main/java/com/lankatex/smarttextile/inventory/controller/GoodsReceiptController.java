package com.lankatex.smarttextile.inventory.controller;

import com.lankatex.smarttextile.inventory.entity.GoodsReceipt;
import com.lankatex.smarttextile.inventory.service.GoodsReceiptService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/inventory/receipts")
public class GoodsReceiptController {

    private final GoodsReceiptService receiptService;

    public GoodsReceiptController(
            GoodsReceiptService receiptService) {

        this.receiptService = receiptService;
    }

    @GetMapping
    public String list(Model model) {

        model.addAttribute(
                "receipts",
                receiptService.getAll()
        );

        return "inventory/receipts";
    }

    @GetMapping("/new")
    public String newForm(Model model) {

        model.addAttribute(
                "receipt",
                new GoodsReceipt()
        );

        return "inventory/receipt-form";
    }

    @PostMapping("/save")
    public String save(
            @Valid
            @ModelAttribute("receipt")
            GoodsReceipt receipt,
            BindingResult result) {

        if (result.hasErrors()) {
            return "inventory/receipt-form";
        }

        receiptService.save(receipt);

        return "redirect:/inventory/receipts";
    }
}