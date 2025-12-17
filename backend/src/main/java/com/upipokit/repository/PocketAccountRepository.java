package com.upipokit.repository;

import com.upipokit.entity.PocketAccount;
import com.upipokit.entity.User;
import com.upipokit.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PocketAccountRepository extends JpaRepository<PocketAccount, Long> {
    PocketAccount findByChildUserId(Long childUserId);
    
    @Query("SELECT p FROM PocketAccount p WHERE p.parentAccount.id = :accountId")
    PocketAccount findByParentAccount_Id(@Param("accountId") Long accountId);
}