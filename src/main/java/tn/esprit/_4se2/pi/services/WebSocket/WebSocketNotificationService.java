package tn.esprit._4se2.pi.services.WebSocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import tn.esprit._4se2.pi.websocket.WebSocketEventHandler.ReservationNotificationMessage;
import tn.esprit._4se2.pi.websocket.WebSocketEventHandler.CancellationNotificationMessage;
import tn.esprit._4se2.pi.websocket.WebSocketEventHandler.GenericNotificationMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Service pour gérer les notifications WebSocket
 * Utilisé par le BookingService et d'autres services pour envoyer des notifications
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Envoyer une notification de réservation confirmée à un utilisateur spécifique
     *
     * @param userId L'ID de l'utilisateur destinataire
     * @param title Le titre de la notification
     * @param message Le message de la notification
     * @param fieldName Le nom du terrain
     * @param date La date de la réservation
     * @param time L'heure de la réservation
     */
    public void sendReservationNotification(
            Long userId,
            String title,
            String message,
            String fieldName,
            String date,
            String time) {

        try {
            ReservationNotificationMessage notification = new ReservationNotificationMessage();
            notification.setType("reservation");
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setFieldName(fieldName);
            notification.setDate(date);
            notification.setTime(time);
            notification.setUserId(userId);
            notification.setTimestamp(LocalDateTime.now().format(dateTimeFormatter));

            log.info("Envoi de la notification de réservation à l'utilisateur: {}", userId);

            // Envoyer la notification à l'utilisateur spécifique via sa queue personnelle
            messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    "/queue/notifications",
                    notification
            );

            // Également diffuser à tous les utilisateurs abonnés au topic général
            messagingTemplate.convertAndSend(
                    "/topic/notifications",
                    notification
            );
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de la notification de réservation", e);
        }
    }

    /**
     * Envoyer une notification d'annulation de réservation à un utilisateur
     */
    public void sendCancellationNotification(
            Long userId,
            String title,
            String message,
            String fieldName) {

        try {
            CancellationNotificationMessage notification = new CancellationNotificationMessage();
            notification.setType("cancellation");
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setFieldName(fieldName);
            notification.setUserId(userId);
            notification.setTimestamp(LocalDateTime.now().format(dateTimeFormatter));

            log.info("Envoi de la notification d'annulation à l'utilisateur: {}", userId);

            // Envoyer la notification à l'utilisateur spécifique
            messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    "/queue/notifications",
                    notification
            );
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de la notification d'annulation", e);
        }
    }

    /**
     * Envoyer une notification générique à un utilisateur
     */
    public void sendNotification(
            Long userId,
            String type,
            String title,
            String message) {

        try {
            GenericNotificationMessage notification = new GenericNotificationMessage();
            notification.setType(type);
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setUserId(userId);
            notification.setTimestamp(LocalDateTime.now().format(dateTimeFormatter));

            log.info("Envoi de la notification générique à l'utilisateur: {}", userId);

            // Envoyer la notification à l'utilisateur spécifique
            messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    "/queue/notifications",
                    notification
            );
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de la notification générique", e);
        }
    }

    /**
     * Diffuser une notification à tous les utilisateurs connectés
     */
    public void broadcastNotification(
            String type,
            String title,
            String message) {

        try {
            GenericNotificationMessage notification = new GenericNotificationMessage();
            notification.setType(type);
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setTimestamp(LocalDateTime.now().format(dateTimeFormatter));

            log.info("Diffusion de la notification générique à tous les utilisateurs");

            // Diffuser à tous les utilisateurs abonnés au topic
            messagingTemplate.convertAndSend(
                    "/topic/notifications",
                    notification
            );
        } catch (Exception e) {
            log.error("Erreur lors de la diffusion de la notification", e);
        }
    }
}