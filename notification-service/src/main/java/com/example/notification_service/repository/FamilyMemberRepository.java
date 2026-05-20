package com.example.notification_service.repository;

import com.example.notification_service.entity.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Long> {
    List<FamilyMember> findByFamilyIdAndMembershipStatus(Long familyId, String membershipStatus);
}
