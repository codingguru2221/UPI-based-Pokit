package com.upipokit.service;

import com.upipokit.dto.TransactionRequest;
import com.upipokit.dto.TransactionResponse;
import com.upipokit.entity.*;
import com.upipokit.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ApprovalRepository approvalRepository;

    @Autowired
    private ParentRepository parentRepository;

    public TransactionResponse processTransaction(TransactionRequest req) {
        // Get child, category and parent
        Child child = childRepository.findById(req.getChildId())
                .orElseThrow(() -> new IllegalArgumentException("Child not found"));
        
        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
                
        Parent parent = child.getParent();

        BigDecimal amount = req.getAmount();
        BigDecimal childBalance = child.getCurrentBalance();
        BigDecimal categoryRemaining = category.getRemainingLimit();

        TransactionRecord transaction = new TransactionRecord();
        transaction.setChild(child);
        transaction.setCategory(category);
        transaction.setAmount(amount);
        transaction.setMerchantName(req.getMerchantName());

        // Check if transaction can be processed
        if (childBalance.compareTo(amount) < 0) {
            // Not enough balance
            transaction.setStatus(TransactionRecord.Status.FAILED);
            transaction.setApprovalRequired(false);
        } else if (categoryRemaining.compareTo(amount) >= 0) {
            // Enough in category limit
            transaction.setStatus(TransactionRecord.Status.SUCCESS);
            transaction.setApprovalRequired(false);
            
            // Update balances
            child.setCurrentBalance(childBalance.subtract(amount));
            category.setRemainingLimit(categoryRemaining.subtract(amount));
            
            childRepository.save(child);
            categoryRepository.save(category);
        } else {
            // Not enough in category but enough in total balance
            // Check if we can auto-adjust from other categories
            BigDecimal totalAvailableInOtherCategories = getTotalAvailableInOtherCategories(child.getChildId(), req.getCategoryId());
            
            if (totalAvailableInOtherCategories.add(categoryRemaining).compareTo(amount) >= 0) {
                // Auto-adjust possible
                transaction.setStatus(TransactionRecord.Status.SUCCESS);
                transaction.setApprovalRequired(false);
                
                // Perform auto-adjustment
                performAutoAdjustment(child, category, amount);
            } else {
                // Parent approval required
                transaction.setStatus(TransactionRecord.Status.PENDING);
                transaction.setApprovalRequired(true);
                
                // Create approval record
                Approval approval = new Approval();
                approval.setTransaction(transaction);
                approval.setParent(parent);
                approval.setStatus(Approval.Status.PENDING);
                approvalRepository.save(approval);
            }
        }

        TransactionRecord saved = transactionRepository.save(transaction);
        return toDto(saved);
    }

    private BigDecimal getTotalAvailableInOtherCategories(Integer childId, Integer excludeCategoryId) {
        List<Category> categories = categoryRepository.findByChildChildId(childId);
        return categories.stream()
                .filter(c -> !c.getCategoryId().equals(excludeCategoryId))
                .map(Category::getRemainingLimit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void performAutoAdjustment(Child child, Category category, BigDecimal amount) {
        BigDecimal categoryRemaining = category.getRemainingLimit();
        BigDecimal amountNeededFromOtherCategories = amount.subtract(categoryRemaining);
        
        // Deduct what we can from the category
        category.setRemainingLimit(BigDecimal.ZERO);
        
        // Get other categories to deduct from
        List<Category> otherCategories = categoryRepository.findByChildChildId(child.getChildId());
        otherCategories.removeIf(c -> c.getCategoryId().equals(category.getCategoryId()));
        
        // Sort by remaining limit descending to take from categories with most funds first
        otherCategories.sort((c1, c2) -> c2.getRemainingLimit().compareTo(c1.getRemainingLimit()));
        
        BigDecimal remainingAmountToDeduct = amountNeededFromOtherCategories;
        for (Category otherCategory : otherCategories) {
            if (remainingAmountToDeduct.compareTo(BigDecimal.ZERO) <= 0) break;
            
            BigDecimal otherCategoryRemaining = otherCategory.getRemainingLimit();
            if (otherCategoryRemaining.compareTo(remainingAmountToDeduct) >= 0) {
                // This category has enough
                otherCategory.setRemainingLimit(otherCategoryRemaining.subtract(remainingAmountToDeduct));
                remainingAmountToDeduct = BigDecimal.ZERO;
            } else {
                // Take everything from this category
                otherCategory.setRemainingLimit(BigDecimal.ZERO);
                remainingAmountToDeduct = remainingAmountToDeduct.subtract(otherCategoryRemaining);
            }
            
            categoryRepository.save(otherCategory);
        }
        
        // Update child balance
        child.setCurrentBalance(child.getCurrentBalance().subtract(amount));
        
        // Save changes
        categoryRepository.save(category);
        childRepository.save(child);
    }

    public List<TransactionResponse> getTransactionsByChildId(Integer childId) {
        return transactionRepository.findByChildChildId(childId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public TransactionResponse getTransactionById(Integer transactionId) {
        TransactionRecord transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
        return toDto(transaction);
    }

    private TransactionResponse toDto(TransactionRecord transaction) {
        TransactionResponse dto = new TransactionResponse();
        dto.setTransactionId(transaction.getTransactionId());
        dto.setChildId(transaction.getChild().getChildId());
        if (transaction.getCategory() != null) {
            dto.setCategoryId(transaction.getCategory().getCategoryId());
        }
        dto.setAmount(transaction.getAmount());
        dto.setMerchantName(transaction.getMerchantName());
        dto.setStatus(transaction.getStatus().name());
        dto.setApprovalRequired(transaction.getApprovalRequired());
        dto.setCreatedAt(transaction.getCreatedAt());
        return dto;
    }
}