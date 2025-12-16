package com.upipokit.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "parents")
public class Parent extends User {
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "upi_id")
    private String upiId;

    @Column(name = "bank_account_number")
    private String bankAccountNumber;

    // Constructors
    public Parent() {
        super();
    }

    public Parent(User user, String upiId, String bankAccountNumber) {
        this.user = user;
        this.upiId = upiId;
        this.bankAccountNumber = bankAccountNumber;
    }

    // Getters and Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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