package com.lankatex.smarttextile.inventory.service;

import com.lankatex.smarttextile.inventory.entity.MaterialCategory;
import com.lankatex.smarttextile.inventory.repository.MaterialCategoryRepository;
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

    public List<MaterialCategory> getActiveCategories() {
        return categoryRepository
                .findByStatusOrderByCategoryNameAsc("ACTIVE");
    }

    public List<MaterialCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    public MaterialCategory getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Material category not found"
                        ));
    }

    @Transactional
    public MaterialCategory save(MaterialCategory category) {

        if (category.getCategoryId() == null) {

            if (categoryRepository
                    .existsByCategoryNameIgnoreCase(
                            category.getCategoryName())) {

                throw new IllegalArgumentException(
                        "Category name already exists"
                );
            }

        } else {

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

    @Transactional
    public void archive(Long id) {

        MaterialCategory category = getById(id);

        category.setStatus("ARCHIVED");

        categoryRepository.save(category);
    }
}