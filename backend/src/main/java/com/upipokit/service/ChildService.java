package com.upipokit.service;

import com.upipokit.dto.ChildRegisterRequest;
import com.upipokit.dto.ChildResponse;
import com.upipokit.entity.Child;
import com.upipokit.entity.Parent;
import com.upipokit.repository.ChildRepository;
import com.upipokit.repository.ParentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChildService {

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private ParentRepository parentRepository;

    public ChildResponse registerChild(Integer parentId, ChildRegisterRequest req) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Parent not found"));

        Child child = new Child();
        child.setParent(parent);
        child.setName(req.getName());
        child.setAge(req.getAge());
        child.setMonthlyLimit(req.getMonthlyLimit());
        child.setCurrentBalance(req.getMonthlyLimit()); // Initial balance equals monthly limit

        Child saved = childRepository.save(child);
        return toDto(saved);
    }

    public List<ChildResponse> getChildrenByParentId(Integer parentId) {
        return childRepository.findByParentParentId(parentId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ChildResponse getChildById(Integer childId) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("Child not found"));
        return toDto(child);
    }

    public ChildResponse updateBalance(Integer childId, BigDecimal newBalance) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("Child not found"));
        child.setCurrentBalance(newBalance);
        Child saved = childRepository.save(child);
        return toDto(saved);
    }

    private ChildResponse toDto(Child child) {
        ChildResponse dto = new ChildResponse();
        dto.setChildId(child.getChildId());
        dto.setName(child.getName());
        dto.setAge(child.getAge());
        dto.setMonthlyLimit(child.getMonthlyLimit());
        dto.setCurrentBalance(child.getCurrentBalance());
        dto.setCreatedAt(child.getCreatedAt());
        return dto;
    }
}