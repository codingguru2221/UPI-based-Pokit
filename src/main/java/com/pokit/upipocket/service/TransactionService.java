package com.pokit.upipocket.service;

import com.pokit.upipocket.model.Transaction;
import java.util.List;
import java.util.Optional;

public interface TransactionService {
    Transaction createTransaction(Transaction transaction);
    Optional<Transaction> getTransactionById(Long id);
    List<Transaction> getTransactionsByAccountId(Long accountId);
    List<Transaction> getPendingTransactionsByAccountId(Long accountId);
    Transaction updateTransactionStatus(Long transactionId, Transaction.TransactionStatus status);
    void deleteTransaction(Long id);
    
    // Banking simulation methods
    Transaction processPayment(Long accountId, String merchantName, String merchantUpiId, 
                             java.math.BigDecimal amount, com.pokit.upipocket.model.CategoryLimit.SpendingCategory category);
    List<Transaction> getAllTransactions();
}