package com.example.family_service.dto;

public class FamilyResponse {

    private Long id;
    private String familyName;
    private String timezone;
    private Long createdByUserId;

    public FamilyResponse(Long id, String familyName, String timezone, Long createdByUserId) {
        this.id = id;
        this.familyName = familyName;
        this.timezone = timezone;
        this.createdByUserId = createdByUserId;
    }

    public Long getId() {
        return id;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getTimezone() {
        return timezone;
    }

    public Long getCreatedByUserId() {
        return createdByUserId;
    }
}
