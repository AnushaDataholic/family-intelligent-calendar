package com.example.family_service.event;

public record FamilyMemberAddedEvent(
        Long familyId,
        Long addedUserId,
        Long addedByUserId,
        String role
) {}
