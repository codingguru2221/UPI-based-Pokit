package com.pokit.upipocket.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String transactionId;
    private String merchantName;
    private String merchantUpiId;
    private BigDecimal amount;
    private CategoryLimit.SpendingCategory category;
    private TransactionStatus status;
    private String remarks;
    private LocalDateTime transactionDateTime;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;
    
    // Default constructor
    public Transaction() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchantUpiId() {
        return merchantUpiId;
    }

    public void setMerchantUpiId(String merchantUpiId) {
        this.merchantUpiId = merchantUpiId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public CategoryLimit.SpendingCategory getCategory() {
        return category;
    }

    public void setCategory(CategoryLimit.SpendingCategory category) {
        this.category = category;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getTransactionDateTime() {
        return transactionDateTime;
    }

    public void setTransactionDateTime(LocalDateTime transactionDateTime) {
        this.transactionDateTime = transactionDateTime;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
    
    public enum TransactionStatus {
        PENDING,
        APPROVED,
        REJECTED,
        COMPLETED,
        FAILED
    }
}