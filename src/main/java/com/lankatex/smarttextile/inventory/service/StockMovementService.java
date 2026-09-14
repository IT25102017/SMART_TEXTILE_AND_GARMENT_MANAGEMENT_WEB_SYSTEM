package com.lankatex.smarttextile.inventory.service;

import com.lankatex.smarttextile.inventory.entity.*;
import com.lankatex.smarttextile.inventory.repository.MaterialBatchRepository;
import com.lankatex.smarttextile.inventory.repository.StockMovementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StockMovementService {

    private final StockMovementRepository movementRepository;
    private final MaterialBatchRepository batchRepository;

    public StockMovementService(
            StockMovementRepository movementRepository,
            MaterialBatchRepository batchRepository) {

        this.movementRepository = movementRepository;
        this.batchRepository = batchRepository;
    }

    public List<StockMovement> getAll() {
        return movementRepository
                .findAllByOrderByMovementDateDesc();
    }

    @Transactional
    public StockMovement recordMovement(
            StockMovement movement) {

        if (movement.getQuantity() == null ||
                movement.getQuantity()
                        .compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }

        if (movement.getBatch() == null ||
                movement.getBatch().getBatchId() == null) {

            throw new IllegalArgumentException(
                    "Material batch is required"
            );
        }

        MaterialBatch batch =
                batchRepository
                        .findById(
                                movement
                                        .getBatch()
                                        .getBatchId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Material batch not found"
                                ));

        movement.setBatch(batch);
        movement.setMaterial(batch.getMaterial());

        BigDecimal quantity =
                movement.getQuantity();

        switch (movement.getMovementType()) {

            case STOCK_OUT, WASTAGE -> {

                if (batch.getAvailableQty()
                        .compareTo(quantity) < 0) {

                    throw new IllegalArgumentException(
                            "Insufficient stock in this batch"
                    );
                }

                batch.setAvailableQty(
                        batch.getAvailableQty()
                                .subtract(quantity)
                );
            }

            case RETURN, STOCK_IN -> {

                batch.setAvailableQty(
                        batch.getAvailableQty()
                                .add(quantity)
                );
            }
        }

        batchRepository.save(batch);

        movement.setMovementDate(
                LocalDateTime.now()
        );

        return movementRepository.save(movement);
    }
}