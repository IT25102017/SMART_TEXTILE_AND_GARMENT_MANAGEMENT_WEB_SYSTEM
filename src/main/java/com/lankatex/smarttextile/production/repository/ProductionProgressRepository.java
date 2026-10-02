package com.lankatex.smarttextile.production.repository;

import com.lankatex.smarttextile.production.entity.ProductionProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface ProductionProgressRepository extends JpaRepository<ProductionProgress, Long> {
    List<ProductionProgress> findAllByOrderByProgressDateDescProgressIdDesc();
    boolean existsByPlanId(Long planId);
    boolean existsByPlanIdAndProgressDate(Long planId, LocalDate progressDate);
    boolean existsByPlanIdAndProgressDateAndProgressIdNot(Long planId, LocalDate progressDate, Long progressId);
}