package com.upipokit.repository;

import com.upipokit.entity.CategoryLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryLimitRepository extends JpaRepository<CategoryLimit, Long> {
    List<CategoryLimit> findByPocketAccountId(Long pocketAccountId);
}