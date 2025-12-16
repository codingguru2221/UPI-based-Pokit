package com.upipokit.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "category_limits")
public class CategoryLimit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pocket_account_id", nullable = false)
    private PocketAccount pocketAccount;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "limit_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal limitAmount;

    @Column(name = "current_spent", precision = 10, scale = 2, nullable = false)
    private BigDecimal currentSpent;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public CategoryLimit() {}

    public CategoryLimit(PocketAccount pocketAccount, Category category, BigDecimal limitAmount) {
        this.pocketAccount = pocketAccount;
        this.category = category;
        this.limitAmount = limitAmount;
        this.currentSpent = BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PocketAccount getPocketAccount() {
        return pocketAccount;
    }

    public void setPocketAccount(PocketAccount pocketAccount) {
        this.pocketAccount = pocketAccount;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public BigDecimal getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(BigDecimal limitAmount) {
        this.limitAmount = limitAmount;
    }

    public BigDecimal getCurrentSpent() {
        return currentSpent;
    }

    public void setCurrentSpent(BigDecimal currentSpent) {
        this.currentSpent = currentSpent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Check if the category limit has been exceeded
     * @return true if current spent is greater than or equal to limit amount
     */
    public boolean isLimitExceeded() {
        return currentSpent.compareTo(limitAmount) >= 0;
    }

    /**
     * Check if there is enough balance in this category for a transaction
     * @param amount the amount to check
     * @return true if there is enough balance
     */
    public boolean hasSufficientBalance(BigDecimal amount) {
        return currentSpent.add(amount).compareTo(limitAmount) <= 0;
    }

    /**
     * Add amount to current spent (when a transaction is made)
     * @param amount the amount to add
     */
    public void addToSpent(BigDecimal amount) {
        this.currentSpent = this.currentSpent.add(amount);
    }

    /**
     * Subtract amount from current spent (when a transaction is refunded)
     * @param amount the amount to subtract
     */
    public void subtractFromSpent(BigDecimal amount) {
        this.currentSpent = this.currentSpent.subtract(amount);
    }
}