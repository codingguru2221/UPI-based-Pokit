package com.upipokit.controller;

import com.upipokit.dto.TransactionRequest;
import com.upipokit.dto.TransactionResponse;
import com.upipokit.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public ResponseEntity<?> processTransaction(@Valid @RequestBody TransactionRequest req) {
        try {
            TransactionResponse response = transactionService.processTransaction(req);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @GetMapping("/child/{childId}")
    public ResponseEntity<?> getTransactionsByChildId(@PathVariable Integer childId) {
        try {
            List<TransactionResponse> response = transactionService.getTransactionsByChildId(childId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<?> getTransactionById(@PathVariable Integer transactionId) {
        try {
            TransactionResponse response = transactionService.getTransactionById(transactionId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }
}