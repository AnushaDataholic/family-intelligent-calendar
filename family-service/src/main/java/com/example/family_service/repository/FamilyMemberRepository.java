package com.example.family_service.repository;

import com.example.family_service.entity.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Long> {
    List<FamilyMember> findByUserIdAndMembershipStatus(Long userId, String membershipStatus);
    List<FamilyMember> findByFamilyIdAndMembershipStatus(Long familyId, String membershipStatus);

    Optional<FamilyMember> findByFamilyIdAndUserIdAndMembershipStatus(
            Long familyId,
            Long userId,
            String membershipStatus
    );

    boolean existsByFamilyIdAndUserIdAndMembershipStatus(
            Long familyId,
            Long userId,
            String membershipStatus
    );

}
