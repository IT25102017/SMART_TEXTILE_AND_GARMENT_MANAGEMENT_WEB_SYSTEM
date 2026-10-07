package com.lankatex.smarttextile.production.controller;

import com.lankatex.smarttextile.production.entity.MaterialIssueNote;
import com.lankatex.smarttextile.production.entity.MaterialRequest;
import com.lankatex.smarttextile.production.entity.ProductionPlan;
import com.lankatex.smarttextile.production.entity.ProductionProgress;
import com.lankatex.smarttextile.production.service.ProductionService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/production")
public class ProductionController {

    private final ProductionService service;

    public ProductionController(ProductionService service) {
        this.service = service;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("stats", service.getDashboardStats());
        model.addAttribute("plans", service.getAllProductionPlans());
        model.addAttribute("completionMap", service.getCompletionPercentageMap());
        model.addAttribute("orderMap", service.getCustomerOrderMap());
        return "production/dashboard";
    }

    // =====================================================
    // PRODUCTION PLANS
    // =====================================================

    @GetMapping("/plans")
    public String plans(@RequestParam(required = false) Long editId, Model model) {
        ProductionPlan plan = editId == null ? new ProductionPlan() : service.getProductionPlan(editId);
        if (editId == null) {
            plan.setStartDate(LocalDate.now());
        }
        preparePlanPage(model, plan);
        return "production/plans";
    }

    @PostMapping("/plans/save")
    public String savePlan(
            @Valid @ModelAttribute("productionPlan") ProductionPlan plan,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            preparePlanPage(model, plan);
            return "production/plans";
        }

        try {
            service.saveProductionPlan(plan);
            redirectAttributes.addFlashAttribute("message", "Production Plan saved successfully.");
            return "redirect:/production/plans";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            preparePlanPage(model, plan);
            return "production/plans";
        }
    }

    @PostMapping("/plans/approve/{id}")
    public String approvePlan(@PathVariable Long id, RedirectAttributes ra) {
        return runAction(() -> service.approveProductionPlan(id), "Production Plan approved.", "/production/plans", ra);
    }

    @PostMapping("/plans/reject/{id}")
    public String rejectPlan(@PathVariable Long id, RedirectAttributes ra) {
        return runAction(() -> service.rejectProductionPlan(id), "Production Plan rejected.", "/production/plans", ra);
    }

    @PostMapping("/plans/archive/{id}")
    public String archivePlan(@PathVariable Long id, RedirectAttributes ra) {
        return runAction(() -> service.archiveProductionPlan(id), "Production Plan archived.", "/production/plans", ra);
    }

    @PostMapping("/plans/restore/{id}")
    public String restorePlan(@PathVariable Long id, RedirectAttributes ra) {
        return runAction(() -> service.restoreProductionPlan(id), "Production Plan restored to PENDING.", "/production/plans", ra);
    }

    @PostMapping("/plans/delete/{id}")
    public String deletePlan(@PathVariable Long id, RedirectAttributes ra) {
        return runAction(() -> service.deleteProductionPlan(id), "Production Plan permanently deleted.", "/production/plans", ra);
    }

    // =====================================================
    // MATERIAL REQUESTS
    // =====================================================

    @GetMapping("/requests")
    public String requests(@RequestParam(required = false) Long editId, Model model) {
        MaterialRequest request = editId == null ? new MaterialRequest() : service.getMaterialRequest(editId);
        if (editId == null) request.setRequestDate(LocalDate.now());
        prepareRequestPage(model, request);
        return "production/requests";
    }

    @PostMapping("/requests/save")
    public String saveRequest(
            @Valid @ModelAttribute("materialRequest") MaterialRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes ra) {

        if (result.hasErrors()) {
            prepareRequestPage(model, request);
            return "production/requests";
        }

        try {
            service.saveMaterialRequest(request);
            ra.addFlashAttribute("message", "Material Request saved successfully.");
            return "redirect:/production/requests";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            prepareRequestPage(model, request);
            return "production/requests";
        }
    }

    @PostMapping("/requests/approve/{id}")
    public String approveRequest(@PathVariable Long id, @RequestParam Long approvedBy, RedirectAttributes ra) {
        return runAction(() -> service.approveMaterialRequest(id, approvedBy), "Material Request approved.", "/production/requests", ra);
    }

    @PostMapping("/requests/reject/{id}")
    public String rejectRequest(@PathVariable Long id, @RequestParam(required = false) Long approvedBy, RedirectAttributes ra) {
        return runAction(() -> service.rejectMaterialRequest(id, approvedBy), "Material Request rejected.", "/production/requests", ra);
    }

    @PostMapping("/requests/archive/{id}")
    public String archiveRequest(@PathVariable Long id, RedirectAttributes ra) {
        return runAction(() -> service.archiveMaterialRequest(id), "Material Request archived.", "/production/requests", ra);
    }

    @PostMapping("/requests/delete/{id}")
    public String deleteRequest(@PathVariable Long id, RedirectAttributes ra) {
        return runAction(() -> service.deleteMaterialRequest(id), "Material Request permanently deleted.", "/production/requests", ra);
    }

    // =====================================================
    // MATERIAL ISSUE NOTES
    // =====================================================

    @GetMapping("/issues")
    public String issues(Model model) {
        MaterialIssueNote issue = new MaterialIssueNote();
        issue.setIssueDate(LocalDate.now());
        prepareIssuePage(model, issue);
        return "production/issues";
    }

    @PostMapping("/issues/save")
    public String saveIssue(
            @Valid @ModelAttribute("materialIssueNote") MaterialIssueNote issue,
            BindingResult result,
            Model model,
            RedirectAttributes ra) {

        if (result.hasErrors()) {
            prepareIssuePage(model, issue);
            return "production/issues";
        }

        try {
            service.createMaterialIssueNote(issue);
            ra.addFlashAttribute("message", "Material issued successfully and Inventory stock was updated.");
            return "redirect:/production/issues";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            prepareIssuePage(model, issue);
            return "production/issues";
        }
    }

    // =====================================================
    // PRODUCTION PROGRESS
    // =====================================================

    @GetMapping("/progress")
    public String progress(@RequestParam(required = false) Long editId, Model model) {
        ProductionProgress row = editId == null ? new ProductionProgress() : service.getProductionProgress(editId);
        if (editId == null) row.setProgressDate(LocalDate.now());
        prepareProgressPage(model, row);
        return "production/progress";
    }

    @PostMapping("/progress/save")
    public String saveProgress(
            @Valid @ModelAttribute("productionProgress") ProductionProgress row,
            BindingResult result,
            Model model,
            RedirectAttributes ra) {

        if (result.hasErrors()) {
            prepareProgressPage(model, row);
            return "production/progress";
        }

        try {
            service.saveProductionProgress(row);
            ra.addFlashAttribute("message", "Production Progress saved successfully.");
            return "redirect:/production/progress";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            prepareProgressPage(model, row);
            return "production/progress";
        }
    }

    @PostMapping("/progress/delete/{id}")
    public String deleteProgress(@PathVariable Long id, RedirectAttributes ra) {
        return runAction(() -> service.deleteProductionProgress(id), "Production Progress deleted.", "/production/progress", ra);
    }

    // =====================================================
    // PAGE PREPARATION
    // =====================================================

    private void preparePlanPage(Model model, ProductionPlan plan) {
        model.addAttribute("productionPlan", plan);
        model.addAttribute("plansList", service.getAllProductionPlans());
        model.addAttribute("orders", service.getApprovedCustomerOrders());
        model.addAttribute("employees", service.getActiveEmployees());
        model.addAttribute("orderMap", service.getCustomerOrderMap());
        model.addAttribute("employeeMap", service.getEmployeeMap());
        model.addAttribute("completionMap", service.getCompletionPercentageMap());
    }

    private void prepareRequestPage(Model model, MaterialRequest request) {
        model.addAttribute("materialRequest", request);
        model.addAttribute("requestsList", service.getAllMaterialRequests());
        model.addAttribute("plans", service.getApprovedOrActivePlans());
        model.addAttribute("planMap", service.getProductionPlanMap());
    }

    private void prepareIssuePage(Model model, MaterialIssueNote issue) {
        model.addAttribute("materialIssueNote", issue);
        model.addAttribute("issuesList", service.getAllMaterialIssueNotes());
        model.addAttribute("requests", service.getApprovedMaterialRequests());
        model.addAttribute("plans", service.getApprovedOrActivePlans());
        model.addAttribute("materials", service.getActiveMaterials());
        model.addAttribute("batches", service.getAvailableBatches());
        model.addAttribute("requestMap", service.getMaterialRequestMap());
        model.addAttribute("planMap", service.getProductionPlanMap());
        model.addAttribute("materialMap", service.getMaterialMap());
        model.addAttribute("batchMap", service.getBatchMap());
    }

    private void prepareProgressPage(Model model, ProductionProgress row) {
        model.addAttribute("productionProgress", row);
        model.addAttribute("progressList", service.getAllProductionProgress());
        model.addAttribute("plans", service.getApprovedOrActivePlans());
        model.addAttribute("planMap", service.getProductionPlanMap());
        model.addAttribute("completionMap", service.getCompletionPercentageMap());
    }

    private String runAction(Runnable action, String successMessage, String redirect, RedirectAttributes ra) {
        try {
            action.run();
            ra.addFlashAttribute("message", successMessage);
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:" + redirect;
    }
}