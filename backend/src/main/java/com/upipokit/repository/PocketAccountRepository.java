package com.upipokit.repository;

import com.upipokit.entity.PocketAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PocketAccountRepository extends JpaRepository<PocketAccount, Long> {
}