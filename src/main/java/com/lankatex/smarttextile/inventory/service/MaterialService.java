package com.lankatex.smarttextile.inventory.service;

import com.lankatex.smarttextile.inventory.entity.Material;
import com.lankatex.smarttextile.inventory.repository.MaterialBatchRepository;
import com.lankatex.smarttextile.inventory.repository.MaterialRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final MaterialBatchRepository batchRepository;

    public MaterialService(
            MaterialRepository materialRepository,
            MaterialBatchRepository batchRepository) {

        this.materialRepository = materialRepository;
        this.batchRepository = batchRepository;
    }

    // Get active materials
    public List<Material> getActiveMaterials(String keyword) {

        List<Material> materials;

        if (keyword == null || keyword.isBlank()) {

            materials =
                    materialRepository
                            .findByStatusOrderByMaterialNameAsc(
                                    "ACTIVE"
                            );

        } else {

            materials =
                    materialRepository.search(
                            keyword.trim()
                    );
        }

        // Calculate current stock for every material
        for (Material material : materials) {

            material.setCurrentQuantity(
                    getCurrentStock(
                            material.getMaterialId()
                    )
            );
        }

        return materials;
    }

    // Find one material using ID
    public Material getById(Long id) {

        Material material =
                materialRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Material not found"
                                ));

        material.setCurrentQuantity(
                getCurrentStock(id)
        );

        return material;
    }

    // Calculate current stock using material batches
    public BigDecimal getCurrentStock(Long materialId) {

        BigDecimal quantity =
                batchRepository
                        .calculateCurrentStock(materialId);

        return quantity == null
                ? BigDecimal.ZERO
                : quantity;
    }

    // Get materials where current stock is below reorder level
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

            material.setCurrentQuantity(current);

            BigDecimal reorder =
                    material.getReorderLevel() == null
                            ? BigDecimal.ZERO
                            : material.getReorderLevel();

            if (current.compareTo(reorder) <= 0) {

                lowStock.add(material);
            }
        }

        return lowStock;
    }

    // CREATE / UPDATE material
    @Transactional
    public Material save(Material material) {

        // Creating a new material
        if (material.getMaterialId() == null) {

            if (materialRepository
                    .existsByMaterialCodeIgnoreCase(
                            material.getMaterialCode())) {

                throw new IllegalArgumentException(
                        "Material code already exists"
                );
            }

        } else {

            // Updating existing material
            if (materialRepository
                    .existsByMaterialCodeIgnoreCaseAndMaterialIdNot(
                            material.getMaterialCode(),
                            material.getMaterialId())) {

                throw new IllegalArgumentException(
                        "Material code already exists"
                );
            }
        }

        // Default status
        if (material.getStatus() == null) {

            material.setStatus("ACTIVE");
        }

        return materialRepository.save(material);
    }

    // SOFT DELETE / ARCHIVE
    @Transactional
    public void archive(Long id) {

        Material material =
                getById(id);

        material.setStatus(
                "ARCHIVED"
        );

        materialRepository.save(material);
    }

    // HARD DELETE / PERMANENT DELETE
    @Transactional
    public void delete(Long id) {

        Material material =
                materialRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Material not found"
                                ));

        try {

            // Permanently remove the row
            materialRepository.delete(material);

            /*
             * Force Hibernate to execute DELETE now.
             * This allows us to catch Foreign Key errors
             * inside this method.
             */
            materialRepository.flush();

        } catch (DataIntegrityViolationException e) {

            /*
             * If this material is already used by
             * batches, stock movements or adjustments,
             * SQL Server will not allow permanent deletion.
             */
            throw new IllegalArgumentException(
                    "Cannot permanently delete this material because it is already used in inventory transactions. Please Archive it instead."
            );
        }
    }
}