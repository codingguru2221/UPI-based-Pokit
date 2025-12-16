package com.upipokit.service;

import com.upipokit.entity.*;
import com.upipokit.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountConversionService {
    
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    ChildRepository childRepository;
    
    @Autowired
    AccountRepository accountRepository;
    
    @Autowired
    PocketAccountRepository pocketAccountRepository;
    
    @Autowired
    NotificationRepository notificationRepository;
    
    /**
     * Convert a child account to a normal UPI account when they turn 18
     * @param childId the ID of the child to convert
     * @return the converted user
     */
    public User convertChildToAdult(Long childId) {
        // Find the child
        Child child = childRepository.findById(childId)
            .orElseThrow(() -> new RuntimeException("Child not found"));
        
        User user = child.getUser();
        
        // Check if the child is 18 or older
        if (user.getDateOfBirth() == null || 
            user.getDateOfBirth().plusYears(18).isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Child is not yet 18 years old");
        }
        
        // Convert child to normal user (remove child relationship)
        childRepository.delete(child);
        
        // Update user role to USER (adult)
        user.setRole(User.Role.PARENT); // Using PARENT as adult role for simplicity
        userRepository.save(user);
        
        // Convert pocket account to normal account
        List<Account> accounts = accountRepository.findByUserId(user.getId());
        for (Account account : accounts) {
            if (account.getAccountType() == Account.AccountType.POCKET) {
                // Get the pocket account
                PocketAccount pocketAccount = pocketAccountRepository.findById(account.getId()).orElse(null);
                
                if (pocketAccount != null) {
                    // Convert to normal account by removing pocket-specific fields
                    account.setAccountType(Account.AccountType.MAIN);
                    accountRepository.save(account);
                    
                    // Delete the pocket account record
                    pocketAccountRepository.delete(pocketAccount);
                }
            }
        }
        
        // Create notification
        String message = String.format("%s's account has been successfully converted to a normal UPI account", 
            user.getName());
        Notification notification = new Notification(user, message, 
            Notification.NotificationType.ACCOUNT_CONVERTED);
        notificationRepository.save(notification);
        
        return user;
    }
    
    /**
     * Check if a child is eligible for account conversion
     * @param childId the ID of the child to check
     * @return true if eligible, false otherwise
     */
    public boolean isEligibleForConversion(Long childId) {
        // Find the child
        Child child = childRepository.findById(childId)
            .orElseThrow(() -> new RuntimeException("Child not found"));
        
        User user = child.getUser();
        
        // Check if the child is 18 or older
        if (user.getDateOfBirth() == null) {
            return false;
        }
        
        return !user.getDateOfBirth().plusYears(18).isAfter(LocalDateTime.now());
    }
}