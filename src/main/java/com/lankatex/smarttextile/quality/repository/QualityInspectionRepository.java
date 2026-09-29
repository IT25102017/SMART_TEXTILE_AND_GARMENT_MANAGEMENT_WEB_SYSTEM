package com.lankatex.smarttextile.quality.repository;

import com.lankatex.smarttextile.quality.entity.QualityInspection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QualityInspectionRepository extends JpaRepository<QualityInspection, Long> {
    List<QualityInspection> findAllByOrderByInspectionDateDesc();
}