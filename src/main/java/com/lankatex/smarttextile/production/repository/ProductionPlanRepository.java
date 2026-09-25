package com.lankatex.smarttextile.production.repository;

import com.lankatex.smarttextile.production.entity.ProductionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductionPlanRepository extends JpaRepository<ProductionPlan, Long> {
    List<ProductionPlan> findAllByOrderByPlanIdDesc();
}