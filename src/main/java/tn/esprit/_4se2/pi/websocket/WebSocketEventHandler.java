package tn.esprit._4se2.pi.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Gestionnaire des événements WebSocket
 * Traite les messages envoyés par les clients via /app
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketEventHandler {

    private final SimpMessagingTemplate messagingTemplate;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Événement - Utilisateur rejoint sa room utilisateur
     * Le client envoie son userId pour rejoindre sa room personnelle
     */
    @MessageMapping("/join-user-room")
    public void joinUserRoom(@Payload JoinUserRoomRequest request) {
        log.info("Utilisateur {} a rejoint sa room WebSocket", request.getUserId());
        // La subscription est gérée automatiquement via le client
    }

    /**
     * Événement - Notification de réservation reçue du client
     * Broadcast à tous les utilisateurs connectés
     */
    @MessageMapping("/send-reservation-notification")
    @SendTo("/topic/notifications")
    public ReservationNotificationMessage handleReservationNotification(
            @Payload ReservationNotificationMessage message) {
        log.info("Notification de réservation reçue: {}", message);
        message.setTimestamp(LocalDateTime.now().format(dateTimeFormatter));
        return message;
    }

    /**
     * Événement - Notification d'annulation reçue du client
     * Envoyée à un utilisateur spécifique
     */
    @MessageMapping("/send-cancellation-notification")
    public void handleCancellationNotification(
            @Payload CancellationNotificationMessage message) {
        log.info("Notification d'annulation reçue pour l'utilisateur: {}", message.getUserId());
        message.setTimestamp(LocalDateTime.now().format(dateTimeFormatter));
        
        // Envoyer la notification à l'utilisateur spécifique
        messagingTemplate.convertAndSendToUser(
            message.getUserId().toString(),
            "/queue/notifications",
            message
        );
    }

    /**
     * Événement - Notification générique reçue du client
     * Envoyée à un utilisateur spécifique
     */
    @MessageMapping("/send-notification")
    public void handleGenericNotification(
            @Payload GenericNotificationMessage message) {
        log.info("Notification générique reçue pour l'utilisateur: {}", message.getUserId());
        message.setTimestamp(LocalDateTime.now().format(dateTimeFormatter));
        
        // Envoyer la notification à l'utilisateur spécifique
        messagingTemplate.convertAndSendToUser(
            message.getUserId().toString(),
            "/queue/notifications",
            message
        );
    }

    /**
     * Subscribe - Les clients s'abonnent à ce channel pour recevoir les statuts de connexion
     */
    @SubscribeMapping("/topic/notifications")
    public String subscribeToNotifications() {
        log.info("Un client s'est abonné aux notifications");
        return "Connecté au système de notifications en temps réel";
    }

    // Classes internes pour les messages WebSocket
    
    public static class JoinUserRoomRequest {
        private Long userId;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }
    }

    public static class ReservationNotificationMessage {
        private String type = "reservation";
        private String title;
        private String message;
        private String fieldName;
        private String date;
        private String time;
        private Long userId;
        private String timestamp;

        // Getters et setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getFieldName() { return fieldName; }
        public void setFieldName(String fieldName) { this.fieldName = fieldName; }

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }

        public String getTime() { return time; }
        public void setTime(String time) { this.time = time; }

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

        @Override
        public String toString() {
            return "ReservationNotificationMessage{" +
                    "type='" + type + '\'' +
                    ", title='" + title + '\'' +
                    ", fieldName='" + fieldName + '\'' +
                    ", date='" + date + '\'' +
                    ", time='" + time + '\'' +
                    ", userId=" + userId +
                    '}';
        }
    }

    public static class CancellationNotificationMessage {
        private String type = "cancellation";
        private String title;
        private String message;
        private String fieldName;
        private Long userId;
        private String timestamp;

        // Getters et setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getFieldName() { return fieldName; }
        public void setFieldName(String fieldName) { this.fieldName = fieldName; }

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

        @Override
        public String toString() {
            return "CancellationNotificationMessage{" +
                    "type='" + type + '\'' +
                    ", title='" + title + '\'' +
                    ", fieldName='" + fieldName + '\'' +
                    ", userId=" + userId +
                    '}';
        }
    }

    public static class GenericNotificationMessage {
        private String type;
        private String title;
        private String message;
        private Long userId;
        private String timestamp;

        // Getters et setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

        @Override
        public String toString() {
            return "GenericNotificationMessage{" +
                    "type='" + type + '\'' +
                    ", title='" + title + '\'' +
                    ", userId=" + userId +
                    '}';
        }
    }
}
