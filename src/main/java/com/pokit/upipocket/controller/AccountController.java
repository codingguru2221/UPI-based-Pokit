package com.pokit.upipocket.controller;

import com.pokit.upipocket.model.Account;
import com.pokit.upipocket.model.CategoryLimit;
import com.pokit.upipocket.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*")
public class AccountController {
    
    @Autowired
    private AccountService accountService;
    
    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        Account createdAccount = accountService.createAccount(account);
        return ResponseEntity.ok(createdAccount);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        Optional<Account> account = accountService.getAccountById(id);
        if (account.isPresent()) {
            return ResponseEntity.ok(account.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/upi/{upiId}")
    public ResponseEntity<Account> getAccountByUpiId(@PathVariable String upiId) {
        Optional<Account> account = accountService.getAccountByUpiId(upiId);
        if (account.isPresent()) {
            return ResponseEntity.ok(account.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Account>> getAccountsByUserId(@PathVariable Long userId) {
        List<Account> accounts = accountService.getAccountsByUserId(userId);
        return ResponseEntity.ok(accounts);
    }
    
    @PutMapping
    public ResponseEntity<Account> updateAccount(@RequestBody Account account) {
        Account updatedAccount = accountService.updateAccount(account);
        return ResponseEntity.ok(updatedAccount);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{accountId}/allocate")
    public ResponseEntity<Boolean> allocateMonthlyPocketMoney(@PathVariable Long accountId, 
                                                              @RequestParam BigDecimal amount) {
        boolean result = accountService.allocateMonthlyPocketMoney(accountId, amount);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/{accountId}/transfer")
    public ResponseEntity<Boolean> transferBetweenCategories(@PathVariable Long accountId,
                                                            @RequestParam CategoryLimit.SpendingCategory fromCategory,
                                                            @RequestParam CategoryLimit.SpendingCategory toCategory,
                                                            @RequestParam BigDecimal amount) {
        boolean result = accountService.transferBetweenCategories(accountId, fromCategory, toCategory, amount);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{accountId}/balance")
    public ResponseEntity<BigDecimal> getAvailableBalance(@PathVariable Long accountId) {
        BigDecimal balance = accountService.getAvailableBalance(accountId);
        return ResponseEntity.ok(balance);
    }
    
    @GetMapping("/{accountId}/categories")
    public ResponseEntity<List<CategoryLimit>> getCategoryLimits(@PathVariable Long accountId) {
        List<CategoryLimit> categoryLimits = accountService.getCategoryLimits(accountId);
        return ResponseEntity.ok(categoryLimits);
    }
}