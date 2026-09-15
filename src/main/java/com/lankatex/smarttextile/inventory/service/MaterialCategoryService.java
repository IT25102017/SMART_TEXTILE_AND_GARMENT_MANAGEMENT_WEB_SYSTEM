package com.lankatex.smarttextile.inventory.service;

import com.lankatex.smarttextile.inventory.entity.MaterialCategory;
import com.lankatex.smarttextile.inventory.repository.MaterialCategoryRepository;
import org.springframework.dao.DataIntegrityViolationException;
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

    // READ - Active categories only
    public List<MaterialCategory> getActiveCategories() {

        return categoryRepository
                .findByStatusOrderByCategoryNameAsc("ACTIVE");
    }

    // READ - All categories
    public List<MaterialCategory> getAllCategories() {

        return categoryRepository.findAll();
    }

    // READ - Find one category by ID
    public MaterialCategory getById(Long id) {

        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Material category not found"
                        ));
    }

    // CREATE / UPDATE
    @Transactional
    public MaterialCategory save(
            MaterialCategory category) {

        if (category.getCategoryId() == null) {

            // CREATE validation
            if (categoryRepository
                    .existsByCategoryNameIgnoreCase(
                            category.getCategoryName())) {

                throw new IllegalArgumentException(
                        "Category name already exists"
                );
            }

        } else {

            // UPDATE validation
            if (categoryRepository
                    .existsByCategoryNameIgnoreCaseAndCategoryIdNot(
                            category.getCategoryName(),
                            category.getCategoryId())) {

                throw new IllegalArgumentException(
                        "Category name already exists"
                );
            }
        }

        if (category.getStatus() == null) {

            category.setStatus("ACTIVE");
        }

        return categoryRepository.save(category);
    }

    // SOFT DELETE / ARCHIVE
    @Transactional
    public void archive(Long id) {

        MaterialCategory category =
                getById(id);

        category.setStatus("ARCHIVED");

        categoryRepository.save(category);
    }

    // HARD DELETE / PERMANENT DELETE
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

            // Force SQL DELETE immediately
            categoryRepository.flush();

        } catch (DataIntegrityViolationException e) {

            /*
             * Category එක Materials වලට use වෙලා නම්
             * Foreign Key එක නිසා delete කරන්න බැහැ.
             */
            throw new IllegalArgumentException(
                    "Cannot permanently delete this category because materials are already using it. Please Archive it instead."
            );
        }
    }
}