package com.lankatex.smarttextile.quality.controller;

import com.lankatex.smarttextile.quality.entity.DefectRecord;
import com.lankatex.smarttextile.quality.entity.QualityHold;
import com.lankatex.smarttextile.quality.entity.QualityInspection;
import com.lankatex.smarttextile.quality.entity.ReworkRecord;
import com.lankatex.smarttextile.quality.entity.WastageRecord;
import com.lankatex.smarttextile.quality.service.QualityService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/quality")
public class QualityController {

    private final QualityService service;

    public QualityController(QualityService service) {
        this.service = service;
    }

    // DASHBOARD
    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("stats", service.getDashboardStats());
        model.addAttribute("pendingInspections", service.getPendingInspections());
        model.addAttribute("activeHolds", service.getActiveHolds());
        addReferenceMaps(model);
        return "quality/dashboard";
    }

    // INSPECTIONS
    @GetMapping("/inspections")
    public String inspections(
            @RequestParam(required = false) Long editId,
            Model model) {
        QualityInspection inspection;
        if (editId == null) {
            inspection = new QualityInspection();
            inspection.setInspectionDate(LocalDate.now());
            inspection.setStatus("PENDING");
        } else {
            inspection = service.getInspection(editId);
        }
        model.addAttribute("qualityInspection", inspection);
        model.addAttribute("inspectionsList", service.getAllInspections());
        addReferenceData(model);
        return "quality/inspections";
    }

    @PostMapping("/inspections/save")
    public String saveInspection(
            @ModelAttribute("qualityInspection") QualityInspection inspection,
            RedirectAttributes redirectAttributes) {
        try {
            service.saveInspection(inspection);
            redirectAttributes.addFlashAttribute(
                    "message",
                    "Quality Inspection saved successfully."
            );
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/quality/inspections";
    }

    @PostMapping("/inspections/archive/{id}")
    public String archiveInspection(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        service.archiveInspection(id);
        redirectAttributes.addFlashAttribute(
                "message",
                "Quality Inspection archived successfully."
        );
        return "redirect:/quality/inspections";
    }

    @PostMapping("/inspections/restore/{id}")
    public String restoreInspection(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        service.restoreInspection(id);
        redirectAttributes.addFlashAttribute(
                "message",
                "Quality Inspection restored successfully."
        );
        return "redirect:/quality/inspections";
    }

    // DEFECTS
    @GetMapping("/defects")
    public String defects(
            @RequestParam(required = false) Long editId,
            Model model) {
        model.addAttribute(
                "defectRecord",
                editId == null
                        ? new DefectRecord()
                        : service.getDefect(editId)
        );
        model.addAttribute("defectsList", service.getAllDefects());
        model.addAttribute("inspections", service.getAllInspections());
        addReferenceMaps(model);
        return "quality/defects";
    }

    @PostMapping("/defects/save")
    public String saveDefect(
            @ModelAttribute("defectRecord") DefectRecord defect,
            RedirectAttributes redirectAttributes) {
        try {
            service.saveDefect(defect);
            redirectAttributes.addFlashAttribute(
                    "message",
                    "Defect Record saved successfully."
            );
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/quality/defects";
    }

    @PostMapping("/defects/archive/{id}")
    public String archiveDefect(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        service.archiveDefect(id);
        redirectAttributes.addFlashAttribute(
                "message",
                "Defect Record archived successfully."
        );
        return "redirect:/quality/defects";
    }

    @PostMapping("/defects/restore/{id}")
    public String restoreDefect(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        service.restoreDefect(id);
        redirectAttributes.addFlashAttribute(
                "message",
                "Defect Record restored successfully."
        );
        return "redirect:/quality/defects";
    }

    // REWORK
    @GetMapping("/rework")
    public String rework(
            @RequestParam(required = false) Long editId,
            Model model) {
        model.addAttribute(
                "reworkRecord",
                editId == null
                        ? new ReworkRecord()
                        : service.getRework(editId)
        );
        model.addAttribute("reworkList", service.getAllRework());
        model.addAttribute("inspections", service.getAllInspections());
        model.addAttribute("defects", service.getAllDefects());
        model.addAttribute("employees", service.getActiveEmployees());
        addReferenceMaps(model);
        return "quality/rework";
    }

    @PostMapping("/rework/save")
    public String saveRework(
            @ModelAttribute("reworkRecord") ReworkRecord rework,
            RedirectAttributes redirectAttributes) {
        try {
            service.saveRework(rework);
            redirectAttributes.addFlashAttribute(
                    "message",
                    "Rework Record saved successfully."
            );
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/quality/rework";
    }

    @PostMapping("/rework/verify/{id}")
    public String verifyRework(
            @PathVariable Long id,
            @RequestParam Long verifiedBy,
            @RequestParam String reinspectionResult,
            RedirectAttributes redirectAttributes) {
        try {
            service.verifyRework(id, verifiedBy, reinspectionResult);
            redirectAttributes.addFlashAttribute(
                    "message",
                    "Rework re-inspection recorded successfully."
            );
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/quality/rework";
    }

    @PostMapping("/rework/archive/{id}")
    public String archiveRework(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        service.archiveRework(id);
        redirectAttributes.addFlashAttribute(
                "message",
                "Rework Record archived successfully."
        );
        return "redirect:/quality/rework";
    }

    @PostMapping("/rework/restore/{id}")
    public String restoreRework(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        service.restoreRework(id);
        redirectAttributes.addFlashAttribute(
                "message",
                "Rework Record restored successfully."
        );
        return "redirect:/quality/rework";
    }

    // WASTAGE
    @GetMapping("/wastage")
    public String wastage(
            @RequestParam(required = false) Long editId,
            Model model) {
        model.addAttribute(
                "wastageRecord",
                editId == null
                        ? new WastageRecord()
                        : service.getWastage(editId)
        );
        model.addAttribute("wastageList", service.getAllWastage());
        addReferenceData(model);
        return "quality/wastage";
    }

    @PostMapping("/wastage/save")
    public String saveWastage(
            @ModelAttribute("wastageRecord") WastageRecord wastage,
            RedirectAttributes redirectAttributes) {
        try {
            service.saveWastage(wastage);
            redirectAttributes.addFlashAttribute(
                    "message",
                    "Wastage Record saved successfully."
            );
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/quality/wastage";
    }

    @PostMapping("/wastage/approve/{id}")
    public String approveWastage(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        service.approveWastage(id);
        redirectAttributes.addFlashAttribute(
                "message",
                "Wastage Record approved successfully."
        );
        return "redirect:/quality/wastage";
    }

    @PostMapping("/wastage/reject/{id}")
    public String rejectWastage(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        service.rejectWastage(id);
        redirectAttributes.addFlashAttribute(
                "message",
                "Wastage Record rejected successfully."
        );
        return "redirect:/quality/wastage";
    }

    @PostMapping("/wastage/archive/{id}")
    public String archiveWastage(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        service.archiveWastage(id);
        redirectAttributes.addFlashAttribute(
                "message",
                "Wastage Record archived successfully."
        );
        return "redirect:/quality/wastage";
    }

    @PostMapping("/wastage/restore/{id}")
    public String restoreWastage(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        service.restoreWastage(id);
        redirectAttributes.addFlashAttribute(
                "message",
                "Wastage Record restored successfully."
        );
        return "redirect:/quality/wastage";
    }

    // QUALITY HOLDS
    @GetMapping("/holds")
    public String holds(
            @RequestParam(required = false) Long editId,
            Model model) {
        model.addAttribute(
                "qualityHold",
                editId == null
                        ? new QualityHold()
                        : service.getHold(editId)
        );
        model.addAttribute("holdsList", service.getAllHolds());
        model.addAttribute("inspections", service.getAllInspections());
        model.addAttribute("employees", service.getActiveEmployees());
        addReferenceMaps(model);
        return "quality/holds";
    }

    @PostMapping("/holds/save")
    public String saveHold(
            @ModelAttribute("qualityHold") QualityHold hold,
            RedirectAttributes redirectAttributes) {
        try {
            service.saveHold(hold);
            redirectAttributes.addFlashAttribute(
                    "message",
                    "Quality Hold saved successfully."
            );
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/quality/holds";
    }

    @PostMapping("/holds/release/{id}")
    public String releaseHold(
            @PathVariable Long id,
            @RequestParam Long releasedBy,
            RedirectAttributes redirectAttributes) {
        try {
            service.releaseHold(id, releasedBy);
            redirectAttributes.addFlashAttribute(
                    "message",
                    "Quality Hold released successfully."
            );
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/quality/holds";
    }

    @PostMapping("/holds/archive/{id}")
    public String archiveHold(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        service.archiveHold(id);
        redirectAttributes.addFlashAttribute(
                "message",
                "Quality Hold archived successfully."
        );
        return "redirect:/quality/holds";
    }

    @PostMapping("/holds/restore/{id}")
    public String restoreHold(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        service.restoreHold(id);
        redirectAttributes.addFlashAttribute(
                "message",
                "Quality Hold restored successfully."
        );
        return "redirect:/quality/holds";
    }

    // SHARED MODEL DATA
    private void addReferenceData(Model model) {
        model.addAttribute("productionPlans", service.getProductionPlans());
        model.addAttribute("customerOrders", service.getCustomerOrders());
        model.addAttribute("employees", service.getActiveEmployees());
        model.addAttribute("materials", service.getActiveMaterials());
        addReferenceMaps(model);
    }

    private void addReferenceMaps(Model model) {
        model.addAttribute("productionPlanMap", service.getProductionPlanMap());
        model.addAttribute("customerOrderMap", service.getCustomerOrderMap());
        model.addAttribute("employeeMap", service.getEmployeeMap());
        model.addAttribute("materialMap", service.getMaterialMap());
        model.addAttribute("inspectionMap", service.getInspectionMap());
        model.addAttribute("defectMap", service.getDefectMap());
    }
}