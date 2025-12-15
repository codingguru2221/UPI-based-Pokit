package com.pokit.upipocket.model;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "category_limits")
public class CategoryLimit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private SpendingCategory category;
    private BigDecimal limitAmount;
    private BigDecimal spentAmount;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;
    
    // Default constructor
    public CategoryLimit() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SpendingCategory getCategory() {
        return category;
    }

    public void setCategory(SpendingCategory category) {
        this.category = category;
    }

    public BigDecimal getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(BigDecimal limitAmount) {
        this.limitAmount = limitAmount;
    }

    public BigDecimal getSpentAmount() {
        return spentAmount;
    }

    public void setSpentAmount(BigDecimal spentAmount) {
        this.spentAmount = spentAmount;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
    
    public enum SpendingCategory {
        FOOD,
        TRAVEL,
        ONLINE_SHOPPING,
        ENTERTAINMENT,
        EDUCATION,
        OTHERS
    }
    
    // Helper method to get remaining amount for this category
    public BigDecimal getRemainingAmount() {
        return limitAmount.subtract(spentAmount);
    }
    
    // Helper method to check if limit is exceeded
    public boolean isLimitExceeded() {
        return spentAmount.compareTo(limitAmount) > 0;
    }
}