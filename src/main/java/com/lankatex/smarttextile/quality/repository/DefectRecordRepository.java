package com.lankatex.smarttextile.quality.repository;

import com.lankatex.smarttextile.quality.entity.DefectRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DefectRecordRepository extends JpaRepository<DefectRecord, Long> {
    List<DefectRecord> findAllByOrderByDefectIdDesc();
}