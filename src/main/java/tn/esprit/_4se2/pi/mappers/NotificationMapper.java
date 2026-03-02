package tn.esprit._4se2.pi.mappers;

import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.dto.NotificationRequest;
import tn.esprit._4se2.pi.dto.NotificationResponse;
import tn.esprit._4se2.pi.entities.Notification;
import java.time.LocalDateTime;

@Component
public class NotificationMapper {

    public Notification toEntity(NotificationRequest request) {
        if (request == null) return null;

        Notification notification = new Notification();
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setType(request.getType());
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        return notification;
    }

    public NotificationResponse toResponse(Notification entity) {
        if (entity == null) return null;

        return NotificationResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .type(entity.getType())
                .isRead(entity.getIsRead())
                .createdAt(entity.getCreatedAt())
                .readAt(entity.getReadAt())
                .build();
    }

    public void updateEntity(NotificationRequest request, Notification notification) {
        if (request == null || notification == null) return;

        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setType(request.getType());
    }
}