package com.example.notification_service.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(name = "full_name")
    private String fullName;

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
}
