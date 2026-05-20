package com.example.family_service.dto;
import jakarta.validation.constraints.NotBlank;

public class CreateFamilyRequest {
    @NotBlank(message = "Family name is required")
    private String familyName;

    @NotBlank(message = "Timezone is required")
    private String timezone;

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}
