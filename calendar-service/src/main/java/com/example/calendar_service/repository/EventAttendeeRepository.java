package com.example.calendar_service.repository;

import com.example.calendar_service.entity.EventAttendee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventAttendeeRepository extends JpaRepository<EventAttendee, Long> {
    List<EventAttendee> findByEventId(Long eventId);
    List<EventAttendee> findByEventIdIn(List<Long> eventIds);
}
