package com.lankatex.smarttextile.quality.repository;
import com.lankatex.smarttextile.quality.entity.QualityInspection;
import org.springframework.data.jpa.repository.JpaRepository;
public interface QualityInspectionRepository extends JpaRepository<QualityInspection, Long> {
}