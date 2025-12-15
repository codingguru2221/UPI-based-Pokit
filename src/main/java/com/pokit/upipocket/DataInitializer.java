package com.pokit.upipocket;

import com.pokit.upipocket.model.Account;
import com.pokit.upipocket.model.CategoryLimit;
import com.pokit.upipocket.model.User;
import com.pokit.upipocket.repository.AccountRepository;
import com.pokit.upipocket.repository.CategoryLimitRepository;
import com.pokit.upipocket.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private CategoryLimitRepository categoryLimitRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // Create a parent user
        User parent = new User();
        parent.setName("John Doe");
        parent.setEmail("john.doe@example.com");
        parent.setPhoneNumber("9876543210");
        parent.setDateOfBirth(LocalDate.of(1980, 5, 15));
        parent.setUserType(User.UserType.PARENT);
        parent = userRepository.save(parent);
        
        // Create a child user
        User child = new User();
        child.setName("Jane Doe");
        child.setEmail("jane.doe@example.com");
        child.setPhoneNumber("9876543211");
        child.setDateOfBirth(LocalDate.of(2010, 8, 20));
        child.setUserType(User.UserType.CHILD);
        child.setParent(parent);
        child = userRepository.save(child);
        
        // Create an account for the child
        Account account = new Account();
        account.setUpiId("jane@upi");
        account.setAccountName("Jane's Pocket Money");
        account.setBalance(new BigDecimal("1000.00"));
        account.setMonthlyPocketMoney(new BigDecimal("1000.00"));
        account.setLastAllocationDate(LocalDateTime.now());
        account.setUser(child);
        account = accountRepository.save(account);
        
        // Create category limits for the account
        CategoryLimit foodLimit = new CategoryLimit();
        foodLimit.setCategory(CategoryLimit.SpendingCategory.FOOD);
        foodLimit.setLimitAmount(new BigDecimal("400.00"));
        foodLimit.setSpentAmount(new BigDecimal("0.00"));
        foodLimit.setAccount(account);
        categoryLimitRepository.save(foodLimit);
        
        CategoryLimit travelLimit = new CategoryLimit();
        travelLimit.setCategory(CategoryLimit.SpendingCategory.TRAVEL);
        travelLimit.setLimitAmount(new BigDecimal("300.00"));
        travelLimit.setSpentAmount(new BigDecimal("0.00"));
        travelLimit.setAccount(account);
        categoryLimitRepository.save(travelLimit);
        
        CategoryLimit shoppingLimit = new CategoryLimit();
        shoppingLimit.setCategory(CategoryLimit.SpendingCategory.ONLINE_SHOPPING);
        shoppingLimit.setLimitAmount(new BigDecimal("200.00"));
        shoppingLimit.setSpentAmount(new BigDecimal("0.00"));
        shoppingLimit.setAccount(account);
        categoryLimitRepository.save(shoppingLimit);
        
        CategoryLimit entertainmentLimit = new CategoryLimit();
        entertainmentLimit.setCategory(CategoryLimit.SpendingCategory.ENTERTAINMENT);
        entertainmentLimit.setLimitAmount(new BigDecimal("100.00"));
        entertainmentLimit.setSpentAmount(new BigDecimal("0.00"));
        entertainmentLimit.setAccount(account);
        categoryLimitRepository.save(entertainmentLimit);
        
        System.out.println("Sample data initialized successfully!");
    }
}