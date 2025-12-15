package com.pokit.upipocket.service;

import com.pokit.upipocket.model.Account;
import com.pokit.upipocket.model.CategoryLimit;
import com.pokit.upipocket.repository.AccountRepository;
import com.pokit.upipocket.repository.CategoryLimitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private CategoryLimitRepository categoryLimitRepository;
    
    @Override
    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }
    
    @Override
    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }
    
    @Override
    public Optional<Account> getAccountByUpiId(String upiId) {
        return accountRepository.findByUpiId(upiId);
    }
    
    @Override
    public List<Account> getAccountsByUserId(Long userId) {
        return accountRepository.findByUserId(userId);
    }
    
    @Override
    public Account updateAccount(Account account) {
        return accountRepository.save(account);
    }
    
    @Override
    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }
    
    @Override
    public boolean allocateMonthlyPocketMoney(Long accountId, BigDecimal amount) {
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        if (!accountOpt.isPresent()) {
            return false;
        }
        
        Account account = accountOpt.get();
        account.setBalance(amount);
        account.setMonthlyPocketMoney(amount);
        account.setLastAllocationDate(LocalDateTime.now());
        
        // Save the updated account
        accountRepository.save(account);
        return true;
    }
    
    @Override
    public boolean transferBetweenCategories(Long accountId, CategoryLimit.SpendingCategory fromCategory, 
                                           CategoryLimit.SpendingCategory toCategory, BigDecimal amount) {
        // Get all category limits for this account
        List<CategoryLimit> categoryLimits = categoryLimitRepository.findByAccountId(accountId);
        
        CategoryLimit fromCatLimit = null;
        CategoryLimit toCatLimit = null;
        
        // Find the from and to category limits
        for (CategoryLimit limit : categoryLimits) {
            if (limit.getCategory() == fromCategory) {
                fromCatLimit = limit;
            }
            if (limit.getCategory() == toCategory) {
                toCatLimit = limit;
            }
        }
        
        // Check if both categories exist
        if (fromCatLimit == null || toCatLimit == null) {
            return false;
        }
        
        // Check if there's enough in the from category
        if (fromCatLimit.getRemainingAmount().compareTo(amount) < 0) {
            return false;
        }
        
        // Transfer the amount
        fromCatLimit.setSpentAmount(fromCatLimit.getSpentAmount().add(amount));
        toCatLimit.setSpentAmount(toCatLimit.getSpentAmount().subtract(amount));
        
        // Save the updated category limits
        categoryLimitRepository.save(fromCatLimit);
        categoryLimitRepository.save(toCatLimit);
        
        return true;
    }
    
    @Override
    public BigDecimal getAvailableBalance(Long accountId) {
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        return accountOpt.map(Account::getBalance).orElse(BigDecimal.ZERO);
    }
    
    @Override
    public List<CategoryLimit> getCategoryLimits(Long accountId) {
        return categoryLimitRepository.findByAccountId(accountId);
    }
}