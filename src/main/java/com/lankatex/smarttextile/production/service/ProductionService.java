package com.lankatex.smarttextile.production.service;

import com.lankatex.smarttextile.production.dto.ProductionDashboardStats;
import com.lankatex.smarttextile.production.entity.MaterialIssueNote;
import com.lankatex.smarttextile.production.entity.MaterialRequest;
import com.lankatex.smarttextile.production.entity.ProductionPlan;
import com.lankatex.smarttextile.production.entity.ProductionProgress;
import com.lankatex.smarttextile.production.repository.MaterialIssueNoteRepository;
import com.lankatex.smarttextile.production.repository.MaterialRequestRepository;
import com.lankatex.smarttextile.production.repository.ProductionPlanRepository;
import com.lankatex.smarttextile.production.repository.ProductionProgressRepository;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductionService {

    private final ProductionPlanRepository productionPlanRepository;
    private final MaterialRequestRepository materialRequestRepository;
    private final MaterialIssueNoteRepository materialIssueNoteRepository;
    private final ProductionProgressRepository productionProgressRepository;

    public ProductionService(
            ProductionPlanRepository productionPlanRepository,
            MaterialRequestRepository materialRequestRepository,
            MaterialIssueNoteRepository materialIssueNoteRepository,
            ProductionProgressRepository productionProgressRepository) {

        this.productionPlanRepository = productionPlanRepository;
        this.materialRequestRepository = materialRequestRepository;
        this.materialIssueNoteRepository = materialIssueNoteRepository;
        this.productionProgressRepository = productionProgressRepository;
    }


    // =========================
    // DASHBOARD
    // =========================

    public ProductionDashboardStats getDashboardStats() {

        return new ProductionDashboardStats(
                productionPlanRepository.count(),
                materialRequestRepository.count(),
                materialIssueNoteRepository.count(),
                productionProgressRepository.count()
        );
    }


    // =========================
    // PRODUCTION PLAN CRUD
    // =========================

    public List<ProductionPlan> getAllProductionPlans() {

        return productionPlanRepository.findAll(
                Sort.by(Sort.Direction.DESC, "planId")
        );
    }

    public ProductionPlan getProductionPlan(Long id) {

        return productionPlanRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Production Plan not found: " + id
                        ));
    }

    public ProductionPlan saveProductionPlan(
            ProductionPlan productionPlan) {

        if (productionPlan.getOrderId() == null) {

            throw new IllegalArgumentException(
                    "Customer Order ID is required"
            );
        }

        if (productionPlan.getStatus() == null
                || productionPlan.getStatus().isBlank()) {

            productionPlan.setStatus("Pending");
        }

        return productionPlanRepository.save(productionPlan);
    }

    // Soft Delete
    public void archiveProductionPlan(Long id) {

        ProductionPlan productionPlan =
                getProductionPlan(id);

        productionPlan.setStatus("Archived");

        productionPlanRepository.save(productionPlan);
    }

    // Hard Delete
    public void deleteProductionPlan(Long id) {

        if (!productionPlanRepository.existsById(id)) {

            throw new IllegalArgumentException(
                    "Production Plan not found: " + id
            );
        }

        productionPlanRepository.deleteById(id);

        // Execute DELETE immediately
        productionPlanRepository.flush();
    }


    // =========================
    // MATERIAL REQUEST CRUD
    // =========================

    public List<MaterialRequest> getAllMaterialRequests() {

        return materialRequestRepository.findAll(
                Sort.by(
                        Sort.Direction.DESC,
                        "materialRequestId"
                )
        );
    }

    public MaterialRequest getMaterialRequest(Long id) {

        return materialRequestRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Material Request not found: " + id
                        ));
    }

    public MaterialRequest saveMaterialRequest(
            MaterialRequest materialRequest) {

        if (materialRequest.getPlanId() == null) {

            throw new IllegalArgumentException(
                    "Production Plan ID is required"
            );
        }

        if (materialRequest.getStatus() == null
                || materialRequest.getStatus().isBlank()) {

            materialRequest.setStatus("Pending");
        }

        return materialRequestRepository.save(materialRequest);
    }

    // Soft Delete
    public void archiveMaterialRequest(Long id) {

        MaterialRequest materialRequest =
                getMaterialRequest(id);

        materialRequest.setStatus("Archived");

        materialRequestRepository.save(materialRequest);
    }

    // Hard Delete
    public void deleteMaterialRequest(Long id) {

        if (!materialRequestRepository.existsById(id)) {

            throw new IllegalArgumentException(
                    "Material Request not found: " + id
            );
        }

        materialRequestRepository.deleteById(id);

        materialRequestRepository.flush();
    }


    // =========================
    // MATERIAL ISSUE NOTE CRUD
    // =========================

    public List<MaterialIssueNote> getAllMaterialIssueNotes() {

        return materialIssueNoteRepository.findAll(
                Sort.by(Sort.Direction.DESC, "issueId")
        );
    }

    public MaterialIssueNote getMaterialIssueNote(Long id) {

        return materialIssueNoteRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Material Issue Note not found: " + id
                        ));
    }

    public MaterialIssueNote saveMaterialIssueNote(
            MaterialIssueNote materialIssueNote) {

        if (materialIssueNote.getPlanId() == null) {

            throw new IllegalArgumentException(
                    "Production Plan ID is required"
            );
        }

        return materialIssueNoteRepository.save(
                materialIssueNote
        );
    }

    public void deleteMaterialIssueNote(Long id) {

        if (!materialIssueNoteRepository.existsById(id)) {

            throw new IllegalArgumentException(
                    "Material Issue Note not found: " + id
            );
        }

        materialIssueNoteRepository.deleteById(id);

        materialIssueNoteRepository.flush();
    }


    // =========================
    // PRODUCTION PROGRESS CRUD
    // =========================

    public List<ProductionProgress>
    getAllProductionProgresss() {

        return productionProgressRepository.findAll(
                Sort.by(
                        Sort.Direction.DESC,
                        "progressId"
                )
        );
    }

    public ProductionProgress getProductionProgress(Long id) {

        return productionProgressRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Production Progress not found: " + id
                        ));
    }

    public ProductionProgress saveProductionProgress(
            ProductionProgress productionProgress) {

        if (productionProgress.getPlanId() == null) {

            throw new IllegalArgumentException(
                    "Production Plan ID is required"
            );
        }

        return productionProgressRepository.save(
                productionProgress
        );
    }

    public void deleteProductionProgress(Long id) {

        if (!productionProgressRepository.existsById(id)) {

            throw new IllegalArgumentException(
                    "Production Progress not found: " + id
            );
        }

        productionProgressRepository.deleteById(id);

        productionProgressRepository.flush();
    }
}