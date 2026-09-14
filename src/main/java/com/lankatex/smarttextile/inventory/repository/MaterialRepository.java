package com.lankatex.smarttextile.inventory.repository;

import com.lankatex.smarttextile.inventory.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MaterialRepository extends JpaRepository<Material, Long> {

    boolean existsByMaterialCodeIgnoreCase(String materialCode);

    boolean existsByMaterialCodeIgnoreCaseAndMaterialIdNot(
            String materialCode,
            Long materialId
    );

    List<Material> findByStatusOrderByMaterialNameAsc(String status);

    @Query("""
            SELECT m FROM Material m
            WHERE m.status = 'ACTIVE'
            AND (
                LOWER(m.materialCode) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(m.materialName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
            ORDER BY m.materialName
            """)
    List<Material> search(@Param("keyword") String keyword);
}