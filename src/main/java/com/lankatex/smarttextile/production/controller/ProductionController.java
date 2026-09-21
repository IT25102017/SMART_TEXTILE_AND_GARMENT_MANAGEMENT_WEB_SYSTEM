package com.lankatex.smarttextile.production.controller;

import com.lankatex.smarttextile.production.entity.MaterialIssueNote;
import com.lankatex.smarttextile.production.entity.MaterialRequest;
import com.lankatex.smarttextile.production.entity.ProductionPlan;
import com.lankatex.smarttextile.production.entity.ProductionProgress;
import com.lankatex.smarttextile.production.service.ProductionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/production")
public class ProductionController {

    private final ProductionService service;

    public ProductionController(
            ProductionService service) {

        this.service = service;
    }


    // =========================================================
    // DASHBOARD
    // =========================================================

    @GetMapping
    public String dashboard(Model model) {

        model.addAttribute(
                "stats",
                service.getDashboardStats()
        );

        return "production/dashboard";
    }


    // =========================================================
    // PRODUCTION PLAN
    // =========================================================

    @GetMapping("/plans")
    public String plans(
            @RequestParam(required = false) Long editId,
            Model model) {

        model.addAttribute(
                "productionPlan",
                editId == null
                        ? new ProductionPlan()
                        : service.getProductionPlan(editId)
        );

        model.addAttribute(
                "plansList",
                service.getAllProductionPlans()
        );

        return "production/plans";
    }


    @PostMapping("/plans/save")
    public String saveProductionPlan(
            @ModelAttribute ProductionPlan productionPlan,
            RedirectAttributes redirectAttributes) {

        try {

            service.saveProductionPlan(
                    productionPlan
            );

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Production Plan saved successfully."
            );

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage()
            );
        }

        return "redirect:/production/plans";
    }


    @GetMapping("/plans/archive/{id}")
    public String archiveProductionPlan(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            service.archiveProductionPlan(id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Production Plan archived."
            );

        } catch (Exception ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Cannot archive Production Plan."
            );
        }

        return "redirect:/production/plans";
    }


    @GetMapping("/plans/restore/{id}")
    public String restoreProductionPlan(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            service.restoreProductionPlan(id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Production Plan restored successfully."
            );

        } catch (Exception ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Cannot restore Production Plan."
            );
        }

        return "redirect:/production/plans";
    }


    @GetMapping("/plans/delete/{id}")
    public String deleteProductionPlan(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            service.deleteProductionPlan(id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Production Plan permanently deleted."
            );

        } catch (Exception ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Cannot delete Production Plan. It may already be used by another production record."
            );
        }

        return "redirect:/production/plans";
    }


    // =========================================================
    // MATERIAL REQUEST
    // =========================================================

    @GetMapping("/requests")
    public String requests(
            @RequestParam(required = false) Long editId,
            Model model) {

        model.addAttribute(
                "materialRequest",
                editId == null
                        ? new MaterialRequest()
                        : service.getMaterialRequest(editId)
        );

        model.addAttribute(
                "requestsList",
                service.getAllMaterialRequests()
        );

        return "production/requests";
    }


    @PostMapping("/requests/save")
    public String saveMaterialRequest(
            @ModelAttribute MaterialRequest materialRequest,
            RedirectAttributes redirectAttributes) {

        try {

            service.saveMaterialRequest(
                    materialRequest
            );

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Material Request saved successfully."
            );

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage()
            );
        }

        return "redirect:/production/requests";
    }


    @GetMapping("/requests/archive/{id}")
    public String archiveMaterialRequest(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            service.archiveMaterialRequest(id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Material Request archived."
            );

        } catch (Exception ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Cannot archive Material Request."
            );
        }

        return "redirect:/production/requests";
    }


    @GetMapping("/requests/restore/{id}")
    public String restoreMaterialRequest(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            service.restoreMaterialRequest(id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Material Request restored successfully."
            );

        } catch (Exception ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Cannot restore Material Request."
            );
        }

        return "redirect:/production/requests";
    }


    @GetMapping("/requests/delete/{id}")
    public String deleteMaterialRequest(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            service.deleteMaterialRequest(id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Material Request permanently deleted."
            );

        } catch (Exception ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Cannot delete Material Request. It may already be used by a Material Issue Note."
            );
        }

        return "redirect:/production/requests";
    }


    // =========================================================
    // MATERIAL ISSUE NOTE
    // =========================================================

    @GetMapping("/issues")
    public String issues(
            @RequestParam(required = false) Long editId,
            Model model) {

        model.addAttribute(
                "materialIssueNote",
                editId == null
                        ? new MaterialIssueNote()
                        : service.getMaterialIssueNote(editId)
        );

        model.addAttribute(
                "issuesList",
                service.getAllMaterialIssueNotes()
        );

        return "production/issues";
    }


    @PostMapping("/issues/save")
    public String saveMaterialIssueNote(
            @ModelAttribute MaterialIssueNote materialIssueNote,
            RedirectAttributes redirectAttributes) {

        try {

            service.saveMaterialIssueNote(
                    materialIssueNote
            );

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Material Issue Note saved successfully."
            );

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage()
            );
        }

        return "redirect:/production/issues";
    }


    @GetMapping("/issues/delete/{id}")
    public String deleteMaterialIssueNote(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            service.deleteMaterialIssueNote(id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Material Issue Note permanently deleted."
            );

        } catch (Exception ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Cannot delete Material Issue Note."
            );
        }

        return "redirect:/production/issues";
    }


    // =========================================================
    // PRODUCTION PROGRESS
    // =========================================================

    @GetMapping("/progress")
    public String progress(
            @RequestParam(required = false) Long editId,
            Model model) {

        model.addAttribute(
                "productionProgress",
                editId == null
                        ? new ProductionProgress()
                        : service.getProductionProgress(editId)
        );

        model.addAttribute(
                "progressList",
                service.getAllProductionProgresss()
        );

        return "production/progress";
    }


    @PostMapping("/progress/save")
    public String saveProductionProgress(
            @ModelAttribute ProductionProgress productionProgress,
            RedirectAttributes redirectAttributes) {

        try {

            service.saveProductionProgress(
                    productionProgress
            );

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Production Progress saved successfully."
            );

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage()
            );
        }

        return "redirect:/production/progress";
    }


    @GetMapping("/progress/delete/{id}")
    public String deleteProductionProgress(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            service.deleteProductionProgress(id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Production Progress permanently deleted."
            );

        } catch (Exception ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Cannot delete Production Progress."
            );
        }

        return "redirect:/production/progress";
    }
}