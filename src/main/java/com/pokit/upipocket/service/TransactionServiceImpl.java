package com.pokit.upipocket.service;

import com.pokit.upipocket.model.Account;
import com.pokit.upipocket.model.CategoryLimit;
import com.pokit.upipocket.model.Transaction;
import com.pokit.upipocket.repository.AccountRepository;
import com.pokit.upipocket.repository.CategoryLimitRepository;
import com.pokit.upipocket.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private CategoryLimitRepository categoryLimitRepository;
    
    @Override
    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }
    
    @Override
    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }
    
    @Override
    public List<Transaction> getTransactionsByAccountId(Long accountId) {
        return transactionRepository.findByAccountId(accountId);
    }
    
    @Override
    public List<Transaction> getPendingTransactionsByAccountId(Long accountId) {
        return transactionRepository.findByAccountIdAndStatus(accountId, Transaction.TransactionStatus.PENDING);
    }
    
    @Override
    public Transaction updateTransactionStatus(Long transactionId, Transaction.TransactionStatus status) {
        Optional<Transaction> transactionOpt = transactionRepository.findById(transactionId);
        if (!transactionOpt.isPresent()) {
            throw new RuntimeException("Transaction not found with id: " + transactionId);
        }
        
        Transaction transaction = transactionOpt.get();
        transaction.setStatus(status);
        
        if (status == Transaction.TransactionStatus.APPROVED || status == Transaction.TransactionStatus.COMPLETED) {
            // If approved, process the payment
            processApprovedTransaction(transaction);
        }
        
        return transactionRepository.save(transaction);
    }
    
    @Override
    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }
    
    @Override
    public Transaction processPayment(Long accountId, String merchantName, String merchantUpiId, 
                                   BigDecimal amount, CategoryLimit.SpendingCategory category) {
        // Create a new transaction
        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setMerchantName(merchantName);
        transaction.setMerchantUpiId(merchantUpiId);
        transaction.setAmount(amount);
        transaction.setCategory(category); // This now uses CategoryLimit.SpendingCategory
        transaction.setTransactionDateTime(LocalDateTime.now());
        
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        if (!accountOpt.isPresent()) {
            transaction.setStatus(Transaction.TransactionStatus.FAILED);
            transaction.setRemarks("Account not found");
            return transactionRepository.save(transaction);
        }
        
        Account account = accountOpt.get();
        transaction.setAccount(account);
        
        // Check if there's enough balance in the account
        if (account.getBalance().compareTo(amount) < 0) {
            transaction.setStatus(Transaction.TransactionStatus.FAILED);
            transaction.setRemarks("Insufficient balance");
            return transactionRepository.save(transaction);
        }
        
        // Get the category limit for this category
        List<CategoryLimit> categoryLimits = categoryLimitRepository.findByAccountIdAndCategory(accountId, category);
        if (categoryLimits.isEmpty()) {
            transaction.setStatus(Transaction.TransactionStatus.FAILED);
            transaction.setRemarks("Category limit not found");
            return transactionRepository.save(transaction);
        }
        
        CategoryLimit categoryLimit = categoryLimits.get(0);
        
        // Check if the category limit is exceeded
        if (categoryLimit.getRemainingAmount().compareTo(amount) < 0) {
            // Check if there's still balance in other categories
            BigDecimal totalSpent = BigDecimal.ZERO;
            BigDecimal totalLimit = BigDecimal.ZERO;
            
            List<CategoryLimit> allCategoryLimits = categoryLimitRepository.findByAccountId(accountId);
            for (CategoryLimit limit : allCategoryLimits) {
                totalSpent = totalSpent.add(limit.getSpentAmount());
                totalLimit = totalLimit.add(limit.getLimitAmount());
            }
            
            // If total spent is less than total limit, allow the transaction but require parent approval
            if (totalSpent.add(amount).compareTo(totalLimit) <= 0) {
                transaction.setStatus(Transaction.TransactionStatus.PENDING);
                transaction.setRemarks("Category limit exceeded but total balance available. Pending parent approval.");
            } else {
                transaction.setStatus(Transaction.TransactionStatus.FAILED);
                transaction.setRemarks("Both category limit and total balance exceeded");
            }
        } else {
            // Sufficient funds in category, process transaction immediately
            transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
            transaction.setRemarks("Transaction successful");
            
            // Update account balance
            account.setBalance(account.getBalance().subtract(amount));
            accountRepository.save(account);
            
            // Update category limit
            categoryLimit.setSpentAmount(categoryLimit.getSpentAmount().add(amount));
            categoryLimitRepository.save(categoryLimit);
        }
        
        return transactionRepository.save(transaction);
    }
    
    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
    
    private void processApprovedTransaction(Transaction transaction) {
        // Process an approved transaction
        Account account = transaction.getAccount();
        CategoryLimit.SpendingCategory category = transaction.getCategory(); // This now uses CategoryLimit.SpendingCategory
        BigDecimal amount = transaction.getAmount();
        
        // Update account balance
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
        
        // Update category limit
        List<CategoryLimit> categoryLimits = categoryLimitRepository.findByAccountIdAndCategory(
            account.getId(), category);
        
        if (!categoryLimits.isEmpty()) {
            CategoryLimit categoryLimit = categoryLimits.get(0);
            categoryLimit.setSpentAmount(categoryLimit.getSpentAmount().add(amount));
            categoryLimitRepository.save(categoryLimit);
        }
    }
}