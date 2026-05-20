package com.example.family_service.service;

import com.example.family_service.dto.AddFamilyMemberRequest;
import com.example.family_service.dto.CreateFamilyRequest;
import com.example.family_service.dto.FamilyResponse;
import com.example.family_service.dto.FamilyMemberResponse;

import java.util.List;

public interface FamilyService {
    FamilyResponse createFamily(CreateFamilyRequest request, Long userId);
    List<FamilyResponse> getFamiliesForUser(Long userId);
    List<FamilyMemberResponse> getFamilyMembers(Long familyId, Long requestingUserId);
    FamilyMemberResponse addFamilyMember(Long familyId, Long requestingUserId, AddFamilyMemberRequest request);


}
