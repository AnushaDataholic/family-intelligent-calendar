package com.example.family_service.service.impl;

import com.example.family_service.dto.AddFamilyMemberRequest;
import com.example.family_service.dto.CreateFamilyRequest;
import com.example.family_service.dto.FamilyResponse;
import com.example.family_service.entity.Family;
import com.example.family_service.entity.FamilyMember;
import com.example.family_service.repository.FamilyMemberRepository;
import com.example.family_service.repository.FamilyRepository;
import com.example.family_service.service.FamilyService;
//import jakarta.transaction.Transactional;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;
import com.example.family_service.dto.FamilyMemberResponse;

import java.time.LocalDateTime;
import java.util.List;

import com.example.family_service.event.FamilyEventProducer;
import com.example.family_service.event.FamilyMemberAddedEvent;

import static java.util.stream.Collectors.toList;
import com.example.family_service.event.FamilyEventProducer;

@Service

public class FamilyServiceImpl implements FamilyService {
    private final FamilyRepository familyRepository;
    private final FamilyMemberRepository familyMemberRepository;

    private final FamilyEventProducer familyEventProducer;

    public FamilyServiceImpl(
            FamilyRepository familyRepository,
            FamilyMemberRepository familyMemberRepository,
            FamilyEventProducer familyEventProducer
    ){
        this.familyRepository = familyRepository;
        this.familyMemberRepository = familyMemberRepository;
        this.familyEventProducer = familyEventProducer;
    }
    @Override
    @Transactional
    public FamilyResponse createFamily(CreateFamilyRequest request, Long userId){
        Family family = new Family();
        family.setFamilyName(request.getFamilyName().trim());
        family.setTimezone(request.getTimezone().trim());
        family.setCreatedByUserId(userId);

        Family savedFamily = familyRepository.save(family);

        FamilyMember adminMember = new FamilyMember();
        adminMember.setFamilyId(savedFamily.getId());
        adminMember.setUserId(userId);
        adminMember.setRole("ADMIN");
        adminMember.setMembershipStatus("ACTIVE");
        adminMember.setJoinedAt(LocalDateTime.now());

        familyMemberRepository.save(adminMember);

        return new FamilyResponse(
                savedFamily.getId(),
                savedFamily.getFamilyName(),
                savedFamily.getTimezone(),
                savedFamily.getCreatedByUserId()
        );
    }

    @Override
    @Transactional
    public List<FamilyResponse> getFamiliesForUser(Long userId) {
        List<Long> familyIds = familyMemberRepository
                .findByUserIdAndMembershipStatus(userId,"ACTIVE")
                .stream()
                .map(FamilyMember::getFamilyId)
                .toList();
        if(familyIds.isEmpty()){
            return List.of();
        }
        return familyRepository.findByIdIn(familyIds)
                .stream()
                .map(family -> new FamilyResponse(
                        family.getId(),
                        family.getFamilyName(),
                        family.getTimezone(),
                        family.getCreatedByUserId()
                        ))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FamilyMemberResponse> getFamilyMembers(Long familyId, Long requestingUserId) {
        boolean requesterIsMember = familyMemberRepository
                .findByUserIdAndMembershipStatus(requestingUserId, "ACTIVE")
                .stream()
                .anyMatch(member -> member.getFamilyId().equals(familyId));

        if (!requesterIsMember) {
            throw new RuntimeException("You are not a member of this family");
        }

        return familyMemberRepository
                .findByFamilyIdAndMembershipStatus(familyId, "ACTIVE")
                .stream()
                .map(member -> new FamilyMemberResponse(
                        member.getId(),
                        member.getFamilyId(),
                        member.getUserId(),
                        member.getRole(),
                        member.getMembershipStatus()
                ))
                .toList();
    }

    @Override
    @Transactional
    public FamilyMemberResponse addFamilyMember(
            Long familyId,
            Long requestingUserId,
            AddFamilyMemberRequest request
    ) {
        FamilyMember requesterMembership = familyMemberRepository
                .findByFamilyIdAndUserIdAndMembershipStatus(familyId, requestingUserId, "ACTIVE")
                .orElseThrow(() -> new RuntimeException("You are not a member of this family"));

        if (!"ADMIN".equalsIgnoreCase(requesterMembership.getRole())) {
            throw new RuntimeException("Only family admins can add members");
        }

        boolean alreadyMember = familyMemberRepository.existsByFamilyIdAndUserIdAndMembershipStatus(
                familyId,
                request.getUserId(),
                "ACTIVE"
        );

        if (alreadyMember) {
            throw new RuntimeException("User is already an active member of this family");
        }

        FamilyMember member = new FamilyMember();
        member.setFamilyId(familyId);
        member.setUserId(request.getUserId());
        member.setRole(request.getRole().trim().toUpperCase());
        member.setMembershipStatus("ACTIVE");
        member.setJoinedAt(LocalDateTime.now());

        FamilyMember saved = familyMemberRepository.save(member);
        familyEventProducer.publishMemberAdded(
                new FamilyMemberAddedEvent(
                        familyId,
                        saved.getUserId(),
                        requestingUserId,
                        saved.getRole()
                )
        );

        return new FamilyMemberResponse(
                saved.getId(),
                saved.getFamilyId(),
                saved.getUserId(),
                saved.getRole(),
                saved.getMembershipStatus()
        );
    }



}
