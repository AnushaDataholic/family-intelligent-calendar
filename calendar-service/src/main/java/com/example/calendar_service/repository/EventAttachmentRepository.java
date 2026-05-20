package com.example.calendar_service.repository;

import com.example.calendar_service.entity.EventAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventAttachmentRepository extends JpaRepository<EventAttachment, Long> {
    List<EventAttachment> findByEventIdOrderByCreatedAtDesc(Long eventId);
}
