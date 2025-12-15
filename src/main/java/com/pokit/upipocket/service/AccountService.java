package com.pokit.upipocket.service;

import com.pokit.upipocket.model.Account;
import com.pokit.upipocket.model.CategoryLimit;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountService {
    Account createAccount(Account account);
    Optional<Account> getAccountById(Long id);
    Optional<Account> getAccountByUpiId(String upiId);
    List<Account> getAccountsByUserId(Long userId);
    Account updateAccount(Account account);
    void deleteAccount(Long id);
    
    // Banking simulation methods
    boolean allocateMonthlyPocketMoney(Long accountId, BigDecimal amount);
    boolean transferBetweenCategories(Long accountId, CategoryLimit.SpendingCategory fromCategory, 
                                     CategoryLimit.SpendingCategory toCategory, BigDecimal amount);
    BigDecimal getAvailableBalance(Long accountId);
    List<CategoryLimit> getCategoryLimits(Long accountId);
}