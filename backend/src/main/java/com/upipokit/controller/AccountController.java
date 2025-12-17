package com.upipokit.controller;

import com.upipokit.dto.AccountSummary;
import com.upipokit.entity.PocketAccount;
import com.upipokit.entity.Transaction;
import com.upipokit.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    
    @Autowired
    AccountService accountService;
    
    /**
     * Create a pocket account for a child
     */
    @PostMapping("/pocket/{childId}")
    public ResponseEntity<?> createPocketAccount(@PathVariable Long childId, 
                                             @RequestParam Long parentId,
                                             @RequestParam BigDecimal monthlyLimit) {
        try {
            PocketAccount pocketAccount = accountService.createPocketAccount(childId, parentId, monthlyLimit);
            return ResponseEntity.ok(pocketAccount);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Process a transaction
     */
    @PostMapping("/transaction/{childId}")
    public ResponseEntity<?> processTransaction(@PathVariable Long childId,
                                              @RequestParam String merchantName,
                                              @RequestParam String merchantUpiId,
                                              @RequestParam BigDecimal amount,
                                              @RequestParam String categoryName,
                                              @RequestParam String description) {
        try {
            Transaction transaction = accountService.processTransaction(childId, merchantName, merchantUpiId, 
                                                                     amount, categoryName, description);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Approve a pending transaction
     */
    @PutMapping("/transaction/{transactionId}/approve")
    public ResponseEntity<?> approveTransaction(@PathVariable Long transactionId, @RequestParam Long parentId) {
        try {
            Transaction transaction = accountService.approveTransaction(transactionId, parentId);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Reject a pending transaction
     */
    @PutMapping("/transaction/{transactionId}/reject")
    public ResponseEntity<?> rejectTransaction(@PathVariable Long transactionId, @RequestParam Long parentId) {
        try {
            Transaction transaction = accountService.rejectTransaction(transactionId, parentId);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Get account summary
     */
    @GetMapping("/summary/{userId}")
    public ResponseEntity<?> getAccountSummary(@PathVariable Long userId) {
        try {
            AccountSummary summary = accountService.getAccountSummary(userId);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Reallocate limits between categories
     */
    @PutMapping("/reallocate")
    public ResponseEntity<?> reallocateLimits(@RequestParam Long pocketAccountId,
                                            @RequestParam Long fromCategoryId,
                                            @RequestParam Long toCategoryId,
                                            @RequestParam BigDecimal amount) {
        try {
            boolean success = accountService.reallocateLimits(pocketAccountId, fromCategoryId, toCategoryId, amount);
            return ResponseEntity.ok(success);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Check if a child is eligible for account conversion
     */
    @GetMapping("/conversion/eligible/{childId}")
    public ResponseEntity<?> isChildEligibleForConversion(@PathVariable Long childId) {
        try {
            boolean eligible = accountService.isChildEligibleForConversion(childId);
            return ResponseEntity.ok(eligible);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Convert a child account to an independent account
     */
    @PostMapping("/conversion/convert/{childId}")
    public ResponseEntity<?> convertChildToIndependent(@PathVariable Long childId) {
        try {
            boolean success = accountService.convertChildToIndependent(childId);
            return ResponseEntity.ok(success);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}