package com.upipokit.service;

import com.upipokit.dto.CategoryRequest;
import com.upipokit.dto.CategoryResponse;
import com.upipokit.entity.Category;
import com.upipokit.entity.Child;
import com.upipokit.repository.CategoryRepository;
import com.upipokit.repository.ChildRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ChildRepository childRepository;

    public CategoryResponse createCategory(Integer childId, CategoryRequest req) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("Child not found"));

        Category category = new Category();
        category.setChild(child);
        category.setCategoryName(req.getCategoryName());
        category.setAllocatedLimit(req.getAllocatedLimit());
        category.setRemainingLimit(req.getAllocatedLimit()); // Initially remaining equals allocated
        category.setLocked(false);

        Category saved = categoryRepository.save(category);
        return toDto(saved);
    }

    public List<CategoryResponse> getCategoriesByChildId(Integer childId) {
        return categoryRepository.findByChildChildId(childId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public CategoryResponse getCategoryById(Integer categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        return toDto(category);
    }

    public CategoryResponse updateRemainingLimit(Integer categoryId, BigDecimal newRemainingLimit) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        category.setRemainingLimit(newRemainingLimit);
        Category saved = categoryRepository.save(category);
        return toDto(saved);
    }

    public CategoryResponse lockCategory(Integer categoryId, Boolean locked) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        category.setLocked(locked);
        Category saved = categoryRepository.save(category);
        return toDto(saved);
    }

    private CategoryResponse toDto(Category category) {
        CategoryResponse dto = new CategoryResponse();
        dto.setCategoryId(category.getCategoryId());
        dto.setCategoryName(category.getCategoryName());
        dto.setAllocatedLimit(category.getAllocatedLimit());
        dto.setRemainingLimit(category.getRemainingLimit());
        dto.setLocked(category.getLocked());
        return dto;
    }
}