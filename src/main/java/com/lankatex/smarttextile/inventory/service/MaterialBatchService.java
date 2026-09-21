package com.lankatex.smarttextile.inventory.service;

import com.lankatex.smarttextile.inventory.entity.MaterialBatch;
import com.lankatex.smarttextile.inventory.entity.MovementType;
import com.lankatex.smarttextile.inventory.entity.StockMovement;
import com.lankatex.smarttextile.inventory.repository.MaterialBatchRepository;
import com.lankatex.smarttextile.inventory.repository.StockAdjustmentRepository;
import com.lankatex.smarttextile.inventory.repository.StockMovementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class MaterialBatchService {

    private final MaterialBatchRepository batchRepository;

    private final StockMovementRepository movementRepository;

    private final StockAdjustmentRepository adjustmentRepository;


    public MaterialBatchService(
            MaterialBatchRepository batchRepository,
            StockMovementRepository movementRepository,
            StockAdjustmentRepository adjustmentRepository) {

        this.batchRepository = batchRepository;
        this.movementRepository = movementRepository;
        this.adjustmentRepository = adjustmentRepository;
    }


    // =====================================================
    // READ ALL
    // =====================================================

    public List<MaterialBatch> getAll() {

        return batchRepository
                .findAllByOrderByReceivedDateDesc();
    }


    // =====================================================
    // READ ONE
    // =====================================================

    public MaterialBatch getById(Long id) {

        return batchRepository
                .findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Material batch not found"
                        )
                );
    }


    // =====================================================
    // CREATE
    // =====================================================

    @Transactional
    public MaterialBatch createBatch(
            MaterialBatch batch) {

        if (batch.getAvailableQty() == null ||
                batch.getAvailableQty()
                        .signum() <= 0) {

            throw new IllegalArgumentException(
                    "Received quantity must be greater than zero"
            );
        }


        if (batch.getStatus() == null ||
                batch.getStatus().isBlank()) {

            batch.setStatus("ACTIVE");
        }


        MaterialBatch saved =
                batchRepository.save(batch);


        /*
         * Every newly received batch creates its
         * initial STOCK_IN transaction.
         */
        StockMovement movement =
                new StockMovement();

        movement.setMaterial(
                saved.getMaterial()
        );

        movement.setBatch(
                saved
        );

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


        movementRepository.save(
                movement
        );


        return saved;
    }


    // =====================================================
    // UPDATE
    //
    // Material and available quantity are intentionally
    // preserved because changing them would make the
    // existing stock transaction history inconsistent.
    // =====================================================

    @Transactional
    public MaterialBatch updateBatch(
            MaterialBatch submittedBatch) {

        if (submittedBatch.getBatchId() == null) {

            throw new IllegalArgumentException(
                    "Batch ID is required for update"
            );
        }


        MaterialBatch existing =
                getById(
                        submittedBatch.getBatchId()
                );


        existing.setSupplierId(
                submittedBatch.getSupplierId()
        );

        existing.setBatchNo(
                submittedBatch.getBatchNo()
        );

        existing.setReceivedDate(
                submittedBatch.getReceivedDate()
        );

        existing.setUnitCost(
                submittedBatch.getUnitCost()
        );

        existing.setLocation(
                submittedBatch.getLocation()
        );


        return batchRepository.save(
                existing
        );
    }


    // =====================================================
    // ARCHIVE
    // =====================================================

    @Transactional
    public void archive(Long id) {

        MaterialBatch batch =
                getById(id);


        batch.setStatus(
                "ARCHIVED"
        );


        batchRepository.save(
                batch
        );
    }


    // =====================================================
    // RESTORE
    // =====================================================

    @Transactional
    public void restore(Long id) {

        MaterialBatch batch =
                getById(id);


        batch.setStatus(
                "ACTIVE"
        );


        batchRepository.save(
                batch
        );
    }


    // =====================================================
    // SAFE HARD DELETE
    //
    // Delete is allowed only when the batch has not been
    // used after its initial automatic STOCK_IN.
    // =====================================================

    @Transactional
    public void delete(Long id) {

        MaterialBatch batch =
                getById(id);


        List<StockMovement> movements =
                movementRepository
                        .findByBatchBatchId(id);


        boolean hasAdjustment =
                adjustmentRepository
                        .existsByBatchBatchId(id);


        /*
         * The automatic STOCK_IN created together with
         * the batch is not considered downstream usage.
         *
         * Any other movement means the batch has already
         * participated in an inventory transaction.
         */
        boolean hasOtherMovement =
                movements.stream()
                        .anyMatch(movement -> {

                            boolean initialStockIn =
                                    movement.getMovementType()
                                            == MovementType.STOCK_IN
                                            &&
                                            "MATERIAL_BATCH"
                                                    .equalsIgnoreCase(
                                                            movement
                                                                    .getReferenceType()
                                                    )
                                            &&
                                            Objects.equals(
                                                    movement.getReferenceId(),
                                                    id
                                            );


                            return !initialStockIn;
                        });


        if (hasAdjustment ||
                hasOtherMovement) {

            throw new IllegalArgumentException(
                    "Cannot permanently delete this batch because it is already used in inventory transactions. Please Archive it instead."
            );
        }


        /*
         * Remove the automatic STOCK_IN records first.
         * This is required because they reference the batch.
         */
        if (!movements.isEmpty()) {

            movementRepository.deleteAll(
                    movements
            );

            movementRepository.flush();
        }


        batchRepository.delete(
                batch
        );

        batchRepository.flush();
    }
}