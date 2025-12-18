package com.upipokit.controller;

import com.upipokit.dto.ChildRegisterRequest;
import com.upipokit.dto.ChildResponse;
import com.upipokit.service.ChildService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/children")
public class ChildController {

    @Autowired
    private ChildService childService;

    @PostMapping("/parent/{parentId}")
    public ResponseEntity<?> registerChild(@PathVariable Integer parentId, @Valid @RequestBody ChildRegisterRequest req) {
        try {
            ChildResponse response = childService.registerChild(parentId, req);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @GetMapping("/parent/{parentId}")
    public ResponseEntity<?> getChildrenByParentId(@PathVariable Integer parentId) {
        try {
            List<ChildResponse> response = childService.getChildrenByParentId(parentId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @GetMapping("/{childId}")
    public ResponseEntity<?> getChildById(@PathVariable Integer childId) {
        try {
            ChildResponse response = childService.getChildById(childId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }
}