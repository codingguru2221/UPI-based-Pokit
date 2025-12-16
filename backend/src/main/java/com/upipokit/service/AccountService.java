package com.upipokit.service;

import com.upipokit.dto.AccountSummary;
import com.upipokit.entity.*;
import com.upipokit.repository.*;
import com.upipokit.controller.NotificationWebSocketController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    
    @Autowired
    AccountRepository accountRepository;
    
    @Autowired
    PocketAccountRepository pocketAccountRepository;
    
    @Autowired
    CategoryLimitRepository categoryLimitRepository;
    
    @Autowired
    CategoryRepository categoryRepository;
    
    @Autowired
    TransactionRepository transactionRepository;
    
    @Autowired
    NotificationRepository notificationRepository;
    
    @Autowired
    NotificationWebSocketController notificationWebSocketController;
    
    @Autowired
    UserRepository userRepository;
    
    /**
     * Create a pocket account for a child
     * @param childId the ID of the child
     * @param monthlyLimit the monthly limit for the pocket account
     * @return the created pocket account
     */
    public PocketAccount createPocketAccount(Long childId, BigDecimal monthlyLimit) {
        // Find the child
        Optional<User> childOpt = userRepository.findById(childId);
        if (!childOpt.isPresent()) {
            throw new RuntimeException("Child not found");
        }
        
        User child = childOpt.get();
        
        // Create main account
        Account mainAccount = new Account(child, Account.AccountType.MAIN, BigDecimal.ZERO);
        accountRepository.save(mainAccount);
        
        // Create pocket account
        PocketAccount pocketAccount = new PocketAccount(mainAccount, monthlyLimit, 1); // Reset on 1st of each month
        pocketAccountRepository.save(pocketAccount);
        
        // Create default category limits (evenly distributed)
        createDefaultCategoryLimits(pocketAccount, monthlyLimit);
        
        return pocketAccount;
    }
    
    /**
     * Create default category limits for a pocket account
     * @param pocketAccount the pocket account
     * @param monthlyLimit the monthly limit
     */
    private void createDefaultCategoryLimits(PocketAccount pocketAccount, BigDecimal monthlyLimit) {
        // Create default categories if they don't exist
        String[] categoryNames = {"FOOD", "TRAVEL", "SHOPPING", "ENTERTAINMENT"};
        BigDecimal defaultLimit = monthlyLimit.divide(BigDecimal.valueOf(categoryNames.length), 2, BigDecimal.ROUND_HALF_UP);
        
        for (String categoryName : categoryNames) {
            Optional<Category> categoryOpt = categoryRepository.findByName(categoryName);
            Category category;
            
            if (!categoryOpt.isPresent()) {
                category = new Category(categoryName, categoryName.toLowerCase() + " expenses");
                categoryRepository.save(category);
            } else {
                category = categoryOpt.get();
            }
            
            CategoryLimit categoryLimit = new CategoryLimit(pocketAccount, category, defaultLimit);
            categoryLimitRepository.save(categoryLimit);
        }
    }
    
    /**
     * Process a transaction for a child's pocket account
     * @param childId the ID of the child
     * @param merchantName the name of the merchant
     * @param merchantUpiId the UPI ID of the merchant
     * @param amount the transaction amount
     * @param categoryName the category name
     * @param description the transaction description
     * @return the processed transaction
     */
    public Transaction processTransaction(Long childId, String merchantName, String merchantUpiId, 
                                       BigDecimal amount, String categoryName, String description) {
        // Find the child's pocket account
        List<Account> accounts = accountRepository.findByUserId(childId);
        PocketAccount pocketAccount = null;
        
        for (Account account : accounts) {
            if (account.getAccountType() == Account.AccountType.POCKET) {
                pocketAccount = pocketAccountRepository.findById(account.getId()).orElse(null);
                break;
            }
        }
        
        if (pocketAccount == null) {
            throw new RuntimeException("Pocket account not found for child");
        }
        
        // Find the category
        Category category = categoryRepository.findByName(categoryName)
            .orElseThrow(() -> new RuntimeException("Category not found: " + categoryName));
        
        // Find the category limit for this category
        List<CategoryLimit> categoryLimits = categoryLimitRepository.findByPocketAccountId(pocketAccount.getId());
        CategoryLimit categoryLimit = null;
        
        for (CategoryLimit cl : categoryLimits) {
            if (cl.getCategory().getId().equals(category.getId())) {
                categoryLimit = cl;
                break;
            }
        }
        
        if (categoryLimit == null) {
            throw new RuntimeException("Category limit not found for category: " + categoryName);
        }
        
        // Check if the transaction can be processed
        Transaction transaction = new Transaction(pocketAccount.getAccount(), merchantName, merchantUpiId, 
                                               amount, category, Transaction.TransactionType.DEBIT, description);
        
        // Check if category limit is exceeded
        if (categoryLimit.isLimitExceeded()) {
            // Check if total balance is available
            if (pocketAccount.getAccount().getBalance().compareTo(amount) >= 0) {
                // Category limit exceeded but total balance available - require parent approval
                transaction.setParentApprovalRequired(true);
                transaction.setStatus(Transaction.TransactionStatus.PENDING);
                
                // Create notification for parent
                createParentApprovalNotification(pocketAccount, transaction);
            } else {
                // Neither category limit nor total balance available
                transaction.setStatus(Transaction.TransactionStatus.REJECTED);
                throw new RuntimeException("Insufficient funds for transaction");
            }
        } else if (categoryLimit.hasSufficientBalance(amount)) {
            // Sufficient balance in category - approve transaction
            transaction.setStatus(Transaction.TransactionStatus.APPROVED);
            
            // Update category limit
            categoryLimit.addToSpent(amount);
            categoryLimitRepository.save(categoryLimit);
            
            // Update account balance
            BigDecimal newBalance = pocketAccount.getAccount().getBalance().subtract(amount);
            pocketAccount.getAccount().setBalance(newBalance);
            accountRepository.save(pocketAccount.getAccount());
        } else {
            // Insufficient balance in category but check total balance
            if (pocketAccount.getAccount().getBalance().compareTo(amount) >= 0) {
                // Category limit exceeded but total balance available - require parent approval
                transaction.setParentApprovalRequired(true);
                transaction.setStatus(Transaction.TransactionStatus.PENDING);
                
                // Create notification for parent
                createParentApprovalNotification(pocketAccount, transaction);
            } else {
                // Neither category limit nor total balance available
                transaction.setStatus(Transaction.TransactionStatus.REJECTED);
                throw new RuntimeException("Insufficient funds for transaction");
            }
        }
        
        // Save transaction
        transactionRepository.save(transaction);
        
        return transaction;
    }
    
    /**
     * Create a notification for parent approval
     * @param pocketAccount the pocket account
     * @param transaction the transaction requiring approval
     */
    private void createParentApprovalNotification(PocketAccount pocketAccount, Transaction transaction) {
        // Find the child
        User child = pocketAccount.getAccount().getUser();
        
        // Find the parent (assuming single parent for simplicity)
        if (child instanceof Child) {
            Child childEntity = (Child) child;
            Parent parent = childEntity.getParent();
            
            if (parent != null) {
                String message = String.format("Approval required for %s's transaction of ₹%.2f at %s for %s", 
                    child.getName(), transaction.getAmount(), transaction.getMerchantName(), 
                    transaction.getCategory().getName());
                
                Notification notification = new Notification(parent.getUser(), message, 
                    Notification.NotificationType.TRANSACTION_APPROVAL);
                notificationRepository.save(notification);
                
                // Send real-time notification via WebSocket
                notificationWebSocketController.sendNotificationToUser(notification);
            }
        }
    }
    
    /**
     * Approve a pending transaction
     * @param transactionId the ID of the transaction to approve
     * @param parentId the ID of the parent approving the transaction
     * @return the approved transaction
     */
    public Transaction approveTransaction(Long transactionId, Long parentId) {
        // Find the transaction
        Transaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        // Verify parent has permission to approve
        User child = transaction.getAccount().getUser();
        if (child instanceof Child) {
            Child childEntity = (Child) child;
            if (!childEntity.getParent().getId().equals(parentId)) {
                throw new RuntimeException("Parent not authorized to approve this transaction");
            }
        }
        
        // Update transaction status
        transaction.setParentApproved(true);
        transaction.setStatus(Transaction.TransactionStatus.APPROVED);
        transactionRepository.save(transaction);
        
        // Update account balance
        Account account = transaction.getAccount();
        BigDecimal newBalance = account.getBalance().subtract(transaction.getAmount());
        account.setBalance(newBalance);
        accountRepository.save(account);
        
        // Update category limit if applicable
        if (transaction.getCategory() != null) {
            List<CategoryLimit> categoryLimits = categoryLimitRepository.findByPocketAccountId(account.getId());
            for (CategoryLimit categoryLimit : categoryLimits) {
                if (categoryLimit.getCategory().getId().equals(transaction.getCategory().getId())) {
                    categoryLimit.addToSpent(transaction.getAmount());
                    categoryLimitRepository.save(categoryLimit);
                    break;
                }
            }
        }
        
        return transaction;
    }
    
    /**
     * Reject a pending transaction
     * @param transactionId the ID of the transaction to reject
     * @param parentId the ID of the parent rejecting the transaction
     * @return the rejected transaction
     */
    public Transaction rejectTransaction(Long transactionId, Long parentId) {
        // Find the transaction
        Transaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        // Verify parent has permission to reject
        User child = transaction.getAccount().getUser();
        if (child instanceof Child) {
            Child childEntity = (Child) child;
            if (!childEntity.getParent().getId().equals(parentId)) {
                throw new RuntimeException("Parent not authorized to reject this transaction");
            }
        }
        
        // Update transaction status
        transaction.setParentApproved(false);
        transaction.setStatus(Transaction.TransactionStatus.REJECTED);
        transactionRepository.save(transaction);
        
        return transaction;
    }
    
    /**
     * Get account summary for a user
     * @param userId the ID of the user
     * @return account summary
     */
    public AccountSummary getAccountSummary(Long userId) {
        List<Account> accounts = accountRepository.findByUserId(userId);
        Account mainAccount = null;
        PocketAccount pocketAccount = null;
        
        for (Account account : accounts) {
            if (account.getAccountType() == Account.AccountType.MAIN) {
                mainAccount = account;
            } else if (account.getAccountType() == Account.AccountType.POCKET) {
                pocketAccount = pocketAccountRepository.findById(account.getId()).orElse(null);
            }
        }
        
        List<CategoryLimit> categoryLimits = pocketAccount != null ? 
            categoryLimitRepository.findByPocketAccountId(pocketAccount.getId()) : List.of();
        
        List<Transaction> recentTransactions = mainAccount != null ? 
            transactionRepository.findByAccountIdOrderByTransactionTimeDesc(mainAccount.getId()) : List.of();
        
        // Limit to last 10 transactions
        if (recentTransactions.size() > 10) {
            recentTransactions = recentTransactions.subList(0, 10);
        }
        
        return new AccountSummary(mainAccount, pocketAccount, categoryLimits, recentTransactions);
    }
    
    /**
     * Reallocate limits between categories
     * @param pocketAccountId the ID of the pocket account
     * @param fromCategoryId the ID of the category to reduce limit from
     * @param toCategoryId the ID of the category to increase limit to
     * @param amount the amount to reallocate
     * @return true if successful
     */
    public boolean reallocateLimits(Long pocketAccountId, Long fromCategoryId, Long toCategoryId, BigDecimal amount) {
        // Find category limits
        List<CategoryLimit> categoryLimits = categoryLimitRepository.findByPocketAccountId(pocketAccountId);
        CategoryLimit fromLimit = null;
        CategoryLimit toLimit = null;
        
        for (CategoryLimit cl : categoryLimits) {
            if (cl.getCategory().getId().equals(fromCategoryId)) {
                fromLimit = cl;
            } else if (cl.getCategory().getId().equals(toCategoryId)) {
                toLimit = cl;
            }
        }
        
        if (fromLimit == null || toLimit == null) {
            throw new RuntimeException("Category limits not found");
        }
        
        // Check if from category has enough spent amount to reduce
        if (fromLimit.getCurrentSpent().compareTo(amount) < 0) {
            throw new RuntimeException("Not enough spent amount in from category to reallocate");
        }
        
        // Update limits
        fromLimit.setLimitAmount(fromLimit.getLimitAmount().subtract(amount));
        toLimit.setLimitAmount(toLimit.getLimitAmount().add(amount));
        
        categoryLimitRepository.save(fromLimit);
        categoryLimitRepository.save(toLimit);
        
        return true;
    }
}