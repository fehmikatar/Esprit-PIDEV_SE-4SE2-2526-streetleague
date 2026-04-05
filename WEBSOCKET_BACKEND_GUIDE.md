# Guide d'implémentation WebSocket - Backend Spring

## 📋 Vue d'ensemble

Ce guide explique l'implémentation complète du système de notifications WebSocket côté backend Spring pour les réservations de terrains.

## ✅ Ce qui a été fait

### 1. **Configuration WebSocket** (`src/main/java/tn/esprit/_4se2/pi/config/WebSocketConfig.java`)

Fichier de configuration pour:
- Enabler le broker de messages STOMP
- Configurer les endpoints WebSocket (`/ws`)
- Activer SockJS pour les navigateurs sans WebSocket natif
- Configurer les limites de messages et les timeouts

**Points clés:**
- Endpoint: `/ws`
- Broker destinations:
  - `/app` : pour les messages du client vers le serveur
  - `/topic/notifications` : pour le broadcast à tous les clients
  - `/queue` : pour les messages privés à un utilisateur

### 2. **Gestionnaire d'événements WebSocket** (`src/main/java/tn/esprit/_4se2/pi/websocket/WebSocketEventHandler.java`)

Classe pour traiter les événements WebSocket:
- `@MessageMapping("/join-user-room")` - Accueil de l'utilisateur
- `@MessageMapping("/send-reservation-notification")` - Réception des notifications de réservation
- `@MessageMapping("/send-cancellation-notification")` - Réception des annulations
- Classes internes pour les messages typés

### 3. **Service de notification WebSocket** (`src/main/java/tn/esprit/_4se2/pi/services/WebSocket/WebSocketNotificationService.java`)

Service injecté pour envoyer les notifications:
- `sendReservationNotification()` - Envoyer une notification de réservation
- `sendCancellationNotification()` - Envoyer une notification d'annulation
- `sendNotification()` - Envoyer une notification générique
- `broadcastNotification()` - Diffuser à tous les utilisateurs

### 4. **Intégration dans BookingService**

Modifications apportées:
- Injection du `WebSocketNotificationService`
- Appel automatique dans `createBooking()` 
- Appel automatique dans `cancelBooking()`

### 5. **Configuration application.properties**

Ajout des paramètres WebSocket optionnels:
```properties
spring.websocket.enabled=true
spring.websocket.heartbeat-interval=30000
spring.websocket.heartbeat-value=60000
```

## 🔧 Architecture WebSocket

```
Client Angular (socket.io-client)
          ↓
    Connect to /ws endpoint
          ↓
Spring WebSocket Server (STOMP)
          ↓
WebSocketEventHandler (reçoit les messages)
          ↓
WebSocketNotificationService (traite les notifications)
          ↓
SimpMessagingTemplate (envoie les messages)
          ↓
Client reçoit via /topic/notifications ou /queue/notifications
```

## 📊 Flux de notification complet

### 1. **Lors d'une réservation**

```
1. Client Angular → POST /api/bookings
2. BookingRestController.createBooking()
3. BookingService.createBooking()
4. Réservation sauvegardée en BD
5. WebSocketNotificationService.sendReservationNotification()
6. Message envoyé via SimpMessagingTemplate
7. Client reçoit notification via WebSocket
8. Notification affichée en temps réel
```

### 2. **Lors d'une annulation**

```
1. Client Angular → PATCH /api/bookings/{id}/cancel
2. BookingRestController.cancelBooking()
3. BookingService.cancelBooking()
4. Réservation marquée comme CANCELLED
5. WebSocketNotificationService.sendCancellationNotification()
6. Message envoyé via SimpMessagingTemplate
7. Client reçoit notification via WebSocket
8. Notification affichée en temps réel
```

## 🌐 Endpoints WebSocket

### 1. **Connexion initiale**

**URL**: `ws://localhost:8085/ws` (avec SockJS fallback)

**Client JavaScript/Angular**:
```typescript
import { io } from 'socket.io-client';

const socket = io('http://localhost:8085', {
    transports: ['websocket', 'polling']
});

socket.on('connect', () => {
    console.log('Connecté au serveur WebSocket');
});
```

### 2. **Rejoindre une room utilisateur**

**Topic**: `/app/join-user-room`

```typescript
socket.emit('send', {
    destination: '/app/join-user-room',
    body: {
        userId: 123
    }
});
```

### 3. **Recevoir les notifications**

**Topics disponibles**:
- `/topic/notifications` - Broadcast à tous
- `/user/{userId}/queue/notifications` - Message privé à un utilisateur

```typescript
socket.on('/user/123/queue/notifications', (message) => {
    console.log('Notification reçue:', message);
});
```

## 🚀 Integration avec les autres services

### Envoyer une notification depuis n'importe quel service

```java
@Service
@RequiredArgsConstructor
public class MonService {
    private final WebSocketNotificationService webSocketNotificationService;

    public void notifyUser(Long userId) {
        webSocketNotificationService.sendNotification(
            userId,
            "message",
            "Titre",
            "Message de notification"
        );
    }
}
```

## 🔐 Sécurité

### Authentification WebSocket

Vous pouvez ajouter une configuration pour authentifier les connexions WebSocket:

```java
@Configuration
public class WebSocketSecurityConfig {
    
    @Bean
    public AuthChannelInterceptor authChannelInterceptor() {
        return new AuthChannelInterceptor();
    }
}

@Slf4j
public class AuthChannelInterceptor implements ChannelInterceptor {
    
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("token");
            // Valider le token JWT
            // ...
        }
        
        return message;
    }
}
```

## 📝 Logging et Debugging

### Activer les logs WebSocket

Ajoutez dans `application.properties`:

```properties
logging.level.org.springframework.web.socket=DEBUG
logging.level.org.springframework.messaging=DEBUG
logging.level.tn.esprit._4se2.pi.websocket=DEBUG
```

### Vérifier la connexion

```bash
# Test depuis un terminal (WebSocket ping/pong)
curl -i -N \
  -H "Connection: Upgrade" \
  -H "Upgrade: websocket" \
  -H "Sec-WebSocket-Key: SGVsbG8sIHdvcmxkIQ==" \
  -H "Sec-WebSocket-Version: 13" \
  http://localhost:8085/ws
```

## 🧪 Test manuel avec Postman

1. Installer Postman WebSocket client
2. Connecter à: `ws://localhost:8085/ws`
3. Envoyer un message:
```json
{
    "type": "MESSAGE",
    "destination": "/app/send-reservation-notification",
    "payload": {
        "title": "Test",
        "message": "Test notification",
        "fieldName": "Terrain 1",
        "date": "2024-01-01",
        "time": "14:00",
        "userId": 1
    }
}
```

## 📦 Dépendances requises

Les dépendances suivantes doivent être dans `pom.xml`:

```xml
<!-- WebSocket -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>

<!-- Messaging -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-messaging</artifactId>
</dependency>

<!-- Lombok (pour @Slf4j, etc.) -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

## 🔗 Intégration avec le Frontend Angular

Le frontend Angular attend:
- L'endpoint WebSocket à: `http://localhost:8085/ws`
- Les événements via socket.io-client
- Les messages aux topics: `/topic/notifications` ou `/user/{userId}/queue/notifications`

**Synchronisation Frontend/Backend**:
- ✅ Frontend envoie via socket.io
- ✅ Backend reçoit via WebSocketEventHandler
- ✅ Backend traite via WebSocketNotificationService
- ✅ Backend envoie la notification via SimpMessagingTemplate
- ✅ Frontend reçoit et affiche la notification

## 🎯 Prochaines étapes

1. **Tests d'intégration**: Tester la communication bidirectionnelle
2. **Scalabilité**: Configurer un broker RabbitMQ pour les déploiements en cluster
3. **Audit**: Ajouter l'enregistrement des notifications en BD
4. **Authentification**: Sécuriser les connexions WebSocket avec JWT

## 🐛 Troubleshooting

### Les notifications ne s'affichent pas

1. Vérifier que le service WebSocket est actif:
   ```
   curl http://localhost:8085/ws
   ```

2. Vérifier les logs Spring:
   ```
   grep -i websocket /chemin/vers/logs
   ```

3. Vérifier la console du navigateur Angular pour les erreurs

### CORS erreurs

Modifier `WebSocketConfig.java`:
```java
registry.addEndpoint("/ws")
    .setAllowedOrigins("http://localhost:4200", "YOUR_DOMAIN.com")
    .withSockJS();
```

### Connection timeout

Augmenter les timeouts dans `WebSocketConfig.java`:
```java
registry.setMessageSizeLimit(256 * 1024); // 256 KB
```

## 📞 Support

- [Spring WebSocket Documentation](https://spring.io/guides/gs/messaging-stomp-websocket/)
- [STOMP Protocol](https://stomp.github.io/)
- [Socket.io Documentation](https://socket.io/docs/)
