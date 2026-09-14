package com.lankatex.smarttextile.inventory.service;

import com.lankatex.smarttextile.inventory.entity.*;
import com.lankatex.smarttextile.inventory.repository.MaterialBatchRepository;
import com.lankatex.smarttextile.inventory.repository.StockMovementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MaterialBatchService {

    private final MaterialBatchRepository batchRepository;
    private final StockMovementRepository movementRepository;

    public MaterialBatchService(
            MaterialBatchRepository batchRepository,
            StockMovementRepository movementRepository) {

        this.batchRepository = batchRepository;
        this.movementRepository = movementRepository;
    }

    public List<MaterialBatch> getAll() {
        return batchRepository
                .findAllByOrderByReceivedDateDesc();
    }

    public MaterialBatch getById(Long id) {

        return batchRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Material batch not found"
                        ));
    }

    @Transactional
    public MaterialBatch createBatch(
            MaterialBatch batch) {

        if (batch.getAvailableQty() == null ||
                batch.getAvailableQty().signum() <= 0) {

            throw new IllegalArgumentException(
                    "Received quantity must be greater than zero"
            );
        }

        MaterialBatch saved =
                batchRepository.save(batch);

        StockMovement movement =
                new StockMovement();

        movement.setMaterial(
                saved.getMaterial()
        );

        movement.setBatch(saved);

        movement.setMovementType(
                MovementType.STOCK_IN
        );

        movement.setQuantity(
                saved.getAvailableQty()
        );

        movement.setReferenceType(
                "MATERIAL_BATCH"
        );

        movement.setReferenceId(
                saved.getBatchId()
        );

        movement.setMovementDate(
                LocalDateTime.now()
        );

        movementRepository.save(movement);

        return saved;
    }
}