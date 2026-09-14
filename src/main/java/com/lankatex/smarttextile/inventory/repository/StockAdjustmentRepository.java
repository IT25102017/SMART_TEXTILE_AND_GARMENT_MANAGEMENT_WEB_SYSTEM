package com.lankatex.smarttextile.inventory.repository;

import com.lankatex.smarttextile.inventory.entity.StockAdjustment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockAdjustmentRepository
        extends JpaRepository<StockAdjustment, Long> {

    List<StockAdjustment> findAllByOrderByAdjustmentIdDesc();
}