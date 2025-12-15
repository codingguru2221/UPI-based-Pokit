package com.pokit.upipocket.repository;

import com.pokit.upipocket.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUpiId(String upiId);
    List<Account> findByUserId(Long userId);
    List<Account> findByUser_UserType(com.pokit.upipocket.model.User.UserType userType);
}