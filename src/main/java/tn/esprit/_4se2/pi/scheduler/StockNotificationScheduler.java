package tn.esprit._4se2.pi.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.entities.Favorite;
import tn.esprit._4se2.pi.entities.Notification;
import tn.esprit._4se2.pi.entities.Product;
import tn.esprit._4se2.pi.repositories.FavoriteRepository;
import tn.esprit._4se2.pi.repositories.NotificationRepository;
import tn.esprit._4se2.pi.repositories.ProductRepository;
import tn.esprit._4se2.pi.services.WebSocket.WebSocketNotificationService;
import tn.esprit._4se2.pi.services.Auth.EmailService;
import tn.esprit._4se2.pi.entities.User;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class StockNotificationScheduler {

    private final ProductRepository productRepository;
    private final FavoriteRepository favoriteRepository;
    private final NotificationRepository notificationRepository;
    private final WebSocketNotificationService webSocketNotificationService;
    private final EmailService emailService;

    private static final Integer LOW_STOCK_THRESHOLD = 6;

    /**
     * Vérifie périodiquement les produits en rupture imminente qui sont dans les favoris des utilisateurs.
     * S'exécute toutes les 4 heures.
     */
    @Scheduled(cron = "0 0 */4 * * *")
    @Transactional
    public void notifyLowStockFavorites() {
        log.info("Début de la vérification du stock pour les favoris...");

        // 1. Trouver les produits avec un stock faible (entre 1 et 6)
        List<Product> lowStockProducts = productRepository.findByStockLessThanAndDeletedFalse(LOW_STOCK_THRESHOLD + 1);
        
        for (Product product : lowStockProducts) {
            if (product.getStock() <= 0) continue; 

            // 2. Trouver les utilisateurs ayant ce produit en favoris
            List<Favorite> favorites = favoriteRepository.findByProductId(product.getId());
            
            for (Favorite favorite : favorites) {
                User user = favorite.getUser();
                
                if (shouldNotifyUser(user.getId(), product)) {
                    sendStockNotification(user, product);
                }
            }
        }
        
        log.info("Vérification du stock pour les favoris terminée.");
    }

    private boolean shouldNotifyUser(Long userId, Product product) {
        List<Notification> userNotifications = notificationRepository.findByUserId(userId);
        LocalDateTime recentThreshold = LocalDateTime.now().minusHours(24);
        
        return userNotifications.stream()
                .filter(n -> "LOW_STOCK".equals(n.getType()))
                .filter(n -> n.getMessage().contains(product.getNom()))
                .noneMatch(n -> n.getCreatedAt() != null && n.getCreatedAt().isAfter(recentThreshold));
    }

    private void sendStockNotification(User user, Product product) {
        String title = "⚠️ Alerte Stock Favoris !";
        String message = String.format("Rappel : Il ne reste que quelques pièces (%d) du produit '%s' qui vous intéresse dans vos favoris. Achetez plus vite avant la rupture de stock !", 
                product.getStock(), product.getNom());

        // Sauvegarde en base de données
        Notification notification = Notification.builder()
                .userId(user.getId())
                .title(title)
                .message(message)
                .type("LOW_STOCK")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        
        notificationRepository.save(notification);

        // Envoi en temps réel via WebSocket
        webSocketNotificationService.sendNotification(user.getId(), "LOW_STOCK", title, message);
        
        // Envoi de l'e-mail
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            try {
                emailService.sendLowStockAlertEmail(user.getEmail(), product.getNom(), product.getStock());
                log.info("Email de stock faible envoyé à l'adresse {}", user.getEmail());
            } catch (Exception e) {
                log.error("Erreur lors de l'envoi de l'email de stock faible à {}: {}", user.getEmail(), e.getMessage());
            }
        }
        
        log.info("Notification de stock faible envoyée à l'utilisateur {} pour le produit {}", user.getId(), product.getNom());
    }
}
