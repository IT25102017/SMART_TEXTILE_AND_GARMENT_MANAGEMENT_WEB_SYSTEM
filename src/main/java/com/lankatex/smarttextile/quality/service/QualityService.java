package com.lankatex.smarttextile.quality.service;

import com.lankatex.smarttextile.quality.dto.QualityDashboardStats;
import com.lankatex.smarttextile.quality.entity.DefectRecord;
import com.lankatex.smarttextile.quality.entity.QualityHold;
import com.lankatex.smarttextile.quality.entity.QualityInspection;
import com.lankatex.smarttextile.quality.entity.WastageRecord;
import com.lankatex.smarttextile.quality.repository.DefectRecordRepository;
import com.lankatex.smarttextile.quality.repository.QualityHoldRepository;
import com.lankatex.smarttextile.quality.repository.QualityInspectionRepository;
import com.lankatex.smarttextile.quality.repository.WastageRecordRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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


    // =========================================================
    // DASHBOARD
    // =========================================================

    public QualityDashboardStats getDashboardStats() {

        return new QualityDashboardStats(
                qualityInspectionRepository.count(),
                defectRecordRepository.count(),
                wastageRecordRepository.count(),
                qualityHoldRepository.count()
        );
    }


    // =========================================================
    // QUALITY INSPECTION
    // =========================================================

    public List<QualityInspection> getAllQualityInspections() {

        return qualityInspectionRepository.findAll(
                Sort.by(
                        Sort.Direction.DESC,
                        "inspectionId"
                )
        );
    }


    public QualityInspection getQualityInspection(Long id) {

        return qualityInspectionRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Quality Inspection not found: " + id
                        ));
    }


    public QualityInspection saveQualityInspection(
            QualityInspection qualityInspection) {

        if (qualityInspection.getPlanId() == null) {

            throw new IllegalArgumentException(
                    "Production Plan ID is required"
            );
        }

        if (qualityInspection.getStageName() == null
                || qualityInspection.getStageName().isBlank()) {

            throw new IllegalArgumentException(
                    "Stage Name is required"
            );
        }

        if (qualityInspection.getInspectionDate() == null) {

            throw new IllegalArgumentException(
                    "Inspection Date is required"
            );
        }

        if (qualityInspection.getStatus() == null
                || qualityInspection.getStatus().isBlank()) {

            qualityInspection.setStatus("Pending");
        }

        return qualityInspectionRepository.save(
                qualityInspection
        );
    }


    // Archive Quality Inspection
    public void archiveQualityInspection(Long id) {

        QualityInspection qualityInspection =
                getQualityInspection(id);

        qualityInspection.setStatus("Archived");

        qualityInspectionRepository.save(
                qualityInspection
        );
    }


    // Restore Quality Inspection
    public void restoreQualityInspection(Long id) {

        QualityInspection qualityInspection =
                getQualityInspection(id);

        qualityInspection.setStatus("Pending");

        qualityInspectionRepository.save(
                qualityInspection
        );
    }


    // Permanently Delete Quality Inspection
    public void deleteQualityInspection(Long id) {

        if (!qualityInspectionRepository.existsById(id)) {

            throw new IllegalArgumentException(
                    "Quality Inspection not found: " + id
            );
        }

        qualityInspectionRepository.deleteById(id);

        qualityInspectionRepository.flush();
    }


    // =========================================================
    // DEFECT RECORD
    // =========================================================

    public List<DefectRecord> getAllDefectRecords() {

        return defectRecordRepository.findAll(
                Sort.by(
                        Sort.Direction.DESC,
                        "defectId"
                )
        );
    }


    public DefectRecord getDefectRecord(Long id) {

        return defectRecordRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Defect Record not found: " + id
                        ));
    }


    public DefectRecord saveDefectRecord(
            DefectRecord defectRecord) {

        if (defectRecord.getInspectionId() == null) {

            throw new IllegalArgumentException(
                    "Inspection ID is required"
            );
        }

        if (defectRecord.getDefectType() == null
                || defectRecord.getDefectType().isBlank()) {

            throw new IllegalArgumentException(
                    "Defect Type is required"
            );
        }

        if (defectRecord.getSeverity() == null
                || defectRecord.getSeverity().isBlank()) {

            throw new IllegalArgumentException(
                    "Severity is required"
            );
        }

        if (defectRecord.getQuantity() == null
                || defectRecord.getQuantity() <= 0) {

            throw new IllegalArgumentException(
                    "Quantity must be greater than 0"
            );
        }

        return defectRecordRepository.save(
                defectRecord
        );
    }


    // Permanently Delete Defect Record
    public void deleteDefectRecord(Long id) {

        if (!defectRecordRepository.existsById(id)) {

            throw new IllegalArgumentException(
                    "Defect Record not found: " + id
            );
        }

        defectRecordRepository.deleteById(id);

        defectRecordRepository.flush();
    }


    // =========================================================
    // WASTAGE RECORD
    // =========================================================

    public List<WastageRecord> getAllWastageRecords() {

        return wastageRecordRepository.findAll(
                Sort.by(
                        Sort.Direction.DESC,
                        "wastageId"
                )
        );
    }


    public WastageRecord getWastageRecord(Long id) {

        return wastageRecordRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Wastage Record not found: " + id
                        ));
    }


    public WastageRecord saveWastageRecord(
            WastageRecord wastageRecord) {

        if (wastageRecord.getQuantity() == null) {

            throw new IllegalArgumentException(
                    "Quantity is required"
            );
        }

        if (wastageRecord.getUnit() == null
                || wastageRecord.getUnit().isBlank()) {

            throw new IllegalArgumentException(
                    "Unit is required"
            );
        }

        if (wastageRecord.getReason() == null
                || wastageRecord.getReason().isBlank()) {

            throw new IllegalArgumentException(
                    "Reason is required"
            );
        }

        if (wastageRecord.getApprovalStatus() == null
                || wastageRecord.getApprovalStatus().isBlank()) {

            wastageRecord.setApprovalStatus("Pending");
        }

        return wastageRecordRepository.save(
                wastageRecord
        );
    }


    // Archive Wastage Record
    public void archiveWastageRecord(Long id) {

        WastageRecord wastageRecord =
                getWastageRecord(id);

        wastageRecord.setApprovalStatus("Archived");

        wastageRecordRepository.save(
                wastageRecord
        );
    }


    // Restore Wastage Record
    public void restoreWastageRecord(Long id) {

        WastageRecord wastageRecord =
                getWastageRecord(id);

        wastageRecord.setApprovalStatus("Pending");

        wastageRecordRepository.save(
                wastageRecord
        );
    }


    // Permanently Delete Wastage Record
    public void deleteWastageRecord(Long id) {

        if (!wastageRecordRepository.existsById(id)) {

            throw new IllegalArgumentException(
                    "Wastage Record not found: " + id
            );
        }

        wastageRecordRepository.deleteById(id);

        wastageRecordRepository.flush();
    }


    // =========================================================
    // QUALITY HOLD
    // =========================================================

    public List<QualityHold> getAllQualityHolds() {

        return qualityHoldRepository.findAll(
                Sort.by(
                        Sort.Direction.DESC,
                        "holdId"
                )
        );
    }


    public QualityHold getQualityHold(Long id) {

        return qualityHoldRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Quality Hold not found: " + id
                        ));
    }


    public QualityHold saveQualityHold(
            QualityHold qualityHold) {

        if (qualityHold.getInspectionId() == null) {

            throw new IllegalArgumentException(
                    "Inspection ID is required"
            );
        }

        if (qualityHold.getReason() == null
                || qualityHold.getReason().isBlank()) {

            throw new IllegalArgumentException(
                    "Reason is required"
            );
        }

        if (qualityHold.getStatus() == null
                || qualityHold.getStatus().isBlank()) {

            qualityHold.setStatus("Pending");
        }

        return qualityHoldRepository.save(
                qualityHold
        );
    }


    // Archive Quality Hold
    public void archiveQualityHold(Long id) {

        QualityHold qualityHold =
                getQualityHold(id);

        qualityHold.setStatus("Archived");

        qualityHoldRepository.save(
                qualityHold
        );
    }


    // Restore Quality Hold
    public void restoreQualityHold(Long id) {

        QualityHold qualityHold =
                getQualityHold(id);

        qualityHold.setStatus("Pending");

        qualityHoldRepository.save(
                qualityHold
        );
    }


    // Permanently Delete Quality Hold
    public void deleteQualityHold(Long id) {

        if (!qualityHoldRepository.existsById(id)) {

            throw new IllegalArgumentException(
                    "Quality Hold not found: " + id
            );
        }

        qualityHoldRepository.deleteById(id);

        qualityHoldRepository.flush();
    }
}