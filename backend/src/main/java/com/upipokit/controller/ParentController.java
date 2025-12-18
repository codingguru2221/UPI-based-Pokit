package com.upipokit.controller;

import com.upipokit.dto.ParentLoginRequest;
import com.upipokit.dto.ParentRegisterRequest;
import com.upipokit.dto.ParentResponse;
import com.upipokit.service.ParentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parents")
public class ParentController {

    @Autowired
    private ParentService parentService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody ParentRegisterRequest req) {
        try {
            ParentResponse res = parentService.register(req);
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody ParentLoginRequest req) {
        try {
            ParentResponse res = parentService.login(req);
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        }
    }

    @GetMapping("/{parentId}")
    public ResponseEntity<?> getParentById(@PathVariable Integer parentId) {
        try {
            ParentResponse res = parentService.getParentById(parentId);
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }
}
