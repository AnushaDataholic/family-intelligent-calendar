package com.example.family_service.entity;

import jakarta.persistence.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "family_members",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_family_members_family_user",
                        columnNames = {"family_id", "user_id"}
                )
        }
)
public class FamilyMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "family_id", nullable = false)
    private Long familyId;

    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(nullable = false, length = 40)
    private String role;

    @Column(name = "membership_status", nullable = false, length = 30)
    private String membershipStatus;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist(){
        this.createdAt = LocalDateTime.now();
    }
    public Long getId(){
        return id;
    }
    public Long getFamilyId(){
        return familyId;
    }
    public void setFamilyId(Long familyId){
        this.familyId = familyId;
    }
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMembershipStatus() {
        return membershipStatus;
    }

    public void setMembershipStatus(String membershipStatus) {
        this.membershipStatus = membershipStatus;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
}




