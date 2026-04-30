package tn.esprit._4se2.pi.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.entities.Notification;
import tn.esprit._4se2.pi.entities.Product;
import tn.esprit._4se2.pi.repositories.NotificationRepository;
import tn.esprit._4se2.pi.services.WebSocket.WebSocketNotificationService;
import tn.esprit._4se2.pi.services.Auth.EmailService;
import tn.esprit._4se2.pi.entities.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Handles real-time low-stock notifications for favorited products.
 * No periodic scheduling — alerts are sent immediately when a user
 * adds a product with low/no stock to their favorites.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class StockNotificationScheduler {

    private final NotificationRepository notificationRepository;
    private final WebSocketNotificationService webSocketNotificationService;
    private final EmailService emailService;

    private static final Integer LOW_STOCK_THRESHOLD = 6;

    /**
     * Called immediately when a product is added to favorites.
     * Sends a single alert email if the product stock is low or out-of-stock.
     * No anti-spam check here — this is triggered by explicit user action (adding to favorites).
     */
    @Transactional
    public void checkAndNotifySingleFavorite(User user, Product product) {
        if (product.getStock() <= LOW_STOCK_THRESHOLD) {
            // Always notify on add-to-favorites for low/out-of-stock products
            sendStockNotification(user, product);
        }
    }

    /**
     * Kept for manual trigger compatibility (used by FavoriteServiceImpl.triggerStockCheck).
     * Does nothing in this simplified mode.
     */
    public void notifyLowStockFavorites() {
        log.info("notifyLowStockFavorites called (no-op in real-time mode).");
    }

    private boolean shouldNotifyUser(Long userId, Product product) {
        List<Notification> userNotifications = notificationRepository.findByUserId(userId);
        // Anti-spam: only send 1 email per product per user every 24 hours
        LocalDateTime recentThreshold = LocalDateTime.now().minusHours(24);

        return userNotifications.stream()
                .filter(n -> "LOW_STOCK".equals(n.getType()))
                .filter(n -> n.getMessage().contains(product.getNom()))
                .noneMatch(n -> n.getCreatedAt() != null && n.getCreatedAt().isAfter(recentThreshold));
    }

    private void sendStockNotification(User user, Product product) {
        boolean isOutOfStock = product.getStock() <= 0;
        String title = isOutOfStock ? "🚨 Out of Stock Alert!" : "⚠️ Low Stock Alert!";
        String message = isOutOfStock
            ? String.format("The product '%s' you added to your favorites is now out of stock!", product.getNom())
            : String.format("Only %d item(s) of '%s' left in stock! Order now before it's gone.", product.getStock(), product.getNom());

        // Save notification to database
        Notification notification = Notification.builder()
                .userId(user.getId())
                .title(title)
                .message(message)
                .type("LOW_STOCK")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);

        // Send real-time WebSocket notification
        webSocketNotificationService.sendNotification(user.getId(), "LOW_STOCK", title, message);

        // Send email alert
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            try {
                emailService.sendLowStockAlertEmail(user.getEmail(), product.getNom(), product.getStock());
                log.info("Low stock alert email sent to {} for product '{}'", user.getEmail(), product.getNom());
            } catch (Exception e) {
                log.error("Failed to send low stock email to {}: {}", user.getEmail(), e.getMessage());
            }
        }
    }
}
