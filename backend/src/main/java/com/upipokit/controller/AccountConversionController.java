package com.upipokit.controller;

import com.upipokit.entity.User;
import com.upipokit.service.AccountConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/conversion")
public class AccountConversionController {
    
    @Autowired
    AccountConversionService accountConversionService;
    
    /**
     * Check if a child is eligible for account conversion
     */
    @GetMapping("/eligible/{childId}")
    public ResponseEntity<?> isEligibleForConversion(@PathVariable Long childId) {
        try {
            boolean eligible = accountConversionService.isEligibleForConversion(childId);
            return ResponseEntity.ok(eligible);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Convert a child account to an adult account
     */
    @PostMapping("/convert/{childId}")
    public ResponseEntity<?> convertChildToAdult(@PathVariable Long childId) {
        try {
            User user = accountConversionService.convertChildToAdult(childId);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}