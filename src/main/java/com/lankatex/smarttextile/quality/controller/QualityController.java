package com.lankatex.smarttextile.quality.controller;

import com.lankatex.smarttextile.quality.service.QualityService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.lankatex.smarttextile.quality.entity.QualityInspection;
import com.lankatex.smarttextile.quality.entity.DefectRecord;
import com.lankatex.smarttextile.quality.entity.WastageRecord;
import com.lankatex.smarttextile.quality.entity.QualityHold;
@Controller
@RequestMapping("/quality")
public class QualityController {
    private final QualityService service;
    public QualityController(QualityService service) {
        this.service = service;
    }
    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("stats", service.getDashboardStats());
        return "quality/dashboard";
    }
    @GetMapping("/inspections")
    public String inspections(@RequestParam(required = false) Long editId, Model model) {
        model.addAttribute("qualityInspection", editId == null ? new QualityInspection() : service.getQualityInspection(editId));
        model.addAttribute("inspectionsList", service.getAllQualityInspections());
        return "quality/inspections";
    }
    @PostMapping("/inspections/save")
    public String saveQualityInspection(@ModelAttribute QualityInspection qualityInspection, RedirectAttributes
            redirectAttributes) {
        try {
            service.saveQualityInspection(qualityInspection);
            redirectAttributes.addFlashAttribute("message", "Quality Inspection saved successfully.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/quality/inspections";
    }
    @GetMapping("/inspections/archive/{id}")
    public String archiveQualityInspection(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.archiveQualityInspection(id);
        redirectAttributes.addFlashAttribute("message", "Quality Inspection archived.");
        return "redirect:/quality/inspections";
    }
    @GetMapping("/defects")
    public String defects(@RequestParam(required = false) Long editId, Model model) {
        model.addAttribute("defectRecord", editId == null ? new DefectRecord() : service.getDefectRecord(editId));
        model.addAttribute("defectsList", service.getAllDefectRecords());
        return "quality/defects";
    }
    @PostMapping("/defects/save")
    public String saveDefectRecord(@ModelAttribute DefectRecord defectRecord, RedirectAttributes redirectAttributes) {
        try {
            service.saveDefectRecord(defectRecord);
            redirectAttributes.addFlashAttribute("message", "Defect Record saved successfully.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/quality/defects";
    }
    @GetMapping("/defects/delete/{id}")
    public String deleteDefectRecord(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.deleteDefectRecord(id);
        redirectAttributes.addFlashAttribute("message", "Defect Record deleted.");
        return "redirect:/quality/defects";
    }
    @GetMapping("/wastage")
    public String wastage(@RequestParam(required = false) Long editId, Model model) {
        model.addAttribute("wastageRecord", editId == null ? new WastageRecord() : service.getWastageRecord(editId));
        model.addAttribute("wastageList", service.getAllWastageRecords());
        return "quality/wastage";
    }
    @PostMapping("/wastage/save")
    public String saveWastageRecord(@ModelAttribute WastageRecord wastageRecord, RedirectAttributes redirectAttributes) {
        try {
            service.saveWastageRecord(wastageRecord);
            redirectAttributes.addFlashAttribute("message", "Wastage Record saved successfully.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/quality/wastage";
    }
    @GetMapping("/wastage/archive/{id}")
    public String archiveWastageRecord(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.archiveWastageRecord(id);
        redirectAttributes.addFlashAttribute("message", "Wastage Record archived.");
        return "redirect:/quality/wastage";
    }
    @GetMapping("/holds")
    public String holds(@RequestParam(required = false) Long editId, Model model) {
        model.addAttribute("qualityHold", editId == null ? new QualityHold() : service.getQualityHold(editId));
        model.addAttribute("holdsList", service.getAllQualityHolds());
        return "quality/holds";
    }
    @PostMapping("/holds/save")
    public String saveQualityHold(@ModelAttribute QualityHold qualityHold, RedirectAttributes redirectAttributes) {
        try {
            service.saveQualityHold(qualityHold);
            redirectAttributes.addFlashAttribute("message", "Quality Hold saved successfully.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/quality/holds";
    }
    @GetMapping("/holds/archive/{id}")
    public String archiveQualityHold(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.archiveQualityHold(id);
        redirectAttributes.addFlashAttribute("message", "Quality Hold archived.");
        return "redirect:/quality/holds";
    }
}