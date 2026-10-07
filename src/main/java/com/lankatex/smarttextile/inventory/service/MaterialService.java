package com.lankatex.smarttextile.inventory.service;

import com.lankatex.smarttextile.inventory.entity.Material;
import com.lankatex.smarttextile.inventory.repository.MaterialBatchRepository;
import com.lankatex.smarttextile.inventory.repository.MaterialRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final MaterialBatchRepository batchRepository;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public MaterialService(
            MaterialRepository materialRepository,
            MaterialBatchRepository batchRepository) {

        this.materialRepository = materialRepository;
        this.batchRepository = batchRepository;
    }


    // =====================================================
    // READ - ALL MATERIALS
    // ACTIVE + ARCHIVED
    //
    // This method is used by the Materials page.
    // Archived materials must also be shown so that
    // the user can Restore them.
    // =====================================================

    public List<Material> getMaterials(String keyword) {

        List<Material> materials =
                materialRepository.findAll(
                        Sort.by(
                                Sort.Direction.ASC,
                                "materialName"
                        )
                );


        // ---------------------------------------------
        // SEARCH
        // Search using material code or material name
        // ---------------------------------------------

        if (keyword != null && !keyword.isBlank()) {

            String search =
                    keyword.trim()
                            .toLowerCase(Locale.ROOT);

            materials = materials.stream()
                    .filter(material -> {

                        String code =
                                material.getMaterialCode() == null
                                        ? ""
                                        : material
                                        .getMaterialCode()
                                        .toLowerCase(Locale.ROOT);


                        String name =
                                material.getMaterialName() == null
                                        ? ""
                                        : material
                                        .getMaterialName()
                                        .toLowerCase(Locale.ROOT);


                        return code.contains(search)
                                || name.contains(search);

                    })
                    .toList();
        }


        // ---------------------------------------------
        // Calculate current stock for every material
        // ---------------------------------------------

        for (Material material : materials) {

            material.setCurrentQuantity(
                    getCurrentStock(
                            material.getMaterialId()
                    )
            );
        }


        return materials;
    }


    // =====================================================
    // READ - ACTIVE MATERIALS ONLY
    //
    // IMPORTANT:
    // InventoryDashboardController and
    // MaterialBatchController use this method.
    //
    // Archived materials should NOT be selectable
    // when creating new batches / stock operations.
    // =====================================================

    public List<Material> getActiveMaterials(
            String keyword) {

        List<Material> materials =
                materialRepository
                        .findByStatusOrderByMaterialNameAsc(
                                "ACTIVE"
                        );


        // ---------------------------------------------
        // SEARCH
        // ---------------------------------------------

        if (keyword != null && !keyword.isBlank()) {

            String search =
                    keyword.trim()
                            .toLowerCase(Locale.ROOT);


            materials = materials.stream()
                    .filter(material -> {

                        String code =
                                material.getMaterialCode() == null
                                        ? ""
                                        : material
                                        .getMaterialCode()
                                        .toLowerCase(Locale.ROOT);


                        String name =
                                material.getMaterialName() == null
                                        ? ""
                                        : material
                                        .getMaterialName()
                                        .toLowerCase(Locale.ROOT);


                        return code.contains(search)
                                || name.contains(search);

                    })
                    .toList();
        }


        // ---------------------------------------------
        // Calculate stock
        // ---------------------------------------------

        for (Material material : materials) {

            material.setCurrentQuantity(
                    getCurrentStock(
                            material.getMaterialId()
                    )
            );
        }


        return materials;
    }


    // =====================================================
    // READ ONE MATERIAL BY ID
    // =====================================================

    public Material getById(Long id) {

        Material material =
                materialRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Material not found"
                                )
                        );


        material.setCurrentQuantity(
                getCurrentStock(id)
        );


        return material;
    }


    // =====================================================
    // CALCULATE CURRENT STOCK
    // =====================================================

    public BigDecimal getCurrentStock(
            Long materialId) {

        BigDecimal quantity =
                batchRepository
                        .calculateCurrentStock(
                                materialId
                        );


        if (quantity == null) {

            return BigDecimal.ZERO;
        }


        return quantity;
    }


    // =====================================================
    // LOW STOCK MATERIALS
    //
    // Only ACTIVE materials are checked.
    // Archived materials should not appear
    // in the Low Stock page.
    // =====================================================

    public List<Material> getLowStockMaterials() {

        List<Material> lowStock =
                new ArrayList<>();


        List<Material> materials =
                materialRepository
                        .findByStatusOrderByMaterialNameAsc(
                                "ACTIVE"
                        );


        for (Material material : materials) {

            BigDecimal current =
                    getCurrentStock(
                            material.getMaterialId()
                    );


            material.setCurrentQuantity(
                    current
            );


            BigDecimal reorderLevel =
                    material.getReorderLevel() == null
                            ? BigDecimal.ZERO
                            : material.getReorderLevel();


            if (current.compareTo(
                    reorderLevel) <= 0) {

                lowStock.add(material);
            }
        }


        return lowStock;
    }


    // =====================================================
    // CREATE / UPDATE MATERIAL
    // =====================================================

    @Transactional
    public Material save(
            Material material) {


        // ---------------------------------------------
        // CREATE
        // ---------------------------------------------

        if (material.getMaterialId() == null) {

            if (materialRepository
                    .existsByMaterialCodeIgnoreCase(
                            material.getMaterialCode()
                    )) {

                throw new IllegalArgumentException(
                        "Material code already exists"
                );
            }

        }


        // ---------------------------------------------
        // UPDATE
        // ---------------------------------------------

        else {

            if (materialRepository
                    .existsByMaterialCodeIgnoreCaseAndMaterialIdNot(
                            material.getMaterialCode(),
                            material.getMaterialId()
                    )) {

                throw new IllegalArgumentException(
                        "Material code already exists"
                );
            }
        }


        // ---------------------------------------------
        // New materials are ACTIVE by default
        // ---------------------------------------------

        if (material.getStatus() == null
                || material.getStatus().isBlank()) {

            material.setStatus(
                    "ACTIVE"
            );
        }


        return materialRepository.save(
                material
        );
    }


    // =====================================================
    // ARCHIVE
    //
    // SOFT DELETE
    //
    // ACTIVE -> ARCHIVED
    // =====================================================

    @Transactional
    public void archive(Long id) {

        Material material =
                getById(id);


        material.setStatus(
                "ARCHIVED"
        );


        materialRepository.save(
                material
        );
    }


    // =====================================================
    // RESTORE
    //
    // ARCHIVED -> ACTIVE
    // =====================================================

    @Transactional
    public void restore(Long id) {

        Material material =
                getById(id);


        material.setStatus(
                "ACTIVE"
        );


        materialRepository.save(
                material
        );
    }


    // =====================================================
    // HARD DELETE
    //
    // Permanently removes material from database.
    //
    // If the material is already referenced by
    // Batch / Movement / Adjustment records,
    // SQL Server Foreign Key protection may block delete.
    // =====================================================

    @Transactional
    public void delete(Long id) {

        Material material =
                materialRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Material not found"
                                )
                        );


        try {

            // Permanently delete material
            materialRepository.delete(
                    material
            );


            /*
             * Execute DELETE immediately.
             * This allows Foreign Key errors to be
             * caught inside this method.
             */
            materialRepository.flush();


        } catch (DataIntegrityViolationException e) {

            throw new IllegalArgumentException(
                    "Cannot permanently delete this material because it is already used in inventory transactions. Please Archive it instead."
            );
        }
    }
}