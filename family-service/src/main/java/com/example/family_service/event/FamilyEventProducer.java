package com.example.family_service.event;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class FamilyEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public FamilyEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishMemberAdded(FamilyMemberAddedEvent event) {
        String message = """
                {
                  "familyId": %d,
                  "addedUserId": %d,
                  "addedByUserId": %d,
                  "role": "%s"
                }
                """.formatted(
                event.familyId(),
                event.addedUserId(),
                event.addedByUserId(),
                event.role()
        );

        kafkaTemplate.send("family.member.added", message);
    }
}
