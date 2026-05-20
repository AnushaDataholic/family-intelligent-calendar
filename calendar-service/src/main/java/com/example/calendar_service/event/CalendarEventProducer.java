package com.example.calendar_service.event;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CalendarEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public CalendarEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishEventCreated(CalendarEventCreatedEvent event) {
        String message = """
                {
                  "familyId": %d,
                  "eventId": %d,
                  "createdByUserId": %d,
                  "title": "%s",
                  "startTime": "%s",
                  "endTime": "%s",
                  "attendeeUserIds": "%s"
                }
                """.formatted(
                event.familyId(),
                event.eventId(),
                event.createdByUserId(),
                event.title(),
                event.startTime(),
                event.endTime(),
                event.attendeeUserIds()
        );

        kafkaTemplate.send("calendar.event.created", message);
    }
}
