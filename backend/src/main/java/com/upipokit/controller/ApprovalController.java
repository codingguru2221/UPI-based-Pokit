package com.upipokit.controller;

import com.upipokit.dto.ApprovalRequest;
import com.upipokit.dto.ApprovalResponse;
import com.upipokit.service.ApprovalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/approvals")
public class ApprovalController {

    @Autowired
    private ApprovalService approvalService;

    @PutMapping("/process")
    public ResponseEntity<?> processApproval(@Valid @RequestBody ApprovalRequest req) {
        try {
            ApprovalResponse response = approvalService.processApproval(req.getApprovalId(), req.getApproved());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @GetMapping("/parent/{parentId}/pending")
    public ResponseEntity<?> getPendingApprovalsByParentId(@PathVariable Integer parentId) {
        try {
            List<ApprovalResponse> response = approvalService.getPendingApprovalsByParentId(parentId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @GetMapping("/{approvalId}")
    public ResponseEntity<?> getApprovalById(@PathVariable Integer approvalId) {
        try {
            ApprovalResponse response = approvalService.getApprovalById(approvalId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }
}