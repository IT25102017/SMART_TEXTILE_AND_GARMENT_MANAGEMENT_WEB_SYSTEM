package com.lankatex.smarttextile.production.service;

import com.lankatex.smarttextile.customer.entity.CustomerOrder;
import com.lankatex.smarttextile.customer.repository.CustomerOrderRepository;
import com.lankatex.smarttextile.hr.entity.Employee;
import com.lankatex.smarttextile.hr.repository.EmployeeRepository;
import com.lankatex.smarttextile.inventory.entity.Material;
import com.lankatex.smarttextile.inventory.entity.MaterialBatch;
import com.lankatex.smarttextile.inventory.entity.MovementType;
import com.lankatex.smarttextile.inventory.entity.StockMovement;
import com.lankatex.smarttextile.inventory.repository.MaterialBatchRepository;
import com.lankatex.smarttextile.inventory.repository.MaterialRepository;
import com.lankatex.smarttextile.inventory.repository.StockMovementRepository;
import com.lankatex.smarttextile.production.dto.ProductionDashboardStats;
import com.lankatex.smarttextile.production.entity.MaterialIssueNote;
import com.lankatex.smarttextile.production.entity.MaterialRequest;
import com.lankatex.smarttextile.production.entity.ProductionPlan;
import com.lankatex.smarttextile.production.entity.ProductionProgress;
import com.lankatex.smarttextile.production.repository.MaterialIssueNoteRepository;
import com.lankatex.smarttextile.production.repository.MaterialRequestRepository;
import com.lankatex.smarttextile.production.repository.ProductionPlanRepository;
import com.lankatex.smarttextile.production.repository.ProductionProgressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProductionService {

    private final ProductionPlanRepository productionPlanRepository;
    private final MaterialRequestRepository materialRequestRepository;
    private final MaterialIssueNoteRepository materialIssueNoteRepository;
    private final ProductionProgressRepository productionProgressRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final EmployeeRepository employeeRepository;
    private final MaterialRepository materialRepository;
    private final MaterialBatchRepository materialBatchRepository;
    private final StockMovementRepository stockMovementRepository;

    public ProductionService(
            ProductionPlanRepository productionPlanRepository,
            MaterialRequestRepository materialRequestRepository,
            MaterialIssueNoteRepository materialIssueNoteRepository,
            ProductionProgressRepository productionProgressRepository,
            CustomerOrderRepository customerOrderRepository,
            EmployeeRepository employeeRepository,
            MaterialRepository materialRepository,
            MaterialBatchRepository materialBatchRepository,
            StockMovementRepository stockMovementRepository) {
        this.productionPlanRepository = productionPlanRepository;
        this.materialRequestRepository = materialRequestRepository;
        this.materialIssueNoteRepository = materialIssueNoteRepository;
        this.productionProgressRepository = productionProgressRepository;
        this.customerOrderRepository = customerOrderRepository;
        this.employeeRepository = employeeRepository;
        this.materialRepository = materialRepository;
        this.materialBatchRepository = materialBatchRepository;
        this.stockMovementRepository = stockMovementRepository;
    }

    // =====================================================
    // DASHBOARD
    // =====================================================

    public ProductionDashboardStats getDashboardStats() {
        LocalDate today = LocalDate.now();

        long activePlans = productionPlanRepository.findAll().stream()
                .filter(plan -> !isStatus(plan.getStatus(), "ARCHIVED"))
                .filter(plan -> !isStatus(plan.getStatus(), "COMPLETED"))
                .count();

        long pendingRequests = materialRequestRepository.findAll().stream()
                .filter(request -> isStatus(request.getStatus(), "PENDING"))
                .count();

        long delayedPlans = productionPlanRepository.findAll().stream()
                .filter(plan -> plan.getEndDate() != null && plan.getEndDate().isBefore(today))
                .filter(plan -> !isStatus(plan.getStatus(), "COMPLETED"))
                .filter(plan -> !isStatus(plan.getStatus(), "ARCHIVED"))
                .count();

        return new ProductionDashboardStats(
                activePlans,
                pendingRequests,
                materialIssueNoteRepository.count(),
                delayedPlans
        );
    }

    // =====================================================
    // LOOKUP DATA FOR FORMS AND LISTS
    // =====================================================

    public List<CustomerOrder> getApprovedCustomerOrders() {
        return customerOrderRepository.findAll().stream()
                .filter(order -> isStatus(order.getStatus(), "APPROVED"))
                .toList();
    }

    public List<Employee> getActiveEmployees() {
        return employeeRepository.findAll().stream()
                .filter(employee -> !isStatus(employee.getStatus(), "ARCHIVED"))
                .toList();
    }

    public List<Material> getActiveMaterials() {
        return materialRepository.findAll().stream()
                .filter(material -> !isStatus(material.getStatus(), "ARCHIVED"))
                .toList();
    }

    public List<MaterialBatch> getAvailableBatches() {
        return materialBatchRepository.findAll().stream()
                .filter(batch -> !isStatus(batch.getStatus(), "ARCHIVED"))
                .filter(batch -> batch.getAvailableQty() != null
                        && batch.getAvailableQty().compareTo(BigDecimal.ZERO) > 0)
                .toList();
    }

    public List<ProductionPlan> getApprovedOrActivePlans() {
        return productionPlanRepository.findAllByOrderByPlanIdDesc().stream()
                .filter(plan -> isStatus(plan.getStatus(), "APPROVED")
                        || isStatus(plan.getStatus(), "IN_PROGRESS"))
                .toList();
    }

    public List<MaterialRequest> getApprovedMaterialRequests() {
        return materialRequestRepository.findAllByOrderByMaterialRequestIdDesc().stream()
                .filter(request -> isStatus(request.getStatus(), "APPROVED"))
                .toList();
    }

    public Map<Long, CustomerOrder> getCustomerOrderMap() {
        return customerOrderRepository.findAll().stream()
                .collect(Collectors.toMap(CustomerOrder::getOrderId, Function.identity()));
    }

    public Map<Long, Employee> getEmployeeMap() {
        return employeeRepository.findAll().stream()
                .collect(Collectors.toMap(Employee::getEmployeeId, Function.identity()));
    }

    public Map<Long, Material> getMaterialMap() {
        return materialRepository.findAll().stream()
                .collect(Collectors.toMap(Material::getMaterialId, Function.identity()));
    }

    public Map<Long, MaterialBatch> getBatchMap() {
        return materialBatchRepository.findAll().stream()
                .collect(Collectors.toMap(MaterialBatch::getBatchId, Function.identity()));
    }

    public Map<Long, ProductionPlan> getProductionPlanMap() {
        return productionPlanRepository.findAll().stream()
                .collect(Collectors.toMap(ProductionPlan::getPlanId, Function.identity()));
    }

    public Map<Long, MaterialRequest> getMaterialRequestMap() {
        return materialRequestRepository.findAll().stream()
                .collect(Collectors.toMap(MaterialRequest::getMaterialRequestId, Function.identity()));
    }

    public Map<Long, BigDecimal> getCompletionPercentageMap() {
        Map<Long, BigDecimal> result = new LinkedHashMap<>();

        for (ProductionPlan plan : productionPlanRepository.findAll()) {
            BigDecimal percentage = BigDecimal.ZERO;

            ProductionProgress latest = productionProgressRepository
                    .findAllByOrderByProgressDateDescProgressIdDesc()
                    .stream()
                    .filter(row -> Objects.equals(row.getPlanId(), plan.getPlanId()))
                    .findFirst()
                    .orElse(null);

            if (latest != null
                    && plan.getPlannedQty() != null
                    && plan.getPlannedQty().compareTo(BigDecimal.ZERO) > 0) {
                percentage = latest.getCompletedQty()
                        .multiply(BigDecimal.valueOf(100))
                        .divide(plan.getPlannedQty(), 2, RoundingMode.HALF_UP)
                        .min(BigDecimal.valueOf(100));
            }

            result.put(plan.getPlanId(), percentage);
        }

        return result;
    }

    // =====================================================
    // PRODUCTION PLAN
    // =====================================================

    public List<ProductionPlan> getAllProductionPlans() {
        return productionPlanRepository.findAllByOrderByPlanIdDesc();
    }

    public ProductionPlan getProductionPlan(Long id) {
        return productionPlanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Production Plan not found."));
    }

    @Transactional
    public ProductionPlan saveProductionPlan(ProductionPlan submitted) {
        validateProductionPlan(submitted);

        if (submitted.getPlanId() == null) {
            submitted.setStatus("PENDING");
            return productionPlanRepository.save(submitted);
        }

        ProductionPlan existing = getProductionPlan(submitted.getPlanId());

        if (!isStatus(existing.getStatus(), "PENDING")
                && !isStatus(existing.getStatus(), "REJECTED")) {
            throw new IllegalArgumentException(
                    "Only PENDING or REJECTED Production Plans can be edited."
            );
        }

        existing.setOrderId(submitted.getOrderId());
        existing.setPlannedQty(submitted.getPlannedQty());
        existing.setStartDate(submitted.getStartDate());
        existing.setEndDate(submitted.getEndDate());
        existing.setProductionLine(submitted.getProductionLine());
        existing.setSupervisorId(submitted.getSupervisorId());
        existing.setStatus("PENDING");

        return productionPlanRepository.save(existing);
    }

    @Transactional
    public void approveProductionPlan(Long id) {
        ProductionPlan plan = getProductionPlan(id);
        if (!isStatus(plan.getStatus(), "PENDING")) {
            throw new IllegalArgumentException("Only PENDING plans can be approved.");
        }
        plan.setStatus("APPROVED");
        productionPlanRepository.save(plan);
    }

    @Transactional
    public void rejectProductionPlan(Long id) {
        ProductionPlan plan = getProductionPlan(id);
        if (!isStatus(plan.getStatus(), "PENDING")) {
            throw new IllegalArgumentException("Only PENDING plans can be rejected.");
        }
        plan.setStatus("REJECTED");
        productionPlanRepository.save(plan);
    }

    @Transactional
    public void archiveProductionPlan(Long id) {
        ProductionPlan plan = getProductionPlan(id);
        plan.setStatus("ARCHIVED");
        productionPlanRepository.save(plan);
    }

    @Transactional
    public void restoreProductionPlan(Long id) {
        ProductionPlan plan = getProductionPlan(id);
        plan.setStatus("PENDING");
        productionPlanRepository.save(plan);
    }

    @Transactional
    public void deleteProductionPlan(Long id) {
        getProductionPlan(id);

        if (materialRequestRepository.existsByPlanId(id)
                || materialIssueNoteRepository.existsByPlanId(id)
                || productionProgressRepository.existsByPlanId(id)) {
            throw new IllegalArgumentException(
                    "Cannot permanently delete this plan because related production records exist. Archive it instead."
            );
        }

        productionPlanRepository.deleteById(id);
    }

    // =====================================================
    // MATERIAL REQUEST
    // =====================================================

    public List<MaterialRequest> getAllMaterialRequests() {
        return materialRequestRepository.findAllByOrderByMaterialRequestIdDesc();
    }

    public MaterialRequest getMaterialRequest(Long id) {
        return materialRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Material Request not found."));
    }

    @Transactional
    public MaterialRequest saveMaterialRequest(MaterialRequest submitted) {
        validateMaterialRequest(submitted);

        if (submitted.getMaterialRequestId() == null) {
            submitted.setStatus("PENDING");
            submitted.setApprovedBy(null);
            return materialRequestRepository.save(submitted);
        }

        MaterialRequest existing = getMaterialRequest(submitted.getMaterialRequestId());

        if (!isStatus(existing.getStatus(), "PENDING")) {
            throw new IllegalArgumentException("Only PENDING Material Requests can be edited.");
        }

        existing.setPlanId(submitted.getPlanId());
        existing.setRequestedBy(submitted.getRequestedBy());
        existing.setRequestDate(submitted.getRequestDate());

        return materialRequestRepository.save(existing);
    }

    @Transactional
    public void approveMaterialRequest(Long id, Long approvedBy) {
        MaterialRequest request = getMaterialRequest(id);
        if (!isStatus(request.getStatus(), "PENDING")) {
            throw new IllegalArgumentException("Only PENDING Material Requests can be approved.");
        }
        if (approvedBy == null) {
            throw new IllegalArgumentException("Approved By User ID is required.");
        }
        request.setStatus("APPROVED");
        request.setApprovedBy(approvedBy);
        materialRequestRepository.save(request);
    }

    @Transactional
    public void rejectMaterialRequest(Long id, Long approvedBy) {
        MaterialRequest request = getMaterialRequest(id);
        if (!isStatus(request.getStatus(), "PENDING")) {
            throw new IllegalArgumentException("Only PENDING Material Requests can be rejected.");
        }
        request.setStatus("REJECTED");
        request.setApprovedBy(approvedBy);
        materialRequestRepository.save(request);
    }

    @Transactional
    public void archiveMaterialRequest(Long id) {
        MaterialRequest request = getMaterialRequest(id);
        request.setStatus("ARCHIVED");
        materialRequestRepository.save(request);
    }

    @Transactional
    public void deleteMaterialRequest(Long id) {
        MaterialRequest request = getMaterialRequest(id);

        if (!isStatus(request.getStatus(), "PENDING")) {
            throw new IllegalArgumentException("Only PENDING Material Requests can be permanently deleted.");
        }

        if (materialIssueNoteRepository.existsByMaterialRequestId(id)) {
            throw new IllegalArgumentException(
                    "Cannot delete this Material Request because a Material Issue Note already exists."
            );
        }

        materialRequestRepository.delete(request);
    }

    // =====================================================
    // MATERIAL ISSUE NOTE + INVENTORY STOCK UPDATE
    // =====================================================

    public List<MaterialIssueNote> getAllMaterialIssueNotes() {
        return materialIssueNoteRepository.findAllByOrderByIssueIdDesc();
    }

    @Transactional
    public MaterialIssueNote createMaterialIssueNote(MaterialIssueNote issue) {
        if (issue.getIssueId() != null) {
            throw new IllegalArgumentException("Material Issue Notes are immutable audit records and cannot be edited.");
        }

        MaterialRequest request = getMaterialRequest(issue.getMaterialRequestId());
        if (!isStatus(request.getStatus(), "APPROVED")) {
            throw new IllegalArgumentException("Only an APPROVED Material Request can be issued.");
        }

        if (!Objects.equals(request.getPlanId(), issue.getPlanId())) {
            throw new IllegalArgumentException("Selected Material Request does not belong to the selected Production Plan.");
        }

        ProductionPlan plan = getProductionPlan(issue.getPlanId());
        if (!isStatus(plan.getStatus(), "APPROVED")
                && !isStatus(plan.getStatus(), "IN_PROGRESS")) {
            throw new IllegalArgumentException("Production Plan must be APPROVED or IN_PROGRESS before issuing materials.");
        }

        Material material = materialRepository.findById(issue.getMaterialId())
                .orElseThrow(() -> new IllegalArgumentException("Selected Material does not exist."));

        MaterialBatch batch = materialBatchRepository.findById(issue.getBatchId())
                .orElseThrow(() -> new IllegalArgumentException("Selected Material Batch does not exist."));

        if (!Objects.equals(batch.getMaterial().getMaterialId(), material.getMaterialId())) {
            throw new IllegalArgumentException("Selected Batch does not belong to the selected Material.");
        }

        if (isStatus(material.getStatus(), "ARCHIVED") || isStatus(batch.getStatus(), "ARCHIVED")) {
            throw new IllegalArgumentException("Archived Material or Batch cannot be issued.");
        }

        if (issue.getIssuedQty() == null || issue.getIssuedQty().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Issued quantity must be greater than zero.");
        }

        if (batch.getAvailableQty() == null
                || issue.getIssuedQty().compareTo(batch.getAvailableQty()) > 0) {
            throw new IllegalArgumentException("Issued quantity is greater than the available batch stock.");
        }

        if (issue.getIssueDate() == null) {
            issue.setIssueDate(LocalDate.now());
        }

        MaterialIssueNote saved = materialIssueNoteRepository.save(issue);

        batch.setAvailableQty(batch.getAvailableQty().subtract(issue.getIssuedQty()));
        materialBatchRepository.save(batch);

        StockMovement movement = new StockMovement();
        movement.setMaterial(material);
        movement.setBatch(batch);
        movement.setMovementType(MovementType.STOCK_OUT);
        movement.setQuantity(issue.getIssuedQty());
        movement.setReferenceType("MATERIAL_ISSUE");
        movement.setReferenceId(saved.getIssueId());
        movement.setMovementDate(LocalDateTime.now());
        movement.setCreatedBy(issue.getIssuedBy());
        stockMovementRepository.save(movement);

        if (isStatus(plan.getStatus(), "APPROVED")) {
            plan.setStatus("IN_PROGRESS");
            productionPlanRepository.save(plan);
        }

        return saved;
    }

    // =====================================================
    // PRODUCTION PROGRESS
    // =====================================================

    public List<ProductionProgress> getAllProductionProgress() {
        return productionProgressRepository.findAllByOrderByProgressDateDescProgressIdDesc();
    }

    public ProductionProgress getProductionProgress(Long id) {
        return productionProgressRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Production Progress not found."));
    }

    @Transactional
    public ProductionProgress saveProductionProgress(ProductionProgress submitted) {
        ProductionPlan plan = getProductionPlan(submitted.getPlanId());

        if (!isStatus(plan.getStatus(), "APPROVED")
                && !isStatus(plan.getStatus(), "IN_PROGRESS")) {
            throw new IllegalArgumentException("Progress can be recorded only for APPROVED or IN_PROGRESS plans.");
        }

        if (submitted.getProgressDate() == null) {
            throw new IllegalArgumentException("Progress date is required.");
        }

        normalizeAndValidateProgress(submitted, plan);

        boolean duplicate = submitted.getProgressId() == null
                ? productionProgressRepository.existsByPlanIdAndProgressDate(
                submitted.getPlanId(), submitted.getProgressDate())
                : productionProgressRepository.existsByPlanIdAndProgressDateAndProgressIdNot(
                submitted.getPlanId(), submitted.getProgressDate(), submitted.getProgressId());

        if (duplicate) {
            throw new IllegalArgumentException("A progress record already exists for this plan on the selected date.");
        }

        ProductionProgress saved = productionProgressRepository.save(submitted);

        if (submitted.getRemainingQty().compareTo(BigDecimal.ZERO) == 0) {
            plan.setStatus("COMPLETED");
        } else {
            plan.setStatus("IN_PROGRESS");
        }
        productionPlanRepository.save(plan);

        return saved;
    }

    @Transactional
    public void deleteProductionProgress(Long id) {
        ProductionProgress progress = getProductionProgress(id);
        ProductionPlan plan = getProductionPlan(progress.getPlanId());

        if (isStatus(plan.getStatus(), "COMPLETED")) {
            throw new IllegalArgumentException(
                    "Completed plan progress is an audit record and cannot be deleted."
            );
        }

        productionProgressRepository.delete(progress);
    }

    // =====================================================
    // VALIDATION HELPERS
    // =====================================================

    private void validateProductionPlan(ProductionPlan plan) {
        CustomerOrder order = customerOrderRepository.findById(plan.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Selected Customer Order does not exist."));

        if (!isStatus(order.getStatus(), "APPROVED")) {
            throw new IllegalArgumentException("Only APPROVED Customer Orders can be used for Production Plans.");
        }

        if (plan.getPlannedQty() == null || plan.getPlannedQty().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Planned quantity must be greater than zero.");
        }

        if (plan.getStartDate() == null || plan.getEndDate() == null) {
            throw new IllegalArgumentException("Start date and end date are required.");
        }

        if (plan.getEndDate().isBefore(plan.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date.");
        }

        if (plan.getSupervisorId() != null) {
            Employee supervisor = employeeRepository.findById(plan.getSupervisorId())
                    .orElseThrow(() -> new IllegalArgumentException("Selected Supervisor does not exist."));

            if (isStatus(supervisor.getStatus(), "ARCHIVED")) {
                throw new IllegalArgumentException("Archived Employee cannot be selected as Production Supervisor.");
            }
        }
    }

    private void validateMaterialRequest(MaterialRequest request) {
        ProductionPlan plan = getProductionPlan(request.getPlanId());

        if (!isStatus(plan.getStatus(), "APPROVED")
                && !isStatus(plan.getStatus(), "IN_PROGRESS")) {
            throw new IllegalArgumentException("Material Request requires an APPROVED or IN_PROGRESS Production Plan.");
        }

        if (request.getRequestedBy() == null) {
            throw new IllegalArgumentException("Requested By User ID is required.");
        }

        if (request.getRequestDate() == null) {
            request.setRequestDate(LocalDate.now());
        }
    }

    private void normalizeAndValidateProgress(ProductionProgress progress, ProductionPlan plan) {
        if (progress.getCompletedQty() == null) progress.setCompletedQty(BigDecimal.ZERO);
        if (progress.getRejectedQty() == null) progress.setRejectedQty(BigDecimal.ZERO);
        if (progress.getReworkedQty() == null) progress.setReworkedQty(BigDecimal.ZERO);

        if (progress.getCompletedQty().compareTo(BigDecimal.ZERO) < 0
                || progress.getRejectedQty().compareTo(BigDecimal.ZERO) < 0
                || progress.getReworkedQty().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Production quantities cannot be negative.");
        }

        if (progress.getCompletedQty().compareTo(plan.getPlannedQty()) > 0) {
            throw new IllegalArgumentException("Completed quantity cannot exceed planned quantity.");
        }

        if (progress.getRejectedQty().compareTo(progress.getCompletedQty()) > 0) {
            throw new IllegalArgumentException("Rejected quantity cannot exceed completed quantity.");
        }

        if (progress.getReworkedQty().compareTo(progress.getRejectedQty()) > 0) {
            throw new IllegalArgumentException("Reworked quantity cannot exceed rejected quantity.");
        }

        progress.setRemainingQty(plan.getPlannedQty().subtract(progress.getCompletedQty()));
    }

    private boolean isStatus(String actual, String expected) {
        return actual != null && expected.equalsIgnoreCase(actual);
    }
}