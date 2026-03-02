package tn.esprit._4se2.pi.restcontrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.dto.NotificationRequest;
import tn.esprit._4se2.pi.dto.NotificationResponse;
import tn.esprit._4se2.pi.services.Notification.INotificationService;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationRestController {

    private final INotificationService notificationService;

    @PostMapping
    public ResponseEntity<NotificationResponse> createNotification(
            @Valid @RequestBody NotificationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.createNotification(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> getNotificationById(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.getNotificationById(id));
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getNotificationsByUserId(userId));
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(userId));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByType(@PathVariable String type) {
        return ResponseEntity.ok(notificationService.getNotificationsByType(type));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificationResponse> updateNotification(
            @PathVariable Long id,
            @Valid @RequestBody NotificationRequest request) {
        return ResponseEntity.ok(notificationService.updateNotification(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/mark-as-read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }
}