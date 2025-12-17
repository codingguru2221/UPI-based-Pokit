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
    
    @Autowired
    ApprovalHistoryRepository approvalHistoryRepository;
    
    @Autowired
    ParentRepository parentRepository;
    
    /**
     * Check if a child is eligible for account conversion
     * @param childId the ID of the child
     * @return true if eligible for conversion
     */
    public boolean isChildEligibleForConversion(Long childId) {
        User child = userRepository.findById(childId)
            .orElseThrow(() -> new RuntimeException("Child not found"));
        
        return child.isEligibleForConversion();
    }
    
    /**
     * Convert a child account to an independent account
     * @param childId the ID of the child
     * @return true if conversion successful
     */
    public boolean convertChildToIndependent(Long childId) {
        User child = userRepository.findById(childId)
            .orElseThrow(() -> new RuntimeException("Child not found"));
        
        // Check if child is eligible for conversion
        if (!child.isEligibleForConversion()) {
            throw new RuntimeException("Child is not eligible for account conversion (must be 18 years or older)");
        }
        
        // Update child status to independent
        child.setIsIndependent(true);
        userRepository.save(child);
        
        // Find the child's pocket account
        PocketAccount pocketAccount = pocketAccountRepository.findByChildUserId(childId);
        if (pocketAccount != null) {
            // Convert pocket account to a main account
            Account mainAccount = new Account(child, Account.AccountType.MAIN, pocketAccount.getCurrentBalance());
            accountRepository.save(mainAccount);
            
            // Delete the pocket account
            pocketAccountRepository.delete(pocketAccount);
        }
        
        // Create notification
        String message = String.format("Congratulations %s! Your pocket money account has been converted to a full UPI account.", child.getName());
        Notification notification = new Notification(child, message, Notification.NotificationType.ACCOUNT_CONVERTED);
        notificationRepository.save(notification);
        
        // Send real-time notification via WebSocket
        notificationWebSocketController.sendNotificationToUser(notification);
        
        return true;
    }
    
    /**
     * Create a pocket account for a child
     * @param childId the ID of the child
     * @param parentId the ID of the parent
     * @param monthlyLimit the monthly limit for the pocket account
     * @return the created pocket account
     */
    public PocketAccount createPocketAccount(Long childId, Long parentId, BigDecimal monthlyLimit) {
        // Find the child
        Optional<User> childOpt = userRepository.findById(childId);
        if (!childOpt.isPresent()) {
            throw new RuntimeException("Child not found");
        }
        
        User child = childOpt.get();
        
        // Verify child belongs to parent
        if (child instanceof Child) {
            Child childEntity = (Child) child;
            if (!childEntity.getParent().getId().equals(parentId)) {
                throw new RuntimeException("Child does not belong to the specified parent");
            }
        } else {
            throw new RuntimeException("User is not a child");
        }
        
        // Find the parent's main account
        List<Account> parentAccounts = accountRepository.findByUserId(parentId);
        Account parentMainAccount = null;
        for (Account account : parentAccounts) {
            if (account.getAccountType() == Account.AccountType.MAIN) {
                parentMainAccount = account;
                break;
            }
        }
        
        if (parentMainAccount == null) {
            throw new RuntimeException("Parent main account not found");
        }
        
        // Create pocket account linked to parent's main account
        PocketAccount pocketAccount = new PocketAccount(parentMainAccount, child, monthlyLimit, 1); // Reset on 1st of each month
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
        // Find the child's pocket account directly
        PocketAccount pocketAccount = pocketAccountRepository.findByChildUserId(childId);
        
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
        Transaction transaction = new Transaction(pocketAccount.getParentAccount(), merchantName, merchantUpiId, 
                                               amount, category, Transaction.TransactionType.DEBIT, description);
        
        // Check if category limit is exceeded (Rule A: Category Exhausted, Balance Available)
        if (categoryLimit.isLimitExceeded()) {
            // Check if total balance is available in pocket account
            if (pocketAccount.getCurrentBalance().compareTo(amount) >= 0) {
                // Category limit exceeded but total balance available - allow transaction automatically (Rule A)
                transaction.setStatus(Transaction.TransactionStatus.APPROVED);
                
                // Update category limit (log overflow)
                categoryLimit.addToSpent(amount);
                categoryLimitRepository.save(categoryLimit);
                
                // Update pocket account balance
                BigDecimal newBalance = pocketAccount.getCurrentBalance().subtract(amount);
                pocketAccount.setCurrentBalance(newBalance);
                pocketAccountRepository.save(pocketAccount);
                
                // Also update parent account balance
                BigDecimal newParentBalance = pocketAccount.getParentAccount().getBalance().subtract(amount);
                pocketAccount.getParentAccount().setBalance(newParentBalance);
                accountRepository.save(pocketAccount.getParentAccount());
            } else {
                // Neither category limit nor total balance available (Rule C: Hard Limit Exceeded)
                transaction.setParentApprovalRequired(true);
                transaction.setStatus(Transaction.TransactionStatus.PENDING);
                
                // Create notification for parent
                createParentApprovalNotification(pocketAccount, transaction);
            }
        } else if (categoryLimit.hasSufficientBalance(amount)) {
            // Sufficient balance in category - approve transaction
            transaction.setStatus(Transaction.TransactionStatus.APPROVED);
            
            // Update category limit
            categoryLimit.addToSpent(amount);
            categoryLimitRepository.save(categoryLimit);
            
            // Update pocket account balance
            BigDecimal newBalance = pocketAccount.getCurrentBalance().subtract(amount);
            pocketAccount.setCurrentBalance(newBalance);
            pocketAccountRepository.save(pocketAccount);
            
            // Also update parent account balance
            BigDecimal newParentBalance = pocketAccount.getParentAccount().getBalance().subtract(amount);
            pocketAccount.getParentAccount().setBalance(newParentBalance);
            accountRepository.save(pocketAccount.getParentAccount());
        } else {
            // Insufficient balance in category but check total balance (Rule C: Hard Limit Exceeded)
            if (pocketAccount.getCurrentBalance().compareTo(amount) >= 0) {
                // Category limit exceeded but total balance available - allow transaction automatically (Rule A)
                transaction.setStatus(Transaction.TransactionStatus.APPROVED);
                
                // Update category limit (log overflow)
                categoryLimit.addToSpent(amount);
                categoryLimitRepository.save(categoryLimit);
                
                // Update pocket account balance
                BigDecimal newBalance = pocketAccount.getCurrentBalance().subtract(amount);
                pocketAccount.setCurrentBalance(newBalance);
                pocketAccountRepository.save(pocketAccount);
                
                // Also update parent account balance
                BigDecimal newParentBalance = pocketAccount.getParentAccount().getBalance().subtract(amount);
                pocketAccount.getParentAccount().setBalance(newParentBalance);
                accountRepository.save(pocketAccount.getParentAccount());
            } else {
                // Neither category limit nor total balance available (Rule C: Hard Limit Exceeded)
                transaction.setParentApprovalRequired(true);
                transaction.setStatus(Transaction.TransactionStatus.PENDING);
                
                // Create notification for parent
                createParentApprovalNotification(pocketAccount, transaction);
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
        User child = pocketAccount.getChildUser();
        
        // Find the parent
        if (child instanceof Child) {
            Child childEntity = (Child) child;
            Parent parent = childEntity.getParent();
            
            if (parent != null) {
                String message = String.format("Approval required for %s's transaction of ₹%.2f at %s for %s. Description: %s. Time: %s", 
                    child.getName(), transaction.getAmount(), transaction.getMerchantName(), 
                    transaction.getCategory().getName(), transaction.getDescription(), 
                    transaction.getTransactionTime().toString());
                
                Notification notification = new Notification(parent, message, 
                    Notification.NotificationType.TRANSACTION_APPROVAL);
                notificationRepository.save(notification);
                
                // Send real-time notification via WebSocket
                notificationWebSocketController.sendNotificationToUser(notification);
            }
        }
    }
    
    /**
     * Send notification to child about transaction approval/rejection
     * @param pocketAccount the pocket account
     * @param transaction the transaction
     * @param approved whether the transaction was approved or rejected
     */
    private void sendApprovalNotificationToChild(PocketAccount pocketAccount, Transaction transaction, boolean approved) {
        // Find the child
        User child = pocketAccount.getChildUser();
        
        if (child != null) {
            String status = approved ? "approved" : "rejected";
            String message = String.format("Your transaction of ₹%.2f at %s for %s has been %s by your parent", 
                transaction.getAmount(), transaction.getMerchantName(), 
                transaction.getCategory().getName(), status);
            
            Notification notification = new Notification(child, message, 
                Notification.NotificationType.TRANSACTION_APPROVAL);
            notificationRepository.save(notification);
            
            // Send real-time notification via WebSocket
            notificationWebSocketController.sendNotificationToUser(notification);
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
        
        // Find the pocket account for this transaction
        PocketAccount pocketAccount = pocketAccountRepository.findByParentAccount_Id(transaction.getAccount().getId());
        
        if (pocketAccount == null) {
            throw new RuntimeException("Pocket account not found for transaction");
        }
        
        // Verify parent has permission to approve
        if (!pocketAccount.getParentAccount().getUser().getId().equals(parentId)) {
            throw new RuntimeException("Parent not authorized to approve this transaction");
        }
        
        // Update transaction status
        transaction.setParentApproved(true);
        transaction.setStatus(Transaction.TransactionStatus.APPROVED);
        transactionRepository.save(transaction);
        
        // Update pocket account balance
        BigDecimal newBalance = pocketAccount.getCurrentBalance().subtract(transaction.getAmount());
        pocketAccount.setCurrentBalance(newBalance);
        pocketAccountRepository.save(pocketAccount);
        
        // Update parent account balance
        Account parentAccount = pocketAccount.getParentAccount();
        BigDecimal newParentBalance = parentAccount.getBalance().subtract(transaction.getAmount());
        parentAccount.setBalance(newParentBalance);
        accountRepository.save(parentAccount);
        
        // Update category limit if applicable
        if (transaction.getCategory() != null) {
            List<CategoryLimit> categoryLimits = categoryLimitRepository.findByPocketAccountId(pocketAccount.getId());
            for (CategoryLimit categoryLimit : categoryLimits) {
                if (categoryLimit.getCategory().getId().equals(transaction.getCategory().getId())) {
                    categoryLimit.addToSpent(transaction.getAmount());
                    categoryLimitRepository.save(categoryLimit);
                    break;
                }
            }
        }
        
        // Create approval history record
        Parent parent = parentRepository.findById(parentId)
            .orElseThrow(() -> new RuntimeException("Parent not found"));
        ApprovalHistory approvalHistory = new ApprovalHistory(transaction, parent, 
            ApprovalHistory.ApprovalStatus.APPROVED, transaction.getAmount(), "Approved via web interface");
        approvalHistoryRepository.save(approvalHistory);
        
        // Send notification to child about approval
        sendApprovalNotificationToChild(pocketAccount, transaction, true);
        
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
        
        // Find the pocket account for this transaction
        PocketAccount pocketAccount = pocketAccountRepository.findByParentAccount_Id(transaction.getAccount().getId());
        
        if (pocketAccount == null) {
            throw new RuntimeException("Pocket account not found for transaction");
        }
        
        // Verify parent has permission to reject
        if (!pocketAccount.getParentAccount().getUser().getId().equals(parentId)) {
            throw new RuntimeException("Parent not authorized to reject this transaction");
        }
        
        // Update transaction status
        transaction.setParentApproved(false);
        transaction.setStatus(Transaction.TransactionStatus.REJECTED);
        transactionRepository.save(transaction);
        
        // Create approval history record
        Parent parent = parentRepository.findById(parentId)
            .orElseThrow(() -> new RuntimeException("Parent not found"));
        ApprovalHistory approvalHistory = new ApprovalHistory(transaction, parent, 
            ApprovalHistory.ApprovalStatus.REJECTED, transaction.getAmount(), "Rejected via web interface");
        approvalHistoryRepository.save(approvalHistory);
        
        // Send notification to child about rejection
        PocketAccount childPocketAccount = pocketAccountRepository.findByParentAccount_Id(transaction.getAccount().getId());
        if (childPocketAccount != null) {
            sendApprovalNotificationToChild(childPocketAccount, transaction, false);
        }
        
        return transaction;
    }
    
    /**
     * Get account summary for a user
     * @param userId the ID of the user
     * @return account summary
     */
    public AccountSummary getAccountSummary(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        Account mainAccount = null;
        PocketAccount pocketAccount = null;
        
        if (user != null) {
            // For parents, get their main account
            if (user.getRole() == User.Role.PARENT) {
                List<Account> accounts = accountRepository.findByUserId(userId);
                for (Account account : accounts) {
                    if (account.getAccountType() == Account.AccountType.MAIN) {
                        mainAccount = account;
                        break;
                    }
                }
            }
            // For children, get their pocket account
            else if (user.getRole() == User.Role.CHILD) {
                pocketAccount = pocketAccountRepository.findByChildUserId(userId);
                if (pocketAccount != null) {
                    mainAccount = pocketAccount.getParentAccount();
                }
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
        
        // Update limits (Rule B: Child Emergency Reallocation)
        fromLimit.setLimitAmount(fromLimit.getLimitAmount().subtract(amount));
        toLimit.setLimitAmount(toLimit.getLimitAmount().add(amount));
        
        categoryLimitRepository.save(fromLimit);
        categoryLimitRepository.save(toLimit);
        
        // Log reallocation history (would typically save to a separate table)
        System.out.println("Category limit reallocation: " + amount + " moved from " + 
                          fromLimit.getCategory().getName() + " to " + toLimit.getCategory().getName());
        
        return true;
    }
}