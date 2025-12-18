package com.upipokit.repository;

import com.upipokit.entity.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChildRepository extends JpaRepository<Child, Integer> {
    List<Child> findByParentParentId(Integer parentId);
}
