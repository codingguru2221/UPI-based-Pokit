package com.upipokit.dto;

import java.math.BigDecimal;

public class CategoryResponse {
    private Integer categoryId;
    private String categoryName;
    private BigDecimal allocatedLimit;
    private BigDecimal remainingLimit;
    private Boolean locked;

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

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

    public BigDecimal getRemainingLimit() {
        return remainingLimit;
    }

    public void setRemainingLimit(BigDecimal remainingLimit) {
        this.remainingLimit = remainingLimit;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }
}