package com.lankatex.smarttextile.quality.repository;

import com.lankatex.smarttextile.quality.entity.ReworkRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReworkRecordRepository extends JpaRepository<ReworkRecord, Long> {
    List<ReworkRecord> findAllByOrderByReworkIdDesc();
}