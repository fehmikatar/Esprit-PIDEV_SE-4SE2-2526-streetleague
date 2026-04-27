package tn.esprit._4se2.pi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

/**
 * Configuration WebSocket pour le système de notifications en temps réel
 * Utilise STOMP (Simple Text Oriented Messaging Protocol) avec SockJS
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configuration du broker de messages
     * - /app : route pour les messages directs du client au serveur
     * - /topic/notifications : canal de broadcast pour les notifications
     * - /user : pour les messages directs à un utilisateur spécifique
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Préfixe pour les messages du client
        config.setApplicationDestinationPrefixes("/app");

        // Utilisateur préfixe pour les messages privés
        config.setUserDestinationPrefix("/user");

        // Utiliser un broker simple pour les distributions de messages
        config.enableSimpleBroker("/topic", "/queue");
    }

    /**
     * Enregistrement des endpoints WebSocket
     * Le client se connecte via /ws avec SockJS fallback
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                // Endpoint principal WebSocket
                .addEndpoint("/ws")
                // Permet les connexions depuis le front-end Angular
                .setAllowedOrigins("http://localhost:4200", "http://localhost:3000")
                // Fallback pour les navigateurs sans WebSocket
                .withSockJS();
    }

    /**
     * Configuration du transport WebSocket
     */
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry
                .setMessageSizeLimit(128 * 1024) // 128 KB
                .setSendTimeLimit(15 * 1000)      // 15 secondes
                .setSendBufferSizeLimit(512 * 1024); // 512 KB
    }
}