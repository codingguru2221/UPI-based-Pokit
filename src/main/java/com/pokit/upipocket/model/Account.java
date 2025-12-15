package com.pokit.upipocket.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String upiId;
    private String accountName;
    private BigDecimal balance;
    private BigDecimal monthlyPocketMoney;
    private LocalDateTime lastAllocationDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CategoryLimit> categoryLimits;
    
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions;
    
    private boolean isActive = true;
    
    // Default constructor
    public Account() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUpiId() {
        return upiId;
    }

    public void setUpiId(String upiId) {
        this.upiId = upiId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getMonthlyPocketMoney() {
        return monthlyPocketMoney;
    }

    public void setMonthlyPocketMoney(BigDecimal monthlyPocketMoney) {
        this.monthlyPocketMoney = monthlyPocketMoney;
    }

    public LocalDateTime getLastAllocationDate() {
        return lastAllocationDate;
    }

    public void setLastAllocationDate(LocalDateTime lastAllocationDate) {
        this.lastAllocationDate = lastAllocationDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<CategoryLimit> getCategoryLimits() {
        return categoryLimits;
    }

    public void setCategoryLimits(List<CategoryLimit> categoryLimits) {
        this.categoryLimits = categoryLimits;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}