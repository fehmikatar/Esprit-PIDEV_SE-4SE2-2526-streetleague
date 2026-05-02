package tn.esprit._4se2.pi.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import tn.esprit._4se2.pi.Enum.MessageType;
import tn.esprit._4se2.pi.entities.ChatMessage;
import tn.esprit._4se2.pi.repositories.ChatMessageRepository;
import tn.esprit._4se2.pi.repositories.TeamMemberRepository;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final SimpMessagingTemplate messaging;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionRegistry sessionRegistry;
    private final TeamMemberRepository teamMemberRepository;

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_DATE_TIME;

    // ── /app/chat.join ────────────────────────────────────────────────────────

    @MessageMapping("/chat.join")
    public void handleJoin(@Payload Map<String, Object> payload,
                           SimpMessageHeaderAccessor ha,
                           Principal principal) {
        String roomId = getString(payload, "roomId");
        if (roomId == null) return;

        Long userId   = getSessionUserId(ha);
        String name   = getSessionUserName(ha, principal);
        String sid    = ha.getSessionId();

        if (!authorizeRoom(roomId, userId, principal)) return;

        sessionRegistry.joinRoom(sid, roomId, userId, name);

        broadcast(roomId, Map.of(
            "type", "USER_JOINED", "roomId", roomId,
            "senderName", name,
            "content", name + " joined the chat",
            "timestamp", now()
        ));
        broadcastMembers(roomId);
    }

    // ── /app/chat.send ────────────────────────────────────────────────────────

    @MessageMapping("/chat.send")
    public void handleSend(@Payload Map<String, Object> payload,
                           SimpMessageHeaderAccessor ha,
                           Principal principal) {
        String roomId  = getString(payload, "roomId");
        String content = getString(payload, "content");
        if (roomId == null || content == null || content.isBlank()) return;

        Long userId = getSessionUserId(ha);
        String name = getSessionUserName(ha, principal);

        if (!authorizeRoom(roomId, userId, principal)) return;

        ChatMessage saved = chatMessageRepository.save(ChatMessage.builder()
            .roomId(roomId)
            .senderId(userId)
            .senderName(name)
            .content(content.trim())
            .type(MessageType.TEXT)
            .createdAt(LocalDateTime.now())
            .build());

        sessionRegistry.setTyping(roomId, name, false);

        broadcast(roomId, Map.of(
            "type", "MESSAGE",
            "id", saved.getId(),
            "roomId", roomId,
            "senderId", saved.getSenderId() != null ? saved.getSenderId() : 0,
            "senderName", name,
            "content", saved.getContent(),
            "timestamp", saved.getCreatedAt().format(ISO)
        ));
        broadcastTyping(roomId);
    }

    // ── /app/chat.typing ─────────────────────────────────────────────────────

    @MessageMapping("/chat.typing")
    public void handleTyping(@Payload Map<String, Object> payload,
                             SimpMessageHeaderAccessor ha) {
        String roomId = getString(payload, "roomId");
        if (roomId == null) return;

        String name    = getSessionUserName(ha, null);
        boolean typing = Boolean.TRUE.equals(payload.get("isTyping"));

        sessionRegistry.setTyping(roomId, name, typing);
        broadcastTyping(roomId);
    }

    // ── /app/chat.history ─────────────────────────────────────────────────────

    @MessageMapping("/chat.history")
    public void handleHistory(@Payload Map<String, Object> payload,
                              Principal principal) {
        if (principal == null) return;

        String roomId = getString(payload, "roomId");
        if (roomId == null) return;

        int limit = toInt(payload.get("limit"), 50);
        limit = Math.min(Math.max(limit, 1), 100);

        Long lastId = toLong(payload.get("lastMessageId"));

        List<ChatMessage> messages = lastId != null
            ? chatMessageRepository.findByRoomIdAndIdLessThanOrderByIdDesc(roomId, lastId, PageRequest.of(0, limit + 1))
            : chatMessageRepository.findByRoomIdOrderByIdDesc(roomId, PageRequest.of(0, limit + 1));

        boolean hasMore = messages.size() > limit;
        if (hasMore) messages = messages.subList(0, limit);

        // Return in chronological order
        Collections.reverse(messages);

        List<Map<String, Object>> items = messages.stream().map(this::toMap).toList();
        Long newCursor = messages.isEmpty() ? null : messages.get(0).getId();

        messaging.convertAndSendToUser(principal.getName(), "/queue/history", Map.of(
            "items", items,
            "lastMessageId", newCursor != null ? newCursor : "",
            "hasMore", hasMore
        ));
    }

    // ── /app/chat.leave ───────────────────────────────────────────────────────

    @MessageMapping("/chat.leave")
    public void handleLeave(@Payload Map<String, Object> payload,
                            SimpMessageHeaderAccessor ha) {
        String sessionId = ha.getSessionId();
        String name      = getSessionUserName(ha, null);
        String roomId    = getString(payload, "roomId");

        if (roomId != null) {
            sessionRegistry.setTyping(roomId, name, false);
        }

        ChatSessionRegistry.LeaveResult result = sessionRegistry.leaveRoom(sessionId);
        if (result != null && result.roomId() != null) {
            if (result.user() != null) {
                broadcast(result.roomId(), Map.of(
                    "type", "USER_LEFT", "roomId", result.roomId(),
                    "senderName", result.user().userName(),
                    "content", result.user().userName() + " left the chat",
                    "timestamp", now()
                ));
            }
            broadcastMembers(result.roomId());
        }
    }

    // ── disconnect event ──────────────────────────────────────────────────────

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        ChatSessionRegistry.LeaveResult result = sessionRegistry.leaveRoom(event.getSessionId());
        if (result != null && result.roomId() != null) {
            if (result.user() != null) {
                broadcast(result.roomId(), Map.of(
                    "type", "USER_LEFT", "roomId", result.roomId(),
                    "senderName", result.user().userName(),
                    "content", result.user().userName() + " disconnected",
                    "timestamp", now()
                ));
                sessionRegistry.setTyping(result.roomId(), result.user().userName(), false);
                broadcastTyping(result.roomId());
            }
            broadcastMembers(result.roomId());
        }
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private boolean authorizeRoom(String roomId, Long userId, Principal principal) {
        Long teamId = extractTeamId(roomId);
        if (teamId == null || userId == null) return true; // non-team rooms: allow

        if (!teamMemberRepository.existsByTeamIdAndUserId(teamId, userId)) {
            if (principal != null) {
                messaging.convertAndSendToUser(principal.getName(), "/queue/errors",
                    Map.of("message", "Access denied: not a team member.", "status", 403));
            }
            return false;
        }
        return true;
    }

    private void broadcast(String roomId, Object payload) {
        messaging.convertAndSend("/topic/room/" + roomId, payload);
    }

    private void broadcastTyping(String roomId) {
        messaging.convertAndSend("/topic/room/" + roomId + "/typing",
            Map.of("typingUsers", sessionRegistry.getTypingUsers(roomId)));
    }

    private void broadcastMembers(String roomId) {
        Collection<ChatSessionRegistry.UserInfo> members = sessionRegistry.getRoomMembers(roomId);
        List<Map<String, Object>> list = members.stream()
            .map(m -> {
                Map<String, Object> entry = new HashMap<>();
                entry.put("userId", m.userId());
                entry.put("userName", m.userName());
                return entry;
            })
            .toList();
        messaging.convertAndSend("/topic/room/" + roomId + "/members",
            Map.of("onlineCount", list.size(), "members", list));
    }

    private Map<String, Object> toMap(ChatMessage m) {
        Map<String, Object> item = new HashMap<>();
        item.put("id", m.getId());
        item.put("roomId", m.getRoomId());
        item.put("senderId", m.getSenderId() != null ? m.getSenderId() : 0);
        item.put("senderName", m.getSenderName() != null ? m.getSenderName() : "Member");
        item.put("content", m.getContent());
        item.put("createdAt", m.getCreatedAt().format(ISO));
        item.put("type", m.getType().name());
        return item;
    }

    private Long extractTeamId(String roomId) {
        if (roomId != null && roomId.startsWith("team_")) {
            try { return Long.parseLong(roomId.substring(5)); }
            catch (NumberFormatException ignored) {}
        }
        return null;
    }

    private Long getSessionUserId(SimpMessageHeaderAccessor ha) {
        Object v = ha.getSessionAttributes() != null ? ha.getSessionAttributes().get("userId") : null;
        return v instanceof Long l ? l : null;
    }

    private String getSessionUserName(SimpMessageHeaderAccessor ha, Principal principal) {
        Object v = ha.getSessionAttributes() != null ? ha.getSessionAttributes().get("userName") : null;
        if (v instanceof String s && !s.isBlank()) return s;
        if (principal != null) return principal.getName();
        return "Member";
    }

    private static String getString(Map<String, Object> m, String key) {
        Object v = m.get(key);
        return v instanceof String s && !s.isBlank() ? s : null;
    }

    private static int toInt(Object v, int fallback) {
        if (v instanceof Number n) return n.intValue();
        if (v instanceof String s) { try { return Integer.parseInt(s); } catch (NumberFormatException ignored) {} }
        return fallback;
    }

    private static Long toLong(Object v) {
        if (v instanceof Number n) return n.longValue();
        if (v instanceof String s && !s.isBlank()) { try { return Long.parseLong(s); } catch (NumberFormatException ignored) {} }
        return null;
    }

    private static String now() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
    }
}
