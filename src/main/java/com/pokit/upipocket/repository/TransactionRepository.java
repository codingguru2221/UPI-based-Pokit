package com.pokit.upipocket.repository;

import com.pokit.upipocket.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountId(Long accountId);
    List<Transaction> findByAccountIdAndStatus(Long accountId, Transaction.TransactionStatus status);
    List<Transaction> findByMerchantUpiId(String merchantUpiId);
}