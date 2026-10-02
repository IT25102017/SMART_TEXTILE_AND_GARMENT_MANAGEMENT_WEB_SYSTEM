package com.lankatex.smarttextile.quality.service;

import com.lankatex.smarttextile.customer.entity.CustomerOrder;
import com.lankatex.smarttextile.customer.repository.CustomerOrderRepository;
import com.lankatex.smarttextile.hr.entity.Employee;
import com.lankatex.smarttextile.hr.repository.EmployeeRepository;
import com.lankatex.smarttextile.inventory.entity.Material;
import com.lankatex.smarttextile.inventory.repository.MaterialRepository;
import com.lankatex.smarttextile.production.entity.ProductionPlan;
import com.lankatex.smarttextile.production.repository.ProductionPlanRepository;
import com.lankatex.smarttextile.quality.dto.QualityDashboardStats;
import com.lankatex.smarttextile.quality.entity.DefectRecord;
import com.lankatex.smarttextile.quality.entity.QualityHold;
import com.lankatex.smarttextile.quality.entity.QualityInspection;
import com.lankatex.smarttextile.quality.entity.ReworkRecord;
import com.lankatex.smarttextile.quality.entity.WastageRecord;
import com.lankatex.smarttextile.quality.repository.DefectRecordRepository;
import com.lankatex.smarttextile.quality.repository.QualityHoldRepository;
import com.lankatex.smarttextile.quality.repository.QualityInspectionRepository;
import com.lankatex.smarttextile.quality.repository.ReworkRecordRepository;
import com.lankatex.smarttextile.quality.repository.WastageRecordRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class QualityService {

    private final QualityInspectionRepository inspectionRepository;
    private final DefectRecordRepository defectRepository;
    private final ReworkRecordRepository reworkRepository;
    private final WastageRecordRepository wastageRepository;
    private final QualityHoldRepository holdRepository;

    private final ProductionPlanRepository productionPlanRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final EmployeeRepository employeeRepository;
    private final MaterialRepository materialRepository;

    public QualityService(
            QualityInspectionRepository inspectionRepository,
            DefectRecordRepository defectRepository,
            ReworkRecordRepository reworkRepository,
            WastageRecordRepository wastageRepository,
            QualityHoldRepository holdRepository,
            ProductionPlanRepository productionPlanRepository,
            CustomerOrderRepository customerOrderRepository,
            EmployeeRepository employeeRepository,
            MaterialRepository materialRepository) {
        this.inspectionRepository = inspectionRepository;
        this.defectRepository = defectRepository;
        this.reworkRepository = reworkRepository;
        this.wastageRepository = wastageRepository;
        this.holdRepository = holdRepository;
        this.productionPlanRepository = productionPlanRepository;
        this.customerOrderRepository = customerOrderRepository;
        this.employeeRepository = employeeRepository;
        this.materialRepository = materialRepository;
    }

    // DASHBOARD STATS
    public QualityDashboardStats getDashboardStats() {
        List<QualityInspection> inspections = inspectionRepository.findAll();

        long inspectionCount = inspections.stream()
                .filter(row -> !isArchived(row.getStatus()))
                .count();

        long pendingInspectionCount = inspections.stream()
                .filter(row -> "PENDING".equalsIgnoreCase(row.getStatus()))
                .count();

        long defectCount = defectRepository.findAll().stream()
                .filter(row -> !isArchived(row.getStatus()))
                .count();

        long pendingReworkCount = reworkRepository.findAll().stream()
                .filter(row -> "PENDING".equalsIgnoreCase(row.getStatus())
                        || "IN_PROGRESS".equalsIgnoreCase(row.getStatus()))
                .count();

        long wastageRecordCount = wastageRepository.findAll().stream()
                .filter(row -> !isArchived(row.getApprovalStatus()))
                .count();

        long activeHoldCount = holdRepository.findAll().stream()
                .filter(row -> "ACTIVE".equalsIgnoreCase(row.getStatus()))
                .count();

        BigDecimal totalInspected = inspections.stream()
                .filter(row -> !isArchived(row.getStatus()))
                .map(row -> valueOrZero(row.getInspectedQty()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRejected = inspections.stream()
                .filter(row -> !isArchived(row.getStatus()))
                .map(row -> valueOrZero(row.getRejectedQty()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        double rejectionRate = 0.0;
        if (totalInspected.signum() > 0) {
            rejectionRate = totalRejected
                    .multiply(BigDecimal.valueOf(100))
                    .divide(totalInspected, 2, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        return new QualityDashboardStats(
                inspectionCount,
                pendingInspectionCount,
                defectCount,
                pendingReworkCount,
                wastageRecordCount,
                activeHoldCount,
                rejectionRate
        );
    }

    public List<QualityInspection> getPendingInspections() {
        return getAllInspections().stream()
                .filter(row -> "PENDING".equalsIgnoreCase(row.getStatus())
                        || "ON_HOLD".equalsIgnoreCase(row.getStatus())
                        || "REWORK_REQUIRED".equalsIgnoreCase(row.getStatus()))
                .toList();
    }

    public List<QualityHold> getActiveHolds() {
        return getAllHolds().stream()
                .filter(row -> "ACTIVE".equalsIgnoreCase(row.getStatus()))
                .toList();
    }

    // SHARED REFERENCE DATA
    public List<ProductionPlan> getProductionPlans() {
        return productionPlanRepository.findAll(
                Sort.by(Sort.Direction.DESC, "planId")
        );
    }

    public List<CustomerOrder> getCustomerOrders() {
        return customerOrderRepository.findAll(
                Sort.by(Sort.Direction.DESC, "orderId")
        );
    }

    public List<Employee> getActiveEmployees() {
        return employeeRepository.findAll(
                        Sort.by(Sort.Direction.ASC, "fullName")
                ).stream()
                .filter(employee -> employee.getStatus() == null
                        || !"ARCHIVED".equalsIgnoreCase(employee.getStatus()))
                .toList();
    }

    public List<Material> getActiveMaterials() {
        return materialRepository.findAll(
                        Sort.by(Sort.Direction.ASC, "materialName")
                ).stream()
                .filter(material -> material.getStatus() == null
                        || !"ARCHIVED".equalsIgnoreCase(material.getStatus()))
                .toList();
    }

    public Map<Long, ProductionPlan> getProductionPlanMap() {
        return productionPlanRepository.findAll().stream()
                .collect(Collectors.toMap(
                        ProductionPlan::getPlanId,
                        Function.identity()
                ));
    }

    public Map<Long, CustomerOrder> getCustomerOrderMap() {
        return customerOrderRepository.findAll().stream()
                .collect(Collectors.toMap(
                        CustomerOrder::getOrderId,
                        Function.identity()
                ));
    }

    public Map<Long, Employee> getEmployeeMap() {
        return employeeRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Employee::getEmployeeId,
                        Function.identity()
                ));
    }

    public Map<Long, Material> getMaterialMap() {
        return materialRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Material::getMaterialId,
                        Function.identity()
                ));
    }

    public Map<Long, QualityInspection> getInspectionMap() {
        return inspectionRepository.findAll().stream()
                .collect(Collectors.toMap(
                        QualityInspection::getInspectionId,
                        Function.identity()
                ));
    }

    public Map<Long, DefectRecord> getDefectMap() {
        return defectRepository.findAll().stream()
                .collect(Collectors.toMap(
                        DefectRecord::getDefectId,
                        Function.identity()
                ));
    }

    // QUALITY INSPECTIONS
    public List<QualityInspection> getAllInspections() {
        return inspectionRepository.findAllByOrderByInspectionDateDesc();
    }

    public QualityInspection getInspection(Long id) {
        return inspectionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Quality Inspection not found."
                ));
    }

    @Transactional
    public QualityInspection saveInspection(QualityInspection inspection) {
        validatePlanAndOrder(
                inspection.getPlanId(),
                inspection.getOrderId()
        );
        validateEmployee(
                inspection.getInspectorId(),
                "Inspector"
        );

        if (inspection.getStageName() == null || inspection.getStageName().isBlank()) {
            throw new IllegalArgumentException("Stage name is required.");
        }

        if (inspection.getInspectionDate() == null) {
            inspection.setInspectionDate(LocalDate.now());
        }

        BigDecimal inspected = valueOrZero(inspection.getInspectedQty());
        BigDecimal accepted = valueOrZero(inspection.getAcceptedQty());
        BigDecimal rejected = valueOrZero(inspection.getRejectedQty());
        BigDecimal pending = valueOrZero(inspection.getPendingQty());

        if (inspected.signum() < 0
                || accepted.signum() < 0
                || rejected.signum() < 0
                || pending.signum() < 0) {
            throw new IllegalArgumentException(
                    "Inspection quantities cannot be negative."
            );
        }

        if (accepted.add(rejected).add(pending).compareTo(inspected) != 0) {
            throw new IllegalArgumentException(
                    "Accepted + Rejected + Pending quantity must equal Inspected quantity."
            );
        }

        inspection.setInspectedQty(inspected);
        inspection.setAcceptedQty(accepted);
        inspection.setRejectedQty(rejected);
        inspection.setPendingQty(pending);

        if (inspection.getStatus() == null || inspection.getStatus().isBlank()) {
            inspection.setStatus("PENDING");
        }

        return inspectionRepository.save(inspection);
    }

    @Transactional
    public void archiveInspection(Long id) {
        QualityInspection inspection = getInspection(id);
        inspection.setStatus("ARCHIVED");
        inspectionRepository.save(inspection);
    }

    @Transactional
    public void restoreInspection(Long id) {
        QualityInspection inspection = getInspection(id);
        inspection.setStatus("PENDING");
        inspectionRepository.save(inspection);
    }

    // DEFECT RECORDS
    public List<DefectRecord> getAllDefects() {
        return defectRepository.findAllByOrderByDefectIdDesc();
    }

    public DefectRecord getDefect(Long id) {
        return defectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Defect Record not found."
                ));
    }

    @Transactional
    public DefectRecord saveDefect(DefectRecord defect) {
        if (defect.getInspectionId() == null) {
            throw new IllegalArgumentException("Inspection is required.");
        }
        getInspection(defect.getInspectionId());

        if (defect.getDefectType() == null || defect.getDefectType().isBlank()) {
            throw new IllegalArgumentException("Defect type is required.");
        }
        if (defect.getSeverity() == null || defect.getSeverity().isBlank()) {
            throw new IllegalArgumentException("Severity is required.");
        }
        if (defect.getQuantity() == null || defect.getQuantity() <= 0) {
            throw new IllegalArgumentException(
                    "Defect quantity must be greater than zero."
            );
        }

        if (defect.getStatus() == null || defect.getStatus().isBlank()) {
            defect.setStatus("ACTIVE");
        }

        return defectRepository.save(defect);
    }

    @Transactional
    public void archiveDefect(Long id) {
        DefectRecord defect = getDefect(id);
        defect.setStatus("ARCHIVED");
        defectRepository.save(defect);
    }

    @Transactional
    public void restoreDefect(Long id) {
        DefectRecord defect = getDefect(id);
        defect.setStatus("ACTIVE");
        defectRepository.save(defect);
    }

    // REWORK RECORDS
    public List<ReworkRecord> getAllRework() {
        return reworkRepository.findAllByOrderByReworkIdDesc();
    }

    public ReworkRecord getRework(Long id) {
        return reworkRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Rework Record not found."
                ));
    }

    @Transactional
    public ReworkRecord saveRework(ReworkRecord rework) {
        if (rework.getInspectionId() == null) {
            throw new IllegalArgumentException("Inspection is required.");
        }
        if (rework.getDefectId() == null) {
            throw new IllegalArgumentException("Defect is required.");
        }

        getInspection(rework.getInspectionId());
        DefectRecord defect = getDefect(rework.getDefectId());

        if (!defect.getInspectionId().equals(rework.getInspectionId())) {
            throw new IllegalArgumentException(
                    "Selected Defect does not belong to the selected Inspection."
            );
        }

        if (rework.getQuantitySent() == null || rework.getQuantitySent() <= 0) {
            throw new IllegalArgumentException(
                    "Rework quantity must be greater than zero."
            );
        }

        if (rework.getQuantitySent() > defect.getQuantity()) {
            throw new IllegalArgumentException(
                    "Rework quantity cannot exceed the selected Defect quantity."
            );
        }

        if (rework.getStatus() == null || rework.getStatus().isBlank()) {
            rework.setStatus("PENDING");
        }

        if (rework.getReinspectionResult() == null || rework.getReinspectionResult().isBlank()) {
            rework.setReinspectionResult("PENDING");
        }

        return reworkRepository.save(rework);
    }

    @Transactional
    public void verifyRework(Long id, Long verifiedBy, String reinspectionResult) {
        ReworkRecord rework = getRework(id);
        validateEmployee(verifiedBy, "Verifier");

        if (!"PASSED".equalsIgnoreCase(reinspectionResult)
                && !"FAILED".equalsIgnoreCase(reinspectionResult)) {
            throw new IllegalArgumentException(
                    "Re-inspection result must be PASSED or FAILED."
            );
        }

        rework.setVerifiedBy(verifiedBy);
        rework.setReinspectionResult(reinspectionResult.toUpperCase());
        rework.setStatus("REINSPECTED");
        reworkRepository.save(rework);
    }

    @Transactional
    public void archiveRework(Long id) {
        ReworkRecord rework = getRework(id);
        rework.setStatus("ARCHIVED");
        reworkRepository.save(rework);
    }

    @Transactional
    public void restoreRework(Long id) {
        ReworkRecord rework = getRework(id);
        rework.setStatus("PENDING");
        reworkRepository.save(rework);
    }

    // WASTAGE RECORDS
    public List<WastageRecord> getAllWastage() {
        return wastageRepository.findAllByOrderByWastageIdDesc();
    }

    public WastageRecord getWastage(Long id) {
        return wastageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Wastage Record not found."
                ));
    }

    @Transactional
    public WastageRecord saveWastage(WastageRecord wastage) {
        validateProductionPlan(wastage.getPlanId());
        validateMaterial(wastage.getMaterialId());
        validateEmployee(wastage.getRecordedBy(), "Recorded By Employee");

        if (wastage.getQuantity() == null || wastage.getQuantity().signum() <= 0) {
            throw new IllegalArgumentException(
                    "Wastage quantity must be greater than zero."
            );
        }

        if (wastage.getUnit() == null || wastage.getUnit().isBlank()) {
            throw new IllegalArgumentException("Unit is required.");
        }

        if (wastage.getReason() == null || wastage.getReason().isBlank()) {
            throw new IllegalArgumentException("Wastage reason is required.");
        }

        if (wastage.getApprovalStatus() == null || wastage.getApprovalStatus().isBlank()) {
            wastage.setApprovalStatus("PENDING");
        }

        return wastageRepository.save(wastage);
    }

    @Transactional
    public void approveWastage(Long id) {
        WastageRecord wastage = getWastage(id);
        wastage.setApprovalStatus("APPROVED");
        wastageRepository.save(wastage);
    }

    @Transactional
    public void rejectWastage(Long id) {
        WastageRecord wastage = getWastage(id);
        wastage.setApprovalStatus("REJECTED");
        wastageRepository.save(wastage);
    }

    @Transactional
    public void archiveWastage(Long id) {
        WastageRecord wastage = getWastage(id);
        wastage.setApprovalStatus("ARCHIVED");
        wastageRepository.save(wastage);
    }

    @Transactional
    public void restoreWastage(Long id) {
        WastageRecord wastage = getWastage(id);
        wastage.setApprovalStatus("PENDING");
        wastageRepository.save(wastage);
    }

    // QUALITY HOLDS
    public List<QualityHold> getAllHolds() {
        return holdRepository.findAllByOrderByHoldIdDesc();
    }

    public QualityHold getHold(Long id) {
        return holdRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Quality Hold not found."
                ));
    }

    @Transactional
    public QualityHold saveHold(QualityHold hold) {
        if (hold.getInspectionId() == null) {
            throw new IllegalArgumentException("Inspection is required.");
        }

        QualityInspection inspection = getInspection(hold.getInspectionId());
        hold.setPlanId(inspection.getPlanId());
        hold.setOrderId(inspection.getOrderId());

        if (hold.getReason() == null || hold.getReason().isBlank()) {
            throw new IllegalArgumentException("Hold reason is required.");
        }

        boolean creating = hold.getHoldId() == null;
        if (creating && holdRepository.existsByInspectionIdAndStatusIgnoreCase(
                hold.getInspectionId(),
                "ACTIVE"
        )) {
            throw new IllegalArgumentException(
                    "An ACTIVE Quality Hold already exists for this Inspection."
            );
        }

        if (hold.getStatus() == null || hold.getStatus().isBlank()) {
            hold.setStatus("ACTIVE");
        }

        return holdRepository.save(hold);
    }

    @Transactional
    public void releaseHold(Long id, Long releasedBy) {
        QualityHold hold = getHold(id);
        validateEmployee(releasedBy, "Released By Employee");

        hold.setStatus("RELEASED");
        hold.setReleasedBy(releasedBy);
        hold.setReleasedAt(LocalDateTime.now());
        holdRepository.save(hold);
    }

    @Transactional
    public void archiveHold(Long id) {
        QualityHold hold = getHold(id);
        hold.setStatus("ARCHIVED");
        holdRepository.save(hold);
    }

    @Transactional
    public void restoreHold(Long id) {
        QualityHold hold = getHold(id);
        if (hold.getReleasedAt() != null) {
            hold.setStatus("RELEASED");
        } else {
            hold.setStatus("ACTIVE");
        }
        holdRepository.save(hold);
    }

    public boolean isOrderBlockedByQualityHold(Long orderId) {
        return orderId != null && holdRepository.existsByOrderIdAndStatusIgnoreCase(
                orderId,
                "ACTIVE"
        );
    }

    // VALIDATION HELPERS
    private void validatePlanAndOrder(Long planId, Long orderId) {
        ProductionPlan plan = validateProductionPlan(planId);
        if (orderId == null) {
            throw new IllegalArgumentException("Customer Order is required.");
        }
        customerOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Selected Customer Order does not exist."
                ));

        if (plan.getOrderId() != null && !plan.getOrderId().equals(orderId)) {
            throw new IllegalArgumentException(
                    "Selected Customer Order does not match the selected Production Plan."
            );
        }
    }

    private ProductionPlan validateProductionPlan(Long planId) {
        if (planId == null) {
            throw new IllegalArgumentException("Production Plan is required.");
        }
        return productionPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Selected Production Plan does not exist."
                ));
    }

    private void validateEmployee(Long employeeId, String label) {
        if (employeeId == null) {
            throw new IllegalArgumentException(label + " is required.");
        }
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException(
                        label + " does not exist."
                ));

        if ("ARCHIVED".equalsIgnoreCase(employee.getStatus())) {
            throw new IllegalArgumentException(
                    label + " cannot be an archived Employee."
            );
        }
    }

    private void validateMaterial(Long materialId) {
        if (materialId == null) {
            throw new IllegalArgumentException("Material is required.");
        }
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Selected Material does not exist."
                ));

        if ("ARCHIVED".equalsIgnoreCase(material.getStatus())) {
            throw new IllegalArgumentException(
                    "Archived Materials cannot be used for a Wastage Record."
            );
        }
    }

    private BigDecimal valueOrZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private boolean isArchived(String status) {
        return "ARCHIVED".equalsIgnoreCase(status);
    }
}