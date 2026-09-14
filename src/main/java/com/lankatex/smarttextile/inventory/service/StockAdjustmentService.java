package com.lankatex.smarttextile.inventory.service;

import com.lankatex.smarttextile.inventory.entity.MaterialBatch;
import com.lankatex.smarttextile.inventory.entity.StockAdjustment;
import com.lankatex.smarttextile.inventory.repository.MaterialBatchRepository;
import com.lankatex.smarttextile.inventory.repository.StockAdjustmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<StockAdjustment> getAll() {

        return adjustmentRepository
                .findAllByOrderByAdjustmentIdDesc();
    }

    @Transactional
    public StockAdjustment create(
            StockAdjustment adjustment) {

        MaterialBatch batch =
                batchRepository
                        .findById(
                                adjustment
                                        .getBatch()
                                        .getBatchId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Batch not found"
                                ));

        adjustment.setBatch(batch);
        adjustment.setMaterial(
                batch.getMaterial()
        );

        adjustment.setPreviousQty(
                batch.getAvailableQty()
        );

        adjustment.setApprovalStatus(
                "PENDING"
        );

        return adjustmentRepository.save(
                adjustment
        );
    }

    @Transactional
    public void approve(
            Long adjustmentId,
            Long approvedBy) {

        StockAdjustment adjustment =
                adjustmentRepository
                        .findById(adjustmentId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Adjustment not found"
                                ));

        if ("APPROVED".equals(
                adjustment.getApprovalStatus())) {

            return;
        }

        MaterialBatch batch =
                adjustment.getBatch();

        batch.setAvailableQty(
                adjustment.getAdjustedQty()
        );

        batchRepository.save(batch);

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
}