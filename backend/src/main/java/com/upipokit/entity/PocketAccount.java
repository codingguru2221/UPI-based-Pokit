package com.upipokit.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "pocket_accounts")
public class PocketAccount extends Account {
    @OneToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "monthly_limit", precision = 10, scale = 2)
    private BigDecimal monthlyLimit;

    @Column(name = "reset_date")
    private Integer resetDate; // Day of month when limits reset (1-31)

    // Constructors
    public PocketAccount() {
        super();
    }

    public PocketAccount(Account account, BigDecimal monthlyLimit, Integer resetDate) {
        this.account = account;
        this.monthlyLimit = monthlyLimit;
        this.resetDate = resetDate;
    }

    // Getters and Setters
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public BigDecimal getMonthlyLimit() {
        return monthlyLimit;
    }

    public void setMonthlyLimit(BigDecimal monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
    }

    public Integer getResetDate() {
        return resetDate;
    }

    public void setResetDate(Integer resetDate) {
        this.resetDate = resetDate;
    }
}