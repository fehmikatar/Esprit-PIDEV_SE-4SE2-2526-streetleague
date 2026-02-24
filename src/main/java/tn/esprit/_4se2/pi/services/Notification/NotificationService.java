package tn.esprit._4se2.pi.services.Notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.dto.NotificationRequest;
import tn.esprit._4se2.pi.dto.NotificationResponse;
import tn.esprit._4se2.pi.entities.Notification;
import tn.esprit._4se2.pi.mappers.NotificationMapper;
import tn.esprit._4se2.pi.repositories.NotificationRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService implements INotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public NotificationResponse createNotification(NotificationRequest request) {
        log.info("Creating notification for user: {}", request.getUserId());

        Notification notification = notificationMapper.toEntity(request);
        notification.setUserId(request.getUserId());

        Notification savedNotification = notificationRepository.save(notification);
        log.info("Notification created successfully with id: {}", savedNotification.getId());

        return notificationMapper.toResponse(savedNotification);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getNotificationById(Long id) {
        log.info("Fetching notification with id: {}", id);
        return notificationRepository.findById(id)
                .map(notificationMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getAllNotifications() {
        log.info("Fetching all notifications");
        return notificationRepository.findAll()
                .stream()
                .map(notificationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByUserId(Long userId) {
        log.info("Fetching notifications for user: {}", userId);
        return notificationRepository.findByUserId(userId)
                .stream()
                .map(notificationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadNotifications(Long userId) {
        log.info("Fetching unread notifications for user: {}", userId);
        return notificationRepository.findByUserIdAndIsReadFalse(userId)
                .stream()
                .map(notificationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByType(String type) {
        log.info("Fetching notifications by type: {}", type);
        return notificationRepository.findByType(type)
                .stream()
                .map(notificationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public NotificationResponse updateNotification(Long id, NotificationRequest request) {
        log.info("Updating notification with id: {}", id);

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));

        notificationMapper.updateEntity(request, notification);
        Notification updatedNotification = notificationRepository.save(notification);
        log.info("Notification updated successfully with id: {}", id);

        return notificationMapper.toResponse(updatedNotification);
    }

    @Override
    public void deleteNotification(Long id) {
        log.info("Deleting notification with id: {}", id);

        if (!notificationRepository.existsById(id)) {
            throw new RuntimeException("Notification not found with id: " + id);
        }

        notificationRepository.deleteById(id);
        log.info("Notification deleted successfully with id: {}", id);
    }

    @Override
    public void markAsRead(Long id) {
        log.info("Marking notification as read with id: {}", id);

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));

        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());
        notificationRepository.save(notification);
        log.info("Notification marked as read with id: {}", id);
    }
}