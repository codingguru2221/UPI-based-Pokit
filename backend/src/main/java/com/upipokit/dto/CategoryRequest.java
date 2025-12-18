package com.upipokit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class CategoryRequest {
    @NotBlank
    private String categoryName;
    
    @NotNull
    @Positive
    private BigDecimal allocatedLimit;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getAllocatedLimit() {
        return allocatedLimit;
    }

    public void setAllocatedLimit(BigDecimal allocatedLimit) {
        this.allocatedLimit = allocatedLimit;
    }
}