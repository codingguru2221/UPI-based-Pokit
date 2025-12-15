package com.pokit.upipocket.repository;

import com.pokit.upipocket.model.CategoryLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategoryLimitRepository extends JpaRepository<CategoryLimit, Long> {
    List<CategoryLimit> findByAccountId(Long accountId);
    List<CategoryLimit> findByAccountIdAndCategory(Long accountId, CategoryLimit.SpendingCategory category);
}