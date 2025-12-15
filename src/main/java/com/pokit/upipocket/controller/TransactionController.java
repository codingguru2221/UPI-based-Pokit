package com.pokit.upipocket.controller;

import com.pokit.upipocket.model.Transaction;
import com.pokit.upipocket.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {
    
    @Autowired
    private TransactionService transactionService;
    
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
        Transaction createdTransaction = transactionService.createTransaction(transaction);
        return ResponseEntity.ok(createdTransaction);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        Optional<Transaction> transaction = transactionService.getTransactionById(id);
        if (transaction.isPresent()) {
            return ResponseEntity.ok(transaction.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<Transaction>> getTransactionsByAccountId(@PathVariable Long accountId) {
        List<Transaction> transactions = transactionService.getTransactionsByAccountId(accountId);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/account/{accountId}/pending")
    public ResponseEntity<List<Transaction>> getPendingTransactionsByAccountId(@PathVariable Long accountId) {
        List<Transaction> transactions = transactionService.getPendingTransactionsByAccountId(accountId);
        return ResponseEntity.ok(transactions);
    }
    
    @PutMapping("/{transactionId}/status")
    public ResponseEntity<Transaction> updateTransactionStatus(@PathVariable Long transactionId,
                                                             @RequestParam Transaction.TransactionStatus status) {
        try {
            Transaction updatedTransaction = transactionService.updateTransactionStatus(transactionId, status);
            return ResponseEntity.ok(updatedTransaction);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/process-payment")
    public ResponseEntity<Transaction> processPayment(@RequestParam Long accountId,
                                                   @RequestParam String merchantName,
                                                   @RequestParam String merchantUpiId,
                                                   @RequestParam BigDecimal amount,
                                                   @RequestParam com.pokit.upipocket.model.CategoryLimit.SpendingCategory category) {
        Transaction transaction = transactionService.processPayment(accountId, merchantName, merchantUpiId, amount, category);
        return ResponseEntity.ok(transaction);
    }
    
    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }
}