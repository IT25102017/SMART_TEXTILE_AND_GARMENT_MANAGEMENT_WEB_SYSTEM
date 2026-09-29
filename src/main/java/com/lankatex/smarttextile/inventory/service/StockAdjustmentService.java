package com.lankatex.smarttextile.inventory.service;

import com.lankatex.smarttextile.inventory.entity.MaterialBatch;
import com.lankatex.smarttextile.inventory.entity.StockAdjustment;
import com.lankatex.smarttextile.inventory.repository.MaterialBatchRepository;
import com.lankatex.smarttextile.inventory.repository.StockAdjustmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class StockAdjustmentService {

    private final StockAdjustmentRepository adjustmentRepository;

    private final MaterialBatchRepository batchRepository;


    public StockAdjustmentService(
            StockAdjustmentRepository adjustmentRepository,
            MaterialBatchRepository batchRepository) {

        this.adjustmentRepository = adjustmentRepository;
        this.batchRepository = batchRepository;
    }


    // =====================================================
    // GET ALL
    // =====================================================

    public List<StockAdjustment> getAll() {

        return adjustmentRepository
                .findAllByOrderByAdjustmentIdDesc();
    }


    // =====================================================
    // GET ONE
    // =====================================================

    public StockAdjustment getById(Long id) {

        return adjustmentRepository
                .findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Stock adjustment not found."
                        )
                );
    }


    // =====================================================
    // CREATE
    // =====================================================

    @Transactional
    public StockAdjustment create(
            StockAdjustment adjustment) {

        validateAdjustmentInput(adjustment);


        if (adjustment.getBatch() == null ||
                adjustment.getBatch().getBatchId() == null) {

            throw new IllegalArgumentException(
                    "Material batch is required."
            );
        }


        MaterialBatch batch =
                batchRepository
                        .findById(
                                adjustment
                                        .getBatch()
                                        .getBatchId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Material batch not found."
                                )
                        );


        adjustment.setBatch(batch);

        adjustment.setMaterial(
                batch.getMaterial()
        );


        /*
         * Store the quantity that existed when the
         * adjustment request was created.
         */
        adjustment.setPreviousQty(
                batch.getAvailableQty()
        );


        adjustment.setApprovalStatus(
                "PENDING"
        );


        adjustment.setApprovedBy(
                null
        );


        return adjustmentRepository.save(
                adjustment
        );
    }


    // =====================================================
    // UPDATE PENDING ADJUSTMENT
    //
    // Only corrected quantity and reason may be changed.
    // Batch, material and previous quantity remain unchanged.
    // =====================================================

    @Transactional
    public StockAdjustment updatePending(
            StockAdjustment submittedAdjustment) {

        if (submittedAdjustment.getAdjustmentId() == null) {

            throw new IllegalArgumentException(
                    "Adjustment ID is required."
            );
        }


        validateAdjustmentInput(
                submittedAdjustment
        );


        StockAdjustment existing =
                getById(
                        submittedAdjustment.getAdjustmentId()
                );


        ensurePending(existing);


        existing.setAdjustedQty(
                submittedAdjustment.getAdjustedQty()
        );


        existing.setReason(
                submittedAdjustment.getReason().trim()
        );


        return adjustmentRepository.save(
                existing
        );
    }


    // =====================================================
    // APPROVE
    //
    // Once approved, the record becomes immutable.
    // =====================================================

    @Transactional
    public void approve(
            Long adjustmentId,
            Long approvedBy) {

        if (approvedBy == null) {

            throw new IllegalArgumentException(
                    "Approved By User ID is required."
            );
        }


        StockAdjustment adjustment =
                getById(adjustmentId);


        ensurePending(adjustment);


        MaterialBatch batch =
                adjustment.getBatch();


        if (batch == null) {

            throw new IllegalArgumentException(
                    "Material batch not found for this adjustment."
            );
        }


        /*
         * Replace the current batch quantity with
         * the approved corrected quantity.
         */
        batch.setAvailableQty(
                adjustment.getAdjustedQty()
        );


        batchRepository.save(
                batch
        );


        adjustment.setApprovalStatus(
                "APPROVED"
        );


        adjustment.setApprovedBy(
                approvedBy
        );


        adjustmentRepository.save(
                adjustment
        );
    }


    // =====================================================
    // DELETE PENDING ADJUSTMENT
    //
    // Approved adjustment records cannot be deleted.
    // =====================================================

    @Transactional
    public void deletePending(Long id) {

        StockAdjustment adjustment =
                getById(id);


        ensurePending(adjustment);


        adjustmentRepository.delete(
                adjustment
        );


        adjustmentRepository.flush();
    }


    // =====================================================
    // VALIDATE INPUT
    // =====================================================

    private void validateAdjustmentInput(
            StockAdjustment adjustment) {

        BigDecimal adjustedQty =
                adjustment.getAdjustedQty();


        if (adjustedQty == null) {

            throw new IllegalArgumentException(
                    "Corrected quantity is required."
            );
        }


        if (adjustedQty.signum() < 0) {

            throw new IllegalArgumentException(
                    "Corrected quantity cannot be negative."
            );
        }


        if (adjustment.getReason() == null ||
                adjustment.getReason().isBlank()) {

            throw new IllegalArgumentException(
                    "Reason for adjustment is required."
            );
        }
    }


    // =====================================================
    // PENDING STATUS CHECK
    // =====================================================

    private void ensurePending(
            StockAdjustment adjustment) {

        if (!"PENDING".equalsIgnoreCase(
                adjustment.getApprovalStatus())) {

            throw new IllegalArgumentException(
                    "Approved stock adjustments are locked and cannot be edited, deleted or approved again."
            );
        }
    }
}