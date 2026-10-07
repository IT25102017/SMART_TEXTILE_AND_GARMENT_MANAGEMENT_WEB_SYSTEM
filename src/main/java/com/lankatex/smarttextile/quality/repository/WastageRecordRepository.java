package com.lankatex.smarttextile.quality.repository;

import com.lankatex.smarttextile.quality.entity.WastageRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WastageRecordRepository extends JpaRepository<WastageRecord, Long> {
    List<WastageRecord> findAllByOrderByWastageIdDesc();
}