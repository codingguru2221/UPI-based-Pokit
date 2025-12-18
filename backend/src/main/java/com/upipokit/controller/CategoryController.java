package com.upipokit.controller;

import com.upipokit.dto.CategoryRequest;
import com.upipokit.dto.CategoryResponse;
import com.upipokit.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/child/{childId}")
    public ResponseEntity<?> createCategory(@PathVariable Integer childId, @Valid @RequestBody CategoryRequest req) {
        try {
            CategoryResponse response = categoryService.createCategory(childId, req);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @GetMapping("/child/{childId}")
    public ResponseEntity<?> getCategoriesByChildId(@PathVariable Integer childId) {
        try {
            List<CategoryResponse> response = categoryService.getCategoriesByChildId(childId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<?> getCategoryById(@PathVariable Integer categoryId) {
        try {
            CategoryResponse response = categoryService.getCategoryById(categoryId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{categoryId}/remaining-limit")
    public ResponseEntity<?> updateRemainingLimit(@PathVariable Integer categoryId, @RequestParam Double newRemainingLimit) {
        try {
            CategoryResponse response = categoryService.updateRemainingLimit(categoryId, 
                new java.math.BigDecimal(newRemainingLimit.toString()));
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{categoryId}/lock")
    public ResponseEntity<?> lockCategory(@PathVariable Integer categoryId, @RequestParam Boolean locked) {
        try {
            CategoryResponse response = categoryService.lockCategory(categoryId, locked);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }
}