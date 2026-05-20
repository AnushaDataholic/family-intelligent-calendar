package com.example.notification_service.consumer;

import com.example.notification_service.entity.FamilyMember;
import com.example.notification_service.entity.User;
import com.example.notification_service.repository.FamilyMemberRepository;
import com.example.notification_service.repository.UserRepository;
import com.example.notification_service.service.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationKafkaConsumer {

    private final FamilyMemberRepository familyMemberRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public NotificationKafkaConsumer(
            FamilyMemberRepository familyMemberRepository,
            UserRepository userRepository,
            EmailService emailService
    ) {
        this.familyMemberRepository = familyMemberRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @KafkaListener(topics = "calendar.event.created", groupId = "notification-service")
    public void handleCalendarEventCreated(String message) {
        Long familyId = extractLong(message, "familyId");
        String title = extractString(message, "title");
        String startTime = extractString(message, "startTime");

        List<FamilyMember> members =
                familyMemberRepository.findByFamilyIdAndMembershipStatus(familyId, "ACTIVE");

        for (FamilyMember member : members) {
            userRepository.findById(member.getUserId()).ifPresent(user ->
                    emailService.sendEmail(
                            user.getEmail(),
                            "New family event created: " + title,
                            "A new event was added to your family calendar.\n\n"
                                    + "Title: " + title + "\n"
                                    + "Start time: " + startTime
                    )
            );
            System.out.println("Received calendar.event.created message: " + message);

        }
    }

    @KafkaListener(topics = "family.member.added", groupId = "notification-service")
    public void handleFamilyMemberAdded(String message) {
        Long familyId = extractLong(message, "familyId");
        Long addedUserId = extractLong(message, "addedUserId");
        String role = extractString(message, "role");

        User addedUser = userRepository.findById(addedUserId).orElse(null);
        String addedName = addedUser == null ? "A new member" : addedUser.getEmail();

        List<FamilyMember> members =
                familyMemberRepository.findByFamilyIdAndMembershipStatus(familyId, "ACTIVE");

        for (FamilyMember member : members) {
            userRepository.findById(member.getUserId()).ifPresent(user ->
                    emailService.sendEmail(
                            user.getEmail(),
                            "Family member added",
                            addedName + " was added to your family as " + role + "."
                    )
            );
            System.out.println("Received family.member.added message: " + message);


        }
    }

    private Long extractLong(String json, String key) {
        String value = extractRawValue(json, key).replace("\"", "").trim();
        return Long.valueOf(value);
    }

    private String extractString(String json, String key) {
        return extractRawValue(json, key).replace("\"", "").trim();
    }

    private String extractRawValue(String json, String key) {
        String search = "\"" + key + "\":";
        int start = json.indexOf(search);
        if (start == -1) {
            throw new RuntimeException("Missing key in Kafka message: " + key);
        }

        start = start + search.length();
        int end = json.indexOf(",", start);
        if (end == -1) {
            end = json.indexOf("}", start);
        }

        return json.substring(start, end).trim();
    }
}
