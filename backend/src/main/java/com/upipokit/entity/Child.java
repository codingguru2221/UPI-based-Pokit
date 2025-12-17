package com.upipokit.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "children")
public class Child extends User {
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parent parent;

    // Constructors
    public Child() {
        super();
    }

    public Child(User user, Parent parent) {
        // Copy properties from the user object
        this.setId(user.getId());
        this.setName(user.getName());
        this.setUsername(user.getUsername());
        this.setEmail(user.getEmail());
        this.setPhone(user.getPhone());
        this.setPasswordHash(user.getPasswordHash());
        this.setRole(user.getRole());
        this.setDateOfBirth(user.getDateOfBirth());
        this.setCreatedAt(user.getCreatedAt());
        this.setUpdatedAt(user.getUpdatedAt());
        
        // Calculate child age based on date of birth
        if (user.getDateOfBirth() != null) {
            this.setChildAge(java.time.Period.between(user.getDateOfBirth().toLocalDate(), java.time.LocalDate.now()).getYears());
        }
        
        this.parent = parent;
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
     * @deprecated Setting user is not needed as Child extends User
     */
    @Deprecated
    public void setUser(User user) {
        // No-op as Child extends User
    }

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }
}