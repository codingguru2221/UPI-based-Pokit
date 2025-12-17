package com.upipokit.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "parents")
public class Parent extends User {
    @Column(name = "upi_id")
    private String upiId;

    @Column(name = "bank_account_number")
    private String bankAccountNumber;

    // Constructors
    public Parent() {
        super();
    }

    public Parent(User user, String upiId, String bankAccountNumber) {
        // Copy properties from the user object
        this.setId(user.getId());
        this.setName(user.getName());
        this.setUsername(user.getUsername());
        this.setEmail(user.getEmail());
        this.setPhone(user.getPhone());
        this.setPasswordHash(user.getPasswordHash());
        this.setRole(user.getRole());
        this.setDateOfBirth(user.getDateOfBirth());
        this.setChildAge(null); // Parents don't have child age
        this.setIsIndependent(false); // Parents are always independent
        this.setCreatedAt(user.getCreatedAt());
        this.setUpdatedAt(user.getUpdatedAt());
        
        // Set parent-specific fields
        this.upiId = upiId;
        this.bankAccountNumber = bankAccountNumber;
    }

    // Getters and Setters
    /**
     * @deprecated Use the inherited methods from User instead
     */
    @Deprecated
    public User getUser() {
        return this;
    }
    
    /**
     * @deprecated Setting user is not needed as Parent extends User
     */
    @Deprecated
    public void setUser(User user) {
        // No-op as Parent extends User
    }

    public String getUpiId() {
        return upiId;
    }

    public void setUpiId(String upiId) {
        this.upiId = upiId;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }
}