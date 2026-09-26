package com.lankatex.smarttextile.quality.repository;

import com.lankatex.smarttextile.quality.entity.QualityHold;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QualityHoldRepository extends JpaRepository<QualityHold, Long> {
    List<QualityHold> findAllByOrderByHoldIdDesc();

    boolean existsByInspectionIdAndStatusIgnoreCase(Long inspectionId, String status);

    boolean existsByOrderIdAndStatusIgnoreCase(Long orderId, String status);
}