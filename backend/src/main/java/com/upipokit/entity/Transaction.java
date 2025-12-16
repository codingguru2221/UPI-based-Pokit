package com.upipokit.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "merchant_name")
    private String merchantName;

    @Column(name = "merchant_upi_id")
    private String merchantUpiId;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Column(name = "parent_approval_required")
    private Boolean parentApprovalRequired;

    @Column(name = "parent_approved")
    private Boolean parentApproved;

    @Column(name = "transaction_time")
    private LocalDateTime transactionTime;

    private String description;

    public enum TransactionType {
        DEBIT, CREDIT
    }

    public enum TransactionStatus {
        PENDING, APPROVED, REJECTED
    }

    // Constructors
    public Transaction() {}

    public Transaction(Account account, String merchantName, String merchantUpiId, 
                      BigDecimal amount, Category category, TransactionType transactionType, 
                      String description) {
        this.account = account;
        this.merchantName = merchantName;
        this.merchantUpiId = merchantUpiId;
        this.amount = amount;
        this.category = category;
        this.transactionType = transactionType;
        this.description = description;
        this.status = TransactionStatus.PENDING;
        this.parentApprovalRequired = false;
        this.parentApproved = false;
        this.transactionTime = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public Boolean getParentApprovalRequired() {
        return parentApprovalRequired;
    }

    public void setParentApprovalRequired(Boolean parentApprovalRequired) {
        this.parentApprovalRequired = parentApprovalRequired;
    }

    public Boolean getParentApproved() {
        return parentApproved;
    }

    public void setParentApproved(Boolean parentApproved) {
        this.parentApproved = parentApproved;
    }

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(LocalDateTime transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}