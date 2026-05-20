package com.example.notification_service.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "family_members")
public class FamilyMember {
    @Id
    private Long id;

    @Column(name = "family_id")
    private Long familyId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "membership_status")
    private String membershipStatus;

    public Long getId() { return id; }
    public Long getFamilyId() { return familyId; }
    public Long getUserId() { return userId; }
    public String getMembershipStatus() { return membershipStatus; }
}
