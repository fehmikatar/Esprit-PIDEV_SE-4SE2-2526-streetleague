package tn.esprit._4se2.pi.services.Notification;

import tn.esprit._4se2.pi.dto.NotificationRequest;
import tn.esprit._4se2.pi.dto.NotificationResponse;
import java.util.List;

public interface INotificationService {
    NotificationResponse createNotification(NotificationRequest request);
    NotificationResponse getNotificationById(Long id);
    List<NotificationResponse> getAllNotifications();
    List<NotificationResponse> getNotificationsByUserId(Long userId);
    List<NotificationResponse> getUnreadNotifications(Long userId);
    List<NotificationResponse> getNotificationsByType(String type);
    NotificationResponse updateNotification(Long id, NotificationRequest request);
    void deleteNotification(Long id);
    void markAsRead(Long id);
}