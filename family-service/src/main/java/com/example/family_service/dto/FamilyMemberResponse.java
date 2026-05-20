package com.example.family_service.dto;

public class FamilyMemberResponse {

    private Long id;
    private Long familyId;
    private Long userId;
    private String role;
    private String membershipStatus;

    public FamilyMemberResponse(Long id, Long familyId, Long userId, String role, String membershipStatus) {
        this.id = id;
        this.familyId = familyId;
        this.userId = userId;
        this.role = role;
        this.membershipStatus = membershipStatus;
    }

    public Long getId() {
        return id;
    }

    public Long getFamilyId() {
        return familyId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }

    public String getMembershipStatus() {
        return membershipStatus;
    }
}
