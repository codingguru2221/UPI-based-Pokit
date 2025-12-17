package com.upipokit.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pocket_accounts")
public class PocketAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "parent_account_id", nullable = false)
    private Account parentAccount;
    
    @OneToOne
    @JoinColumn(name = "child_user_id", nullable = false)
    private User childUser;
    
    @Column(name = "monthly_limit", precision = 10, scale = 2)
    private BigDecimal monthlyLimit;
    
    @Column(name = "current_balance", precision = 10, scale = 2)
    private BigDecimal currentBalance = BigDecimal.ZERO;

    @Column(name = "reset_date")
    private Integer resetDate; // Day of month when limits reset (1-31)
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public PocketAccount() {
    }

    public PocketAccount(Account parentAccount, User childUser, BigDecimal monthlyLimit, Integer resetDate) {
        this.parentAccount = parentAccount;
        this.childUser = childUser;
        this.monthlyLimit = monthlyLimit;
        this.currentBalance = monthlyLimit; // Initially set to full limit
        this.resetDate = resetDate;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Account getParentAccount() {
        return parentAccount;
    }

    public void setParentAccount(Account parentAccount) {
        this.parentAccount = parentAccount;
    }
    
    public User getChildUser() {
        return childUser;
    }

    public void setChildUser(User childUser) {
        this.childUser = childUser;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public BigDecimal getMonthlyLimit() {
        return monthlyLimit;
    }

    public void setMonthlyLimit(BigDecimal monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
    }
    
    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public Integer getResetDate() {
        return resetDate;
    }

    public void setResetDate(Integer resetDate) {
        this.resetDate = resetDate;
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
}