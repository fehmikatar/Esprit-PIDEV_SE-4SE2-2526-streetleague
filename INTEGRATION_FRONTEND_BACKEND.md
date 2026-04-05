# Guide d'intégration Frontend-Backend WebSocket

## 🎯 Objectif

Connecter le frontend Angular (StreetLeague) avec le backend Spring pour envoyer des notifications WebSocket lors de réservations.

## 📁 Fichiers créés/modifiés

### Backend Spring (C:\Users\fehmi\OneDrive\Documents\Spring\PI)

#### ✅ Nouvelles files
1. **config/WebSocketConfig.java** - Configuration WebSocket STOMP
2. **websocket/WebSocketEventHandler.java** - Gestionnaire d'événements
3. **services/WebSocket/WebSocketNotificationService.java** - Service de notifications

#### ✅ Fichiers modifiés
1. **services/Booking/BookingService.java**
   - Ajout injection `WebSocketNotificationService`
   - Appel à `sendReservationNotification()` dans `createBooking()`
   - Appel à `sendCancellationNotification()` dans `cancelBooking()`

2. **src/main/resources/application.properties**
   - Ajout configuration WebSocket

### Frontend Angular (c:\Users\fehmi\Angular_Workspace\streetLeaguefront-angular)

#### ✅ Nouvelles files
1. **src/app/services/websocket.service.ts** - WebSocket client
2. **src/app/components/websocket-notifications/websocket-notifications.component.ts** - Affichage des notifications

#### ✅ Fichiers modifiés
1. **src/app/services/booking.service.ts**
   - Injection du `WebSocketService`
   - Envoi de notifications dans `reserveField()`

2. **src/environments/environment.ts**
   - Ajout `wsUrl`

## 🔄 Flux de communication

```
┌─────────────────────────┐
│   Angular Frontend      │
│   Port: 4200            │
│   (socket.io-client)    │
└────────────┬────────────┘
             │ Connect via WebSocket
             ↓
┌─────────────────────────┐
│  Spring Backend         │
│  Port: 8085             │
│  /ws endpoint (STOMP)   │
└────────────┬────────────┘
             │ Listen & Broadcast
             ↓
┌─────────────────────────┐
│   All Connected Clients │
│   (Real-time update)    │
└─────────────────────────┘
```

## 🚀 Déploiement et Configuration

### Étape 1: Vérifier les ports

**Frontend:** 
- Port Angular: `4200` (par défaut)
- Command: `npm start`

**Backend:**
- Port Spring: `8085` (configuré)
- WebSocket: `ws://localhost:8085/ws`

Vérifiez dans `application.properties`:
```properties
server.port=8085
```

### Étape 2: Vérifier les configurations CORS

**Backend** (WebSocketConfig.java):
```java
.setAllowedOrigins("http://localhost:4200", "http://localhost:3000")
```

**Frontend** (websocket.service.ts):
```typescript
const wsUrl = environment.wsUrl || 'http://localhost:8085';
```

### Étape 3: Build et redémarrage

**Backend:**
```bash
cd C:\Users\fehmi\OneDrive\Documents\Spring\PI
mvn clean install
mvn spring-boot:run
```

**Frontend:**
```bash
cd c:\Users\fehmi\Angular_Workspace\streetLeaguefront-angular
npm start
```

## 📝 Exemple d'utilisation

### 1. Ajouter le composant de notifications dans App

**app.component.ts:**
```typescript
import { WebSocketNotificationsComponent } from './components/websocket-notifications/websocket-notifications.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    WebSocketNotificationsComponent,
    // ... autres imports
  ],
  template: `
    <app-websocket-notifications></app-websocket-notifications>
    <!-- Votre contenu -->
  `
})
export class AppComponent {}
```

### 2. Déclencher une réservation

**Dans un composant de réservation:**
```typescript
import { BookingService } from '../services/booking.service';

@Component({...})
export class BookingFormComponent {
  constructor(private bookingService: BookingService) {}

  submitReservation(data: any) {
    this.bookingService.reserveField(data).subscribe({
      next: () => {
        // La notification WebSocket est envoyée automatiquement!
        console.log('Réservation créée');
      },
      error: (err) => console.error(err)
    });
  }
}
```

### 3. Résultat attendu

Une notification WebSocket s'affichera AUTOMATIQUEMENT:
- ✅ Titre: "Réservation confirmée"
- ✅ Message: "Votre réservation pour "Terrain" est confirmée."
- ✅ Animée et fermable
- ✅ Indicateur de connexion affichant l'état WebSocket

## 🔍 Vérification du fonctionnement

### Test 1: Vérifier la connexion WebSocket

**Console navigateur (F12):**
```javascript
// Vérifier que le WebSocket est connecté
WebSocket.CONNECTING; // 0
WebSocket.OPEN;       // 1
WebSocket.CLOSING;    // 2
WebSocket.CLOSED;     // 3
```

**Logs Angular (devtools):**
```
WebSocket connecté
```

### Test 2: Créer une réservation et vérifier la notification

1. Ouvrir `http://localhost:4200` dans le navigateur
2. Aller à la page de réservation
3. Remplir le formulaire et soumettre
4. **Attendre:** Une notification WebSocket devrait s'afficher immédiatement

### Test 3: Vérifier les logs backend

```bash
# Dans les logs Spring, vous devriez voir:
[INFO] Utilisateur 123 a rejoint sa room WebSocket
[INFO] Envoi de la notification de réservation à l'utilisateur: 123
```

## 🐛 Troubleshooting

### Problème 1: "WebSocket non connecté"

**Cause:** Le backend n'est pas en cours d'exécution

**Solution:**
```bash
cd C:\Users\fehmi\OneDrive\Documents\Spring\PI
mvn spring-boot:run
```

### Problème 2: "Connection refused"

**Cause:** Port ou URL incorrecte

**Vérifier:**
- URL: `http://localhost:8085/ws`
- Port 8085 est libre
- Pare-feu ne bloque pas la connexion

### Problème 3: Notifications ne s'affichent pas

**Cause:** Le user_id n'est pas dans localStorage

**Vérifier:**
```javascript
// Console du navigateur
localStorage.getItem('user_id');
// Devrait retourner un ID, ex: "123"
```

**Solution:** Assurez-vous que l'utilisateur est connecté avant de créer une réservation

### Problème 4: CORS erreur

**Cause:** Origine non autorisée

**Solution:** Ajouter votre domaine dans `WebSocketConfig.java`:
```java
.setAllowedOrigins("http://localhost:4200", "https://votredomaine.com")
```

## 📊 Scénarios testés

### Scénario 1: Nouvelle réservation
```
✅ Utilisateur crée une réservation
✅ API REST crée booking en BD
✅ WebSocket envoie notification
✅ Interface affiche notification en temps réel
```

### Scénario 2: Annulation de réservation
```
✅ Utilisateur annule une réservation
✅ API REST met à jour le statut
✅ WebSocket envoie notification d'annulation
✅ Interface affiche notification
```

### Scénario 3: Déconnexion/Reconnexion
```
✅ WebSocket se reconnecte automatiquement
✅ Les notifications reçues pendant la déconnexion peuvent être perdues (normalement)
✅ Les notifications après reconnexion fonctionnent
```

## 🔐 Points de sécurité

### À implémenter

1. **JWT dans WebSocket:**
   ```java
   // Dans WebSocketConfig.java
   registry.addEndpoint("/ws")
       .setHandshakeHandler(new CustomHandshakeHandler())
       .withSockJS();
   ```

2. **Validation du userId:**
   ```java
   // Dans WebSocketEventHandler
   @MessageMapping("/send-reservation-notification")
   public void handleNotification(@Payload ReservationNotificationMessage message, 
                                   Authentication auth) {
       // Vérifier que le userId correspond à l'utilisateur authentifié
   }
   ```

3. **Rate limiting:**
   ```properties
   # Ajouter dans application.properties
   spring.websocket.max-messages-per-second=10
   ```

## 📈 Optimisations futures

1. **Persistence DB**: Sauvegarder les notifications en BD
2. **Scalabilité**: Configurer RabbitMQ comme broker STOMP
3. **Audit**: Logger toutes les notifications
4. **Analytics**: Tracer les statistiques d'utilisation

## 📞 Contact et support

- **Backend Issue**: Voir `WEBSOCKET_BACKEND_GUIDE.md`
- **Frontend Issue**: Voir `WEBSOCKET_IMPLEMENTATION_GUIDE.md` (Angular)
- **Integration Issue**: Consulter ce fichier

## ✅ Checklist de déploiement

- [ ] Backend Spring compilé et lancé (port 8085)
- [ ] Frontend Angular lancé (port 4200)
- [ ] WebSocketNotificationsComponent intégré dans app.component.ts
- [ ] Vérifier les CORS: localhost:4200 autorisé
- [ ] Tester une réservation et vérifier la notification
- [ ] Vérifier les logs backend pour les messages
- [ ] Vérifier la console Angular pour les erreurs

**Tout est prêt pour les notifications WebSocket en temps réel! 🚀**
