package com.lankatex.smarttextile.inventory.repository;

import com.lankatex.smarttextile.inventory.entity.MaterialBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface MaterialBatchRepository
        extends JpaRepository<MaterialBatch, Long> {

    List<MaterialBatch> findAllByOrderByReceivedDateDesc();

    List<MaterialBatch> findByMaterialMaterialId(Long materialId);

    @Query("""
            SELECT COALESCE(SUM(b.availableQty), 0)
            FROM MaterialBatch b
            WHERE b.material.materialId = :materialId
            """)
    BigDecimal calculateCurrentStock(
            @Param("materialId") Long materialId
    );
}