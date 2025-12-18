package com.upipokit.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "approvals")
public class Approval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer approvalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private TransactionRecord transaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false)
    private Parent parent;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Instant decisionTime;

    public enum Status {
        APPROVED, REJECTED, PENDING
    }

    public Approval() {
    }

    public Integer getApprovalId() {
        return approvalId;
    }

    public void setApprovalId(Integer approvalId) {
        this.approvalId = approvalId;
    }

    public TransactionRecord getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionRecord transaction) {
        this.transaction = transaction;
    }

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Instant getDecisionTime() {
        return decisionTime;
    }

    public void setDecisionTime(Instant decisionTime) {
        this.decisionTime = decisionTime;
    }
}
