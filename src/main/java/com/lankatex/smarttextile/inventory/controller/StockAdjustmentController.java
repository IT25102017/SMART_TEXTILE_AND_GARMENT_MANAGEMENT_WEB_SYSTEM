package com.lankatex.smarttextile.inventory.controller;

import com.lankatex.smarttextile.inventory.entity.MaterialBatch;
import com.lankatex.smarttextile.inventory.entity.StockAdjustment;
import com.lankatex.smarttextile.inventory.repository.MaterialBatchRepository;
import com.lankatex.smarttextile.inventory.service.StockAdjustmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/inventory/adjustments")
public class StockAdjustmentController {

    private final StockAdjustmentService adjustmentService;

    private final MaterialBatchRepository batchRepository;


    public StockAdjustmentController(
            StockAdjustmentService adjustmentService,
            MaterialBatchRepository batchRepository) {

        this.adjustmentService = adjustmentService;
        this.batchRepository = batchRepository;
    }


    // =====================================================
    // LIST
    // =====================================================

    @GetMapping
    public String list(Model model) {

        model.addAttribute(
                "adjustments",
                adjustmentService.getAll()
        );


        return "inventory/adjustments";
    }


    // =====================================================
    // NEW FORM
    // =====================================================

    @GetMapping("/new")
    public String newForm(Model model) {

        model.addAttribute(
                "adjustment",
                new StockAdjustment()
        );


        model.addAttribute(
                "batches",
                batchRepository
                        .findAllByOrderByReceivedDateDesc()
        );


        model.addAttribute(
                "isEdit",
                false
        );


        return "inventory/adjustment-form";
    }


    // =====================================================
    // EDIT FORM
    //
    // Only PENDING adjustments may be edited.
    // =====================================================

    @GetMapping("/edit/{id}")
    public String editForm(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {

            StockAdjustment adjustment =
                    adjustmentService.getById(id);


            if (!"PENDING".equalsIgnoreCase(
                    adjustment.getApprovalStatus())) {

                redirectAttributes
                        .addFlashAttribute(
                                "error",
                                "Approved stock adjustments are locked and cannot be edited."
                        );


                return "redirect:/inventory/adjustments";
            }


            model.addAttribute(
                    "adjustment",
                    adjustment
            );


            model.addAttribute(
                    "batches",
                    batchRepository
                            .findAllByOrderByReceivedDateDesc()
            );


            model.addAttribute(
                    "isEdit",
                    true
            );


            return "inventory/adjustment-form";


        } catch (IllegalArgumentException e) {

            redirectAttributes
                    .addFlashAttribute(
                            "error",
                            e.getMessage()
                    );


            return "redirect:/inventory/adjustments";
        }
    }


    // =====================================================
    // CREATE / UPDATE
    // =====================================================

    @PostMapping("/save")
    public String save(
            @ModelAttribute("adjustment")
            StockAdjustment adjustment,
            @RequestParam Long batchId,
            Model model,
            RedirectAttributes redirectAttributes) {

        boolean editing =
                adjustment.getAdjustmentId() != null;


        try {

            if (editing) {

                /*
                 * Restore the permanent relationship information
                 * from the existing adjustment.
                 */
                StockAdjustment existing =
                        adjustmentService.getById(
                                adjustment.getAdjustmentId()
                        );


                adjustment.setBatch(
                        existing.getBatch()
                );


                adjustment.setMaterial(
                        existing.getMaterial()
                );


                adjustment.setPreviousQty(
                        existing.getPreviousQty()
                );


                adjustment.setApprovalStatus(
                        existing.getApprovalStatus()
                );


                adjustment.setApprovedBy(
                        existing.getApprovedBy()
                );


                adjustmentService.updatePending(
                        adjustment
                );


                redirectAttributes
                        .addFlashAttribute(
                                "message",
                                "Pending stock adjustment updated successfully."
                        );

            } else {

                MaterialBatch batch =
                        batchRepository
                                .findById(batchId)
                                .orElseThrow(() ->
                                        new IllegalArgumentException(
                                                "Material batch not found."
                                        )
                                );


                adjustment.setBatch(
                        batch
                );


                adjustmentService.create(
                        adjustment
                );


                redirectAttributes
                        .addFlashAttribute(
                                "message",
                                "Stock adjustment submitted successfully and is waiting for approval."
                        );
            }


            return "redirect:/inventory/adjustments";


        } catch (IllegalArgumentException e) {

            /*
             * Reload relationship information when the
             * form needs to be shown again.
             */
            if (editing) {

                try {

                    StockAdjustment existing =
                            adjustmentService.getById(
                                    adjustment.getAdjustmentId()
                            );


                    adjustment.setBatch(
                            existing.getBatch()
                    );


                    adjustment.setMaterial(
                            existing.getMaterial()
                    );


                    adjustment.setPreviousQty(
                            existing.getPreviousQty()
                    );


                    adjustment.setApprovalStatus(
                            existing.getApprovalStatus()
                    );


                } catch (IllegalArgumentException ignored) {
                    // The main error message will be shown below.
                }
            }


            model.addAttribute(
                    "error",
                    e.getMessage()
            );


            model.addAttribute(
                    "batches",
                    batchRepository
                            .findAllByOrderByReceivedDateDesc()
            );


            model.addAttribute(
                    "isEdit",
                    editing
            );


            return "inventory/adjustment-form";
        }
    }


    // =====================================================
    // APPROVE
    // =====================================================

    @PostMapping("/approve/{id}")
    public String approve(
            @PathVariable Long id,
            @RequestParam Long approvedBy,
            RedirectAttributes redirectAttributes) {

        try {

            adjustmentService.approve(
                    id,
                    approvedBy
            );


            redirectAttributes
                    .addFlashAttribute(
                            "message",
                            "Stock adjustment approved successfully. The batch quantity has been updated."
                    );


        } catch (IllegalArgumentException e) {

            redirectAttributes
                    .addFlashAttribute(
                            "error",
                            e.getMessage()
                    );
        }


        return "redirect:/inventory/adjustments";
    }


    // =====================================================
    // DELETE PENDING ADJUSTMENT
    // =====================================================

    @PostMapping("/delete/{id}")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            adjustmentService.deletePending(
                    id
            );


            redirectAttributes
                    .addFlashAttribute(
                            "message",
                            "Pending stock adjustment deleted successfully."
                    );


        } catch (IllegalArgumentException e) {

            redirectAttributes
                    .addFlashAttribute(
                            "error",
                            e.getMessage()
                    );
        }


        return "redirect:/inventory/adjustments";
    }
}