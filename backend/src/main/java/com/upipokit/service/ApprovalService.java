package com.upipokit.service;

import com.upipokit.dto.ApprovalResponse;
import com.upipokit.entity.Approval;
import com.upipokit.entity.TransactionRecord;
import com.upipokit.repository.ApprovalRepository;
import com.upipokit.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApprovalService {

    @Autowired
    private ApprovalRepository approvalRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public ApprovalResponse processApproval(Integer approvalId, Boolean approved) {
        Approval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new IllegalArgumentException("Approval not found"));

        if (!approval.getStatus().equals(Approval.Status.PENDING)) {
            throw new IllegalArgumentException("Approval already processed");
        }

        approval.setStatus(approved ? Approval.Status.APPROVED : Approval.Status.REJECTED);
        approval.setDecisionTime(Instant.now());

        Approval saved = approvalRepository.save(approval);

        // Update transaction status
        TransactionRecord transaction = approval.getTransaction();
        transaction.setStatus(approved ? TransactionRecord.Status.SUCCESS : TransactionRecord.Status.FAILED);
        transactionRepository.save(transaction);

        // If approved, update balances
        if (approved) {
            // Balance updates would be handled here
            // This is simplified - in a real implementation, you would update child and category balances
        }

        return toDto(saved);
    }

    public List<ApprovalResponse> getPendingApprovalsByParentId(Integer parentId) {
        return approvalRepository.findAll()
                .stream()
                .filter(a -> a.getParent().getParentId().equals(parentId))
                .filter(a -> a.getStatus().equals(Approval.Status.PENDING))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ApprovalResponse getApprovalById(Integer approvalId) {
        Approval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new IllegalArgumentException("Approval not found"));
        return toDto(approval);
    }

    private ApprovalResponse toDto(Approval approval) {
        ApprovalResponse dto = new ApprovalResponse();
        dto.setApprovalId(approval.getApprovalId());
        dto.setTransactionId(approval.getTransaction().getTransactionId());
        dto.setParentId(approval.getParent().getParentId());
        dto.setStatus(approval.getStatus().name());
        dto.setDecisionTime(approval.getDecisionTime());
        return dto;
    }
}