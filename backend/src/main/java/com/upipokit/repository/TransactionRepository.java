package com.upipokit.repository;

import com.upipokit.entity.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionRecord, Integer> {
    List<TransactionRecord> findByChildChildId(Integer childId);
}
