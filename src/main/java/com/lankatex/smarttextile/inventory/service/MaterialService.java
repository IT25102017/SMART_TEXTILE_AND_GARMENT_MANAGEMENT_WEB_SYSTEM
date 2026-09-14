package com.lankatex.smarttextile.inventory.service;

import com.lankatex.smarttextile.inventory.entity.Material;
import com.lankatex.smarttextile.inventory.repository.MaterialBatchRepository;
import com.lankatex.smarttextile.inventory.repository.MaterialRepository;
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

    public List<Material> getActiveMaterials(String keyword) {

        List<Material> materials;

        if (keyword == null || keyword.isBlank()) {
            materials =
                    materialRepository
                            .findByStatusOrderByMaterialNameAsc(
                                    "ACTIVE"
                            );
        } else {
            materials = materialRepository.search(keyword.trim());
        }

        for (Material material : materials) {
            material.setCurrentQuantity(
                    getCurrentStock(material.getMaterialId())
            );
        }

        return materials;
    }

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

    public BigDecimal getCurrentStock(Long materialId) {

        BigDecimal quantity =
                batchRepository
                        .calculateCurrentStock(materialId);

        return quantity == null
                ? BigDecimal.ZERO
                : quantity;
    }

    public List<Material> getLowStockMaterials() {

        List<Material> lowStock = new ArrayList<>();

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

    @Transactional
    public Material save(Material material) {

        if (material.getMaterialId() == null) {

            if (materialRepository
                    .existsByMaterialCodeIgnoreCase(
                            material.getMaterialCode())) {

                throw new IllegalArgumentException(
                        "Material code already exists"
                );
            }

        } else {

            if (materialRepository
                    .existsByMaterialCodeIgnoreCaseAndMaterialIdNot(
                            material.getMaterialCode(),
                            material.getMaterialId())) {

                throw new IllegalArgumentException(
                        "Material code already exists"
                );
            }
        }

        if (material.getStatus() == null) {
            material.setStatus("ACTIVE");
        }

        return materialRepository.save(material);
    }

    @Transactional
    public void archive(Long id) {

        Material material = getById(id);

        material.setStatus("ARCHIVED");

        materialRepository.save(material);
    }
}