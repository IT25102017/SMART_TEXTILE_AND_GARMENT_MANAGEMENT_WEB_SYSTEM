package com.lankatex.smarttextile.inventory.service;

import com.lankatex.smarttextile.inventory.entity.MaterialCategory;
import com.lankatex.smarttextile.inventory.repository.MaterialCategoryRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MaterialCategoryService {

    private final MaterialCategoryRepository categoryRepository;

    public MaterialCategoryService(
            MaterialCategoryRepository categoryRepository) {

        this.categoryRepository = categoryRepository;
    }


    // =====================================================
    // ACTIVE CATEGORIES
    // Used when creating new materials
    // =====================================================

    public List<MaterialCategory> getActiveCategories() {

        return categoryRepository
                .findByStatusOrderByCategoryNameAsc(
                        "ACTIVE"
                );
    }


    // =====================================================
    // ALL CATEGORIES
    // ACTIVE + ARCHIVED
    // =====================================================

    public List<MaterialCategory> getAllCategories() {

        return categoryRepository.findAll(
                Sort.by(
                        Sort.Direction.ASC,
                        "categoryName"
                )
        );
    }


    // =====================================================
    // FIND ONE
    // =====================================================

    public MaterialCategory getById(Long id) {

        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Material category not found"
                        ));
    }


    // =====================================================
    // CREATE / UPDATE
    // =====================================================

    @Transactional
    public MaterialCategory save(
            MaterialCategory category) {

        // CREATE
        if (category.getCategoryId() == null) {

            if (categoryRepository
                    .existsByCategoryNameIgnoreCase(
                            category.getCategoryName())) {

                throw new IllegalArgumentException(
                        "Category name already exists"
                );
            }

        } else {

            // UPDATE
            if (categoryRepository
                    .existsByCategoryNameIgnoreCaseAndCategoryIdNot(
                            category.getCategoryName(),
                            category.getCategoryId())) {

                throw new IllegalArgumentException(
                        "Category name already exists"
                );
            }
        }

        if (category.getStatus() == null
                || category.getStatus().isBlank()) {

            category.setStatus("ACTIVE");
        }

        return categoryRepository.save(category);
    }


    // =====================================================
    // ARCHIVE
    // ACTIVE -> ARCHIVED
    // =====================================================

    @Transactional
    public void archive(Long id) {

        MaterialCategory category =
                getById(id);

        category.setStatus("ARCHIVED");

        categoryRepository.save(category);
    }


    // =====================================================
    // RESTORE
    // ARCHIVED -> ACTIVE
    // =====================================================

    @Transactional
    public void restore(Long id) {

        MaterialCategory category =
                getById(id);

        category.setStatus("ACTIVE");

        categoryRepository.save(category);
    }


    // =====================================================
    // HARD DELETE
    // =====================================================

    @Transactional
    public void delete(Long id) {

        MaterialCategory category =
                categoryRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Material category not found"
                                ));

        try {

            categoryRepository.delete(category);

            categoryRepository.flush();

        } catch (DataIntegrityViolationException e) {

            throw new IllegalArgumentException(
                    "Cannot permanently delete this category because materials are already using it. Please Archive it instead."
            );
        }
    }
}