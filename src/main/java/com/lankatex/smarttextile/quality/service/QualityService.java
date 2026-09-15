package com.lankatex.smarttextile.quality.service;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import com.lankatex.smarttextile.quality.entity.QualityInspection;
import com.lankatex.smarttextile.quality.repository.QualityInspectionRepository;
import com.lankatex.smarttextile.quality.entity.DefectRecord;
import com.lankatex.smarttextile.quality.repository.DefectRecordRepository;
import com.lankatex.smarttextile.quality.entity.WastageRecord;
import com.lankatex.smarttextile.quality.repository.WastageRecordRepository;
import com.lankatex.smarttextile.quality.entity.QualityHold;
import com.lankatex.smarttextile.quality.repository.QualityHoldRepository;
import com.lankatex.smarttextile.quality.dto.QualityDashboardStats;
@Service
@Transactional
public class QualityService {
    private final QualityInspectionRepository qualityInspectionRepository;
    private final DefectRecordRepository defectRecordRepository;
    private final WastageRecordRepository wastageRecordRepository;
    private final QualityHoldRepository qualityHoldRepository;
    public QualityService(
            QualityInspectionRepository qualityInspectionRepository,
            DefectRecordRepository defectRecordRepository,
            WastageRecordRepository wastageRecordRepository,
            QualityHoldRepository qualityHoldRepository) {
        this.qualityInspectionRepository = qualityInspectionRepository;
        this.defectRecordRepository = defectRecordRepository;
        this.wastageRecordRepository = wastageRecordRepository;
        this.qualityHoldRepository = qualityHoldRepository;
    }
    public QualityDashboardStats getDashboardStats() {
        return new QualityDashboardStats(
                qualityInspectionRepository.count(),
                defectRecordRepository.count(),
                wastageRecordRepository.count(),
                qualityHoldRepository.count());
    }
    public List<QualityInspection> getAllQualityInspections() {
        return qualityInspectionRepository.findAll(Sort.by(Sort.Direction.DESC, "inspectionId"));
    }
    public QualityInspection getQualityInspection(Long id) {
        return qualityInspectionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quality Inspection not found: " + id));
    }
    public QualityInspection saveQualityInspection(QualityInspection qualityInspection) {
        if (qualityInspection.getPlanId() == null) {
            throw new IllegalArgumentException("Production Plan ID is required");
        }
        if (qualityInspection.getStatus() == null || qualityInspection.getStatus().isBlank()) {
            qualityInspection.setStatus("Pending");
        }
        return qualityInspectionRepository.save(qualityInspection);
    }
    public void archiveQualityInspection(Long id) {
        QualityInspection qualityInspection = getQualityInspection(id);
        qualityInspection.setStatus("Archived");
        qualityInspectionRepository.save(qualityInspection);
    }
    public List<DefectRecord> getAllDefectRecords() {
        return defectRecordRepository.findAll(Sort.by(Sort.Direction.DESC, "defectId"));
    }
    public DefectRecord getDefectRecord(Long id) {
        return defectRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Defect Record not found: " + id));
    }
    public DefectRecord saveDefectRecord(DefectRecord defectRecord) {
        if (defectRecord.getInspectionId() == null) {
            throw new IllegalArgumentException("Inspection ID is required");
        }
        return defectRecordRepository.save(defectRecord);
    }
    public void deleteDefectRecord(Long id) {
        defectRecordRepository.deleteById(id);
    }
    public List<WastageRecord> getAllWastageRecords() {

        return wastageRecordRepository.findAll(Sort.by(Sort.Direction.DESC, "wastageId"));
    }
    public WastageRecord getWastageRecord(Long id) {
        return wastageRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Wastage Record not found: " + id));
    }
    public WastageRecord saveWastageRecord(WastageRecord wastageRecord) {
        if (wastageRecord.getQuantity() == null) {
            throw new IllegalArgumentException("Quantity is required");
        }
        if (wastageRecord.getApprovalStatus() == null || wastageRecord.getApprovalStatus().isBlank()) {
            wastageRecord.setApprovalStatus("Pending");
        }
        return wastageRecordRepository.save(wastageRecord);
    }
    public void archiveWastageRecord(Long id) {
        WastageRecord wastageRecord = getWastageRecord(id);
        wastageRecord.setApprovalStatus("Archived");
        wastageRecordRepository.save(wastageRecord);
    }
    public List<QualityHold> getAllQualityHolds() {
        return qualityHoldRepository.findAll(Sort.by(Sort.Direction.DESC, "holdId"));
    }
    public QualityHold getQualityHold(Long id) {
        return qualityHoldRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quality Hold not found: " + id));
    }
    public QualityHold saveQualityHold(QualityHold qualityHold) {
        if (qualityHold.getInspectionId() == null) {
            throw new IllegalArgumentException("Inspection ID is required");
        }
        if (qualityHold.getStatus() == null || qualityHold.getStatus().isBlank()) {
            qualityHold.setStatus("Pending");
        }
        return qualityHoldRepository.save(qualityHold);
    }
    public void archiveQualityHold(Long id) {
        QualityHold qualityHold = getQualityHold(id);
        qualityHold.setStatus("Archived");
        qualityHoldRepository.save(qualityHold);
    }
}

