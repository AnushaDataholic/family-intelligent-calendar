package com.example.family_service.controller;

import com.example.family_service.dto.AddFamilyMemberRequest;
import com.example.family_service.dto.CreateFamilyRequest;
import com.example.family_service.dto.FamilyMemberResponse;
import com.example.family_service.dto.FamilyResponse;
import com.example.family_service.service.FamilyService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/families")
public class FamilyController {
    private final FamilyService familyService;
    public FamilyController(FamilyService familyService){
        this.familyService = familyService;
    }

    @PostMapping
    public FamilyResponse createFamily(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreateFamilyRequest request
            ){
        return familyService.createFamily(request, userId);
    }

    @GetMapping
    public List<FamilyResponse> getFamiliesForUser(
            @RequestHeader("X-User-Id") Long userId
    ){
        return familyService.getFamiliesForUser(userId);
    }

    @GetMapping("/{familyId}/members")
    public List<FamilyMemberResponse> getFamilyMembers(
            @PathVariable Long familyId,
            @RequestHeader("X-User-Id") Long userId
    ) {
        return familyService.getFamilyMembers(familyId, userId);
    }

    @PostMapping("/{familyId}/members")
    public FamilyMemberResponse addFamilyMember(
            @PathVariable Long familyId,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody AddFamilyMemberRequest request
    ) {
        return familyService.addFamilyMember(familyId, userId, request);
    }

}
