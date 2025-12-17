package com.upipokit.repository;

import com.upipokit.entity.ApprovalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalHistoryRepository extends JpaRepository<ApprovalHistory, Long> {
    List<ApprovalHistory> findByParentIdOrderByCreatedAtDesc(Long parentId);
    List<ApprovalHistory> findByTransactionId(Long transactionId);
}