package com.example.calendar_service.repository;

import com.example.calendar_service.entity.Event;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("""
        select count(e) > 0
        from Event e
        where e.familyId = :familyId
          and e.status <> 'CANCELED'
          and e.startTime < :endTime
          and e.endTime > :startTime
          and exists (
              select a.id from EventAttendee a
              where a.eventId = e.id
              and a.userId in :attendeeUserIds
          )
    """)
    boolean hasConflict(Long familyId, List<Long> attendeeUserIds, LocalDateTime startTime, LocalDateTime endTime);

    List<Event> findByFamilyIdAndStartTimeLessThanAndEndTimeGreaterThanAndStatusNotOrderByStartTimeAsc(
            Long familyId, LocalDateTime to, LocalDateTime from, String status);
}
