package com.example.family_service.repository;

import com.example.family_service.entity.Family;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FamilyRepository extends JpaRepository<Family, Long> {
    List<Family> findByIdIn(List<Long> ids);
}
