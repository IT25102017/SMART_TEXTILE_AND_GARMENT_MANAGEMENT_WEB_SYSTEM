package com.lankatex.smarttextile.inventory.repository;

import com.lankatex.smarttextile.inventory.entity.MaterialCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaterialCategoryRepository
        extends JpaRepository<MaterialCategory, Long> {

    boolean existsByCategoryNameIgnoreCase(String categoryName);

    boolean existsByCategoryNameIgnoreCaseAndCategoryIdNot(
            String categoryName,
            Long categoryId
    );

    List<MaterialCategory> findByStatusOrderByCategoryNameAsc(String status);
}