package com.upipokit.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "children")
public class Child extends User {
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parent parent;

    // Constructors
    public Child() {
        super();
    }

    public Child(User user, Parent parent) {
        this.user = user;
        this.parent = parent;
    }

    // Getters and Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }
}