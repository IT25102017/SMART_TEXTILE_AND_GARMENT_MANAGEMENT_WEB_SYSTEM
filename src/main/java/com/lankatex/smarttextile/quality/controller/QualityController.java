package com.lankatex.smarttextile.quality.controller;

import com.lankatex.smarttextile.quality.entity.DefectRecord;
import com.lankatex.smarttextile.quality.entity.QualityHold;
import com.lankatex.smarttextile.quality.entity.QualityInspection;
import com.lankatex.smarttextile.quality.entity.WastageRecord;
import com.lankatex.smarttextile.quality.service.QualityService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/quality")
public class QualityController {

    private final QualityService service;

    public QualityController(
            QualityService service) {

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

        return "quality/dashboard";
    }


    // =========================================================
    // QUALITY INSPECTION
    // =========================================================

    @GetMapping("/inspections")
    public String inspections(
            @RequestParam(required = false)
            Long editId,
            Model model) {

        model.addAttribute(
                "qualityInspection",
                editId == null
                        ? new QualityInspection()
                        : service.getQualityInspection(editId)
        );

        model.addAttribute(
                "inspectionsList",
                service.getAllQualityInspections()
        );

        return "quality/inspections";
    }

    @PostMapping("/inspections/save")
    public String saveQualityInspection(
            @ModelAttribute
            QualityInspection qualityInspection,
            RedirectAttributes redirectAttributes) {

        try {

            service.saveQualityInspection(
                    qualityInspection
            );

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Quality Inspection saved successfully."
            );

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage()
            );
        }

        return "redirect:/quality/inspections";
    }

    // SOFT DELETE
    @GetMapping("/inspections/archive/{id}")
    public String archiveQualityInspection(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            service.archiveQualityInspection(id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Quality Inspection archived."
            );

        } catch (Exception ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Cannot archive Quality Inspection."
            );
        }

        return "redirect:/quality/inspections";
    }

    // HARD DELETE
    @GetMapping("/inspections/delete/{id}")
    public String deleteQualityInspection(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            service.deleteQualityInspection(id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Quality Inspection permanently deleted."
            );

        } catch (Exception ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Cannot delete Quality Inspection."
            );
        }

        return "redirect:/quality/inspections";
    }


    // =========================================================
    // DEFECT RECORD
    // =========================================================

    @GetMapping("/defects")
    public String defects(
            @RequestParam(required = false)
            Long editId,
            Model model) {

        model.addAttribute(
                "defectRecord",
                editId == null
                        ? new DefectRecord()
                        : service.getDefectRecord(editId)
        );

        model.addAttribute(
                "defectsList",
                service.getAllDefectRecords()
        );

        return "quality/defects";
    }

    @PostMapping("/defects/save")
    public String saveDefectRecord(
            @ModelAttribute
            DefectRecord defectRecord,
            RedirectAttributes redirectAttributes) {

        try {

            service.saveDefectRecord(
                    defectRecord
            );

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Defect Record saved successfully."
            );

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage()
            );
        }

        return "redirect:/quality/defects";
    }

    // HARD DELETE
    @GetMapping("/defects/delete/{id}")
    public String deleteDefectRecord(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            service.deleteDefectRecord(id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Defect Record permanently deleted."
            );

        } catch (Exception ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Cannot delete Defect Record."
            );
        }

        return "redirect:/quality/defects";
    }


    // =========================================================
    // WASTAGE RECORD
    // =========================================================

    @GetMapping("/wastage")
    public String wastage(
            @RequestParam(required = false)
            Long editId,
            Model model) {

        model.addAttribute(
                "wastageRecord",
                editId == null
                        ? new WastageRecord()
                        : service.getWastageRecord(editId)
        );

        model.addAttribute(
                "wastageList",
                service.getAllWastageRecords()
        );

        return "quality/wastage";
    }

    @PostMapping("/wastage/save")
    public String saveWastageRecord(
            @ModelAttribute
            WastageRecord wastageRecord,
            RedirectAttributes redirectAttributes) {

        try {

            service.saveWastageRecord(
                    wastageRecord
            );

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Wastage Record saved successfully."
            );

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage()
            );
        }

        return "redirect:/quality/wastage";
    }

    // SOFT DELETE
    @GetMapping("/wastage/archive/{id}")
    public String archiveWastageRecord(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            service.archiveWastageRecord(id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Wastage Record archived."
            );

        } catch (Exception ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Cannot archive Wastage Record."
            );
        }

        return "redirect:/quality/wastage";
    }

    // HARD DELETE
    @GetMapping("/wastage/delete/{id}")
    public String deleteWastageRecord(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            service.deleteWastageRecord(id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Wastage Record permanently deleted."
            );

        } catch (Exception ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Cannot delete Wastage Record."
            );
        }

        return "redirect:/quality/wastage";
    }


    // =========================================================
    // QUALITY HOLD
    // =========================================================

    @GetMapping("/holds")
    public String holds(
            @RequestParam(required = false)
            Long editId,
            Model model) {

        model.addAttribute(
                "qualityHold",
                editId == null
                        ? new QualityHold()
                        : service.getQualityHold(editId)
        );

        model.addAttribute(
                "holdsList",
                service.getAllQualityHolds()
        );

        return "quality/holds";
    }

    @PostMapping("/holds/save")
    public String saveQualityHold(
            @ModelAttribute
            QualityHold qualityHold,
            RedirectAttributes redirectAttributes) {

        try {

            service.saveQualityHold(
                    qualityHold
            );

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Quality Hold saved successfully."
            );

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage()
            );
        }

        return "redirect:/quality/holds";
    }

    // SOFT DELETE
    @GetMapping("/holds/archive/{id}")
    public String archiveQualityHold(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            service.archiveQualityHold(id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Quality Hold archived."
            );

        } catch (Exception ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Cannot archive Quality Hold."
            );
        }

        return "redirect:/quality/holds";
    }

    // HARD DELETE
    @GetMapping("/holds/delete/{id}")
    public String deleteQualityHold(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            service.deleteQualityHold(id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Quality Hold permanently deleted."
            );

        } catch (Exception ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Cannot delete Quality Hold."
            );
        }

        return "redirect:/quality/holds";
    }
}