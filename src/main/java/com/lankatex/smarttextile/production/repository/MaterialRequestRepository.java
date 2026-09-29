package com.lankatex.smarttextile.production.repository;

import com.lankatex.smarttextile.production.entity.MaterialRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MaterialRequestRepository extends JpaRepository<MaterialRequest, Long> {
    List<MaterialRequest> findAllByOrderByMaterialRequestIdDesc();
    boolean existsByPlanId(Long planId);
}