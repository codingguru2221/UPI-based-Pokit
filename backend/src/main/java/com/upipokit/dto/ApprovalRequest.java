package com.upipokit.dto;

import jakarta.validation.constraints.NotNull;

public class ApprovalRequest {
    @NotNull
    private Integer approvalId;
    
    @NotNull
    private Boolean approved;

    public Integer getApprovalId() {
        return approvalId;
    }

    public void setApprovalId(Integer approvalId) {
        this.approvalId = approvalId;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }
}