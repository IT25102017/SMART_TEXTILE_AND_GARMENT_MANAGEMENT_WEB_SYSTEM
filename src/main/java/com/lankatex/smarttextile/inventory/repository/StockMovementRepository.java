package com.lankatex.smarttextile.inventory.repository;

import com.lankatex.smarttextile.inventory.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockMovementRepository
        extends JpaRepository<StockMovement, Long> {

    List<StockMovement> findAllByOrderByMovementDateDesc();

    List<StockMovement>
    findByMaterialMaterialIdOrderByMovementDateDesc(Long materialId);
}