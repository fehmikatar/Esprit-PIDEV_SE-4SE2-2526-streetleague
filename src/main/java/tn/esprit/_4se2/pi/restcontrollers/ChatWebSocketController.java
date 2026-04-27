package tn.esprit._4se2.pi.restcontrollers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import tn.esprit._4se2.pi.dto.chat.ChatMessageDTO;
import tn.esprit._4se2.pi.dto.chat.TypingIndicatorDTO;
import tn.esprit._4se2.pi.repositories.UserRepository;
import tn.esprit._4se2.pi.services.Sentiment.SentimentAnalysisService;
import tn.esprit._4se2.pi.services.chat.ChatService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final SentimentAnalysisService sentimentService;
    private final UserRepository userRepository;

    private final Map<String, String> userSessions = new ConcurrentHashMap<>();
    private final Map<String, Map<String, String>> roomUsers = new ConcurrentHashMap<>();

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageDTO message,
                            Principal principal,
                            SimpMessageHeaderAccessor headerAccessor) {
        if (principal == null) {
            return;
        }

        try {
            String email = principal.getName();
            Long userId = userRepository.findByEmail(email).orElseThrow().getId();

            message.setSenderId(userId);
            message.setSenderName(email);

            var sentiment = sentimentService.analyzeText(message.getContent());
            message.setSentimentScore(sentiment.getScore());
            message.setSentimentLabel(sentiment.getSentiment());

            ChatMessageDTO saved = chatService.saveMessage(message, email);
            messagingTemplate.convertAndSend("/topic/room/" + saved.getRoomId(), saved);
        } catch (Exception e) {
            log.error("Error sending message", e);
            messagingTemplate.convertAndSendToUser(
                    principal.getName(),
                    "/queue/errors",
                    Map.of("error", String.valueOf(e.getMessage()))
            );
        }
    }

    @MessageMapping("/chat.typing")
    public void typingIndicator(@Payload TypingIndicatorDTO typing,
                                Principal principal) {
        if (principal == null) {
            return;
        }

        try {
            Long userId = userRepository.findByEmail(principal.getName()).orElseThrow().getId();
            chatService.validateTeamRoomAccess(typing.getRoomId(), userId);
        } catch (Exception e) {
            messagingTemplate.convertAndSendToUser(
                    principal.getName(),
                    "/queue/errors",
                    Map.of("error", String.valueOf(e.getMessage()))
            );
            return;
        }

        messagingTemplate.convertAndSend("/topic/room/" + typing.getRoomId() + "/typing", Map.of(
                "username", principal.getName(),
                "isTyping", typing.isTyping(),
                "roomId", typing.getRoomId()
        ));
    }

    @MessageMapping("/chat.join")
    public void joinRoom(@Payload Map<String, String> payload,
                         Principal principal,
                         SimpMessageHeaderAccessor headerAccessor) {
        if (principal == null) {
            return;
        }

        String roomId = payload.get("roomId");
        String username = principal.getName();
        String sessionId = headerAccessor.getSessionId();

        if (roomId == null || roomId.isBlank()) {
            messagingTemplate.convertAndSendToUser(
                    username,
                    "/queue/errors",
                    Map.of("error", "roomId is required")
            );
            return;
        }

        try {
            Long userId = userRepository.findByEmail(username).orElseThrow().getId();
            chatService.validateTeamRoomAccess(roomId, userId);
        } catch (Exception e) {
            messagingTemplate.convertAndSendToUser(
                    username,
                    "/queue/errors",
                    Map.of("error", String.valueOf(e.getMessage()))
            );
            return;
        }

        userSessions.put(sessionId, username);
        roomUsers.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>()).put(sessionId, username);

        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/members", Map.of(
                "action", "JOIN",
                "user", username,
                "count", roomUsers.get(roomId).size()
        ));

        if (headerAccessor.getSessionAttributes() != null) {
            headerAccessor.getSessionAttributes().put("roomId", roomId);
        }

        log.info("User {} joined room {}", username, roomId);
    }

    @MessageMapping("/chat.leave")
    public void leaveRoom(@Payload Map<String, String> payload,
                          Principal principal,
                          SimpMessageHeaderAccessor headerAccessor) {
        if (principal == null) {
            return;
        }

        String roomId = payload.get("roomId");
        String username = principal.getName();
        String sessionId = headerAccessor.getSessionId();

        if (roomUsers.containsKey(roomId)) {
            roomUsers.get(roomId).remove(sessionId);
            messagingTemplate.convertAndSend("/topic/room/" + roomId + "/members", Map.of(
                    "action", "LEAVE",
                    "user", username,
                    "count", roomUsers.getOrDefault(roomId, Map.of()).size()
            ));
        }
    }

    @MessageMapping("/chat.history")
    public void getHistory(@Payload Map<String, Object> payload,
                           Principal principal) {
        if (principal == null) {
            return;
        }

        String roomId = String.valueOf(payload.get("roomId"));
        int limit = payload.get("limit") instanceof Number number ? number.intValue() : 50;
        Long userId = userRepository.findByEmail(principal.getName()).orElseThrow().getId();
        List<ChatMessageDTO> history = chatService.getMessages(roomId, userId, limit, null);

        messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/history",
                Map.of("roomId", roomId, "messages", history)
        );
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        if (sessionId == null) {
            return;
        }

        String username = userSessions.remove(sessionId);
        List<String> emptyRooms = new ArrayList<>();

        roomUsers.forEach((roomId, members) -> {
            String removedUser = members.remove(sessionId);
            if (removedUser != null) {
                messagingTemplate.convertAndSend("/topic/room/" + roomId + "/members", Map.of(
                        "action", "DISCONNECT",
                        "user", username != null ? username : removedUser,
                        "count", members.size()
                ));
            }
            if (members.isEmpty()) {
                emptyRooms.add(roomId);
            }
        });

        emptyRooms.forEach(roomUsers::remove);
    }
}