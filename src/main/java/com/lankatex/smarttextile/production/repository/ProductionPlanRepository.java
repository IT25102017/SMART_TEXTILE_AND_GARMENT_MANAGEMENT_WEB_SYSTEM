package com.lankatex.smarttextile.production.repository;

import com.lankatex.smarttextile.production.entity.ProductionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProductionPlanRepository extends JpaRepository<ProductionPlan, Long> {
}
