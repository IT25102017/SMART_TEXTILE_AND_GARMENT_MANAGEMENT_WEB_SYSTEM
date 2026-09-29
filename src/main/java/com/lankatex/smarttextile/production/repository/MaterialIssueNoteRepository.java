package com.lankatex.smarttextile.production.repository;

import com.lankatex.smarttextile.production.entity.MaterialIssueNote;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MaterialIssueNoteRepository extends JpaRepository<MaterialIssueNote, Long> {
    List<MaterialIssueNote> findAllByOrderByIssueIdDesc();
    boolean existsByMaterialRequestId(Long materialRequestId);
    boolean existsByPlanId(Long planId);
}