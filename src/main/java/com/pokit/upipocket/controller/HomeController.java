package com.pokit.upipocket.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {
    
    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to UPI-based Pokit - Parent-Controlled Pocket Money Application");
        response.put("description", "This is a banking simulation for a Parent-Controlled, UPI-Powered Pocket Money Application designed for Indian families.");
        
        Map<String, String> apiEndpoints = new HashMap<>();
        apiEndpoints.put("GET /api/users", "Get all users");
        apiEndpoints.put("POST /api/users", "Create a new user");
        apiEndpoints.put("GET /api/users/{id}", "Get user by ID");
        apiEndpoints.put("GET /api/users/email/{email}", "Get user by email");
        apiEndpoints.put("GET /api/users/parent/{parentId}/children", "Get all children of a parent");
        apiEndpoints.put("PUT /api/users", "Update user information");
        apiEndpoints.put("DELETE /api/users/{id}", "Delete a user");
        apiEndpoints.put("GET /api/users/parents", "Get all parents");
        
        apiEndpoints.put("POST /api/accounts", "Create a new account");
        apiEndpoints.put("GET /api/accounts/{id}", "Get account by ID");
        apiEndpoints.put("GET /api/accounts/upi/{upiId}", "Get account by UPI ID");
        apiEndpoints.put("GET /api/accounts/user/{userId}", "Get all accounts of a user");
        apiEndpoints.put("PUT /api/accounts", "Update account information");
        apiEndpoints.put("DELETE /api/accounts/{id}", "Delete an account");
        apiEndpoints.put("POST /api/accounts/{accountId}/allocate", "Allocate monthly pocket money");
        apiEndpoints.put("POST /api/accounts/{accountId}/transfer", "Transfer between categories");
        apiEndpoints.put("GET /api/accounts/{accountId}/balance", "Get available balance");
        apiEndpoints.put("GET /api/accounts/{accountId}/categories", "Get category limits");
        
        apiEndpoints.put("POST /api/transactions", "Create a new transaction");
        apiEndpoints.put("GET /api/transactions/{id}", "Get transaction by ID");
        apiEndpoints.put("GET /api/transactions/account/{accountId}", "Get all transactions of an account");
        apiEndpoints.put("GET /api/transactions/account/{accountId}/pending", "Get pending transactions of an account");
        apiEndpoints.put("PUT /api/transactions/{transactionId}/status", "Update transaction status");
        apiEndpoints.put("DELETE /api/transactions/{id}", "Delete a transaction");
        apiEndpoints.put("POST /api/transactions/process-payment", "Process a payment");
        apiEndpoints.put("GET /api/transactions", "Get all transactions");
        
        apiEndpoints.put("GET /h2-console", "Access H2 database console (Development only)");
        
        response.put("api_endpoints", apiEndpoints);
        response.put("sample_data", "On startup, the application initializes with a parent user (John Doe), a child user (Jane Doe), and an account with ₹1000 monthly pocket money");
        
        return response;
    }
}