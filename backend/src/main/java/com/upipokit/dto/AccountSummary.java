package com.upipokit.dto;

import com.upipokit.entity.Account;
import com.upipokit.entity.PocketAccount;
import com.upipokit.entity.CategoryLimit;
import com.upipokit.entity.Transaction;

import java.math.BigDecimal;
import java.util.List;

public class AccountSummary {
    private Account mainAccount;
    private PocketAccount pocketAccount;
    private List<CategoryLimit> categoryLimits;
    private List<Transaction> recentTransactions;

    public AccountSummary(Account mainAccount, PocketAccount pocketAccount, 
                         List<CategoryLimit> categoryLimits, List<Transaction> recentTransactions) {
        this.mainAccount = mainAccount;
        this.pocketAccount = pocketAccount;
        this.categoryLimits = categoryLimits;
        this.recentTransactions = recentTransactions;
    }

    // Getters and Setters
    public Account getMainAccount() {
        return mainAccount;
    }

    public void setMainAccount(Account mainAccount) {
        this.mainAccount = mainAccount;
    }

    public PocketAccount getPocketAccount() {
        return pocketAccount;
    }

    public void setPocketAccount(PocketAccount pocketAccount) {
        this.pocketAccount = pocketAccount;
    }

    public List<CategoryLimit> getCategoryLimits() {
        return categoryLimits;
    }

    public void setCategoryLimits(List<CategoryLimit> categoryLimits) {
        this.categoryLimits = categoryLimits;
    }

    public List<Transaction> getRecentTransactions() {
        return recentTransactions;
    }

    public void setRecentTransactions(List<Transaction> recentTransactions) {
        this.recentTransactions = recentTransactions;
    }

    public BigDecimal getTotalBalance() {
        return mainAccount != null ? mainAccount.getBalance() : BigDecimal.ZERO;
    }

    public BigDecimal getPocketBalance() {
        return pocketAccount != null ? pocketAccount.getAccount().getBalance() : BigDecimal.ZERO;
    }
}