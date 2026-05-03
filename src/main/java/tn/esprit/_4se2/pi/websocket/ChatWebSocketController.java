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
import tn.esprit._4se2.pi.Enum.CallStatus;
import tn.esprit._4se2.pi.Enum.CallType;
import tn.esprit._4se2.pi.Enum.MessageType;
import tn.esprit._4se2.pi.entities.CallRecord;
import tn.esprit._4se2.pi.entities.ChatMessage;
import tn.esprit._4se2.pi.repositories.CallRecordRepository;
import tn.esprit._4se2.pi.repositories.ChatMessageRepository;
import tn.esprit._4se2.pi.repositories.TeamMemberRepository;
import tn.esprit._4se2.pi.repositories.UserRepository;

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
    private final CallRecordRepository callRecordRepository;
    private final UserRepository userRepository;

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

        sessionRegistry.joinRoom(sid, roomId, userId, name, principal != null ? principal.getName() : null);

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

        String transcript = getString(payload, "transcript");

        ChatMessage saved = chatMessageRepository.save(ChatMessage.builder()
            .roomId(roomId)
            .senderId(userId)
            .senderName(name)
            .content(content.trim())
            .transcript(transcript)
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
            "transcript", saved.getTranscript() != null ? saved.getTranscript() : "",
            "timestamp", saved.getCreatedAt().format(ISO)
        ));
        broadcastTyping(roomId);
    }

    // ── /app/chat.typing ─────────────────────────────────────────────────────

    @MessageMapping("/chat.typing")
    public void handleTyping(@Payload Map<String, Object> payload,
                             SimpMessageHeaderAccessor ha,
                             Principal principal) {
        String roomId = getString(payload, "roomId");
        if (roomId == null) return;

        Long userId = getSessionUserId(ha);
        if (!authorizeRoom(roomId, userId, principal)) return;

        String name    = getSessionUserName(ha, null);
        boolean typing = Boolean.TRUE.equals(payload.get("isTyping"));

        sessionRegistry.setTyping(roomId, name, typing);
        broadcastTyping(roomId);
    }

    // ── /app/chat.history ─────────────────────────────────────────────────────

    @MessageMapping("/chat.history")
    public void handleHistory(@Payload Map<String, Object> payload,
                              Principal principal,
                              SimpMessageHeaderAccessor ha) {
        if (principal == null) return;

        String roomId = getString(payload, "roomId");
        if (roomId == null) return;

        Long userId = getSessionUserId(ha);
        if (!authorizeRoom(roomId, userId, principal)) return;

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
        if (teamId == null) return true;
        if (userId == null) {
            if (principal != null) {
                messaging.convertAndSendToUser(principal.getName(), "/queue/errors",
                    Map.of("message", "Authentication required for this chat room.", "status", 401));
            }
            return false;
        }

        PrivateRoomInfo privateRoom = extractPrivateRoomInfo(roomId);
        if (privateRoom != null) {
            boolean participant = userId.equals(privateRoom.firstUserId()) || userId.equals(privateRoom.secondUserId());
            boolean sameTeam = teamMemberRepository.existsByTeamIdAndUserId(privateRoom.teamId(), privateRoom.firstUserId())
                && teamMemberRepository.existsByTeamIdAndUserId(privateRoom.teamId(), privateRoom.secondUserId());

            if (!participant || !sameTeam) {
                if (principal != null) {
                    messaging.convertAndSendToUser(principal.getName(), "/queue/errors",
                        Map.of("message", "Access denied: not allowed in this private conversation.", "status", 403));
                }
                return false;
            }
            return true;
        }

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
        item.put("transcript", m.getTranscript() != null ? m.getTranscript() : "");
        item.put("createdAt", m.getCreatedAt().format(ISO));
        item.put("type", m.getType().name());
        return item;
    }

    private Long extractTeamId(String roomId) {
        if (roomId != null && roomId.startsWith("team_")) {
            String suffix = roomId.substring(5);
            int nextSeparator = suffix.indexOf('_');
            String teamIdPart = nextSeparator >= 0 ? suffix.substring(0, nextSeparator) : suffix;
            try { return Long.parseLong(teamIdPart); }
            catch (NumberFormatException ignored) {}
        }
        return null;
    }

    private PrivateRoomInfo extractPrivateRoomInfo(String roomId) {
        if (roomId == null || !roomId.startsWith("team_") || !roomId.contains("_private_")) {
            return null;
        }

        String[] parts = roomId.split("_");
        if (parts.length != 5 || !"team".equals(parts[0]) || !"private".equals(parts[2])) {
            return null;
        }

        try {
            return new PrivateRoomInfo(
                Long.parseLong(parts[1]),
                Long.parseLong(parts[3]),
                Long.parseLong(parts[4])
            );
        } catch (NumberFormatException ignored) {
            return null;
        }
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

    private static Long getLong(Map<String, Object> m, String key) {
        return toLong(m.get(key));
    }

    private static Long toLong(Object v) {
        if (v instanceof Number n) return n.longValue();
        if (v instanceof String s && !s.isBlank()) { try { return Long.parseLong(s); } catch (NumberFormatException ignored) {} }
        return null;
    }

    private static String now() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
    }

    // ── WebRTC Signaling ─────────────────────────────────────────────────────

    /**
     * /app/call.invite — caller sends an invite to a specific user.
     * Payload: { toUserId, fromUserId, fromUserName, callType, roomId }
     */
    @MessageMapping("/call.invite")
    public void handleCallInvite(@Payload Map<String, Object> payload,
                                 Principal principal,
                                 SimpMessageHeaderAccessor ha) {
        Long callerId = resolveSignalUserId(payload, ha);
        String callerName = resolveSignalUserName(payload, ha, principal);
        String roomId = getString(payload, "roomId");
        String callTypeStr = Optional.ofNullable(getString(payload, "callType")).orElse("audio");
            Long toUserId = getLong(payload, "toUserId");
            log.info("[WebRTC] Call invite from {} ({}) to userId {} in room {}", callerName, callerId, toUserId, roomId);

            if (!authorizeRoom(roomId, callerId, principal)) {
                log.warn("[WebRTC] Authorization failed for caller {} in room {}", callerId, roomId);
                return;
            }

            if (callerId != null && roomId != null) {
                Long teamId = extractTeamId(roomId);
                boolean directCall = toUserId != null && toUserId > 0;
                var callee = directCall ? userRepository.findById(toUserId).orElse(null) : null;
                
                if (directCall && callee == null) {
                    log.warn("[WebRTC] Callee with userId {} not found", toUserId);
                }

                String calleeName = callee != null
                    ? formatDisplayName(callee)
                    : (directCall ? "Team Member" : "Team Members");
            
            CallRecord call = CallRecord.builder()
                .roomId(roomId)
                .teamId(teamId)
                .callerId(callerId)
                .callerName(callerName)
                .calleeId(directCall ? toUserId : 0L)
                .calleeName(calleeName)
                .callType(CallType.valueOf(callTypeStr != null ? callTypeStr.toUpperCase() : "AUDIO"))
                .status(CallStatus.INITIATED)
                .startedAt(LocalDateTime.now())
                .build();
            callRecordRepository.save(call);

            broadcastSystemMessage(roomId, callerName + " started a " + callTypeStr + " call");
            
            Map<String, Object> envelope = new java.util.HashMap<>(payload);
            envelope.put("fromUserId", callerId);
            envelope.put("fromUserName", callerName);
            envelope.put("type", "CALL_INVITE");
            envelope.put("timestamp", now());

            if (directCall && callee != null) {
                String targetEmail = callee.getEmail().toLowerCase();
                log.info("[WebRTC] Relaying invite to user email: {}", targetEmail);
                messaging.convertAndSendToUser(targetEmail, "/queue/call", envelope);
            } else if (teamId != null) {
                log.info("[WebRTC] Broadcasting group call invite to team {}", teamId);
                teamMemberRepository.findByTeamId(teamId).forEach(member -> {
                    if (member.getUser() != null && !member.getUser().getId().equals(callerId)) {
                        String targetEmail = member.getUser().getEmail().toLowerCase();
                        messaging.convertAndSendToUser(targetEmail, "/queue/call", envelope);
                    }
                });
            }
        }
    }

    /** /app/call.offer — SDP offer from caller to callee. */
    @MessageMapping("/call.offer")
    public void handleCallOffer(@Payload Map<String, Object> payload,
                                Principal principal,
                                SimpMessageHeaderAccessor ha) {
        Long fromUserId = resolveSignalUserId(payload, ha);
        String roomId = getString(payload, "roomId");

        if (!authorizeRoom(roomId, fromUserId, principal)) return;

        relayCallMessage("CALL_OFFER", buildRelayPayload(payload, roomId, fromUserId), principal, ha);
    }

    /** /app/call.answer — SDP answer from callee to caller. */
    @MessageMapping("/call.answer")
    public void handleCallAnswer(@Payload Map<String, Object> payload,
                                 Principal principal,
                                 SimpMessageHeaderAccessor ha) {
        Long calleeId = resolveSignalUserId(payload, ha);
        String roomId = getString(payload, "roomId");
        Map<String, Object> relayPayload = buildRelayPayload(payload, roomId, calleeId);
        Long callerId = getLong(relayPayload, "toUserId");

        if (!authorizeRoom(roomId, calleeId, principal)) return;

        if (callerId != null) {
            callRecordRepository.findTopByRoomIdAndCallerIdAndCalleeIdOrderByStartedAtDesc(roomId, callerId, calleeId)
                .ifPresent(call -> {
                    call.setStatus(CallStatus.ONGOING);
                    call.setAnsweredAt(LocalDateTime.now());
                    callRecordRepository.save(call);
                });
        }

        relayCallMessage("CALL_ANSWER", relayPayload, principal, ha);
    }

    /** /app/call.ice — ICE candidate relay (both directions). */
    @MessageMapping("/call.ice")
    public void handleCallIce(@Payload Map<String, Object> payload,
                              Principal principal,
                              SimpMessageHeaderAccessor ha) {
        Long fromUserId = resolveSignalUserId(payload, ha);
        String roomId = getString(payload, "roomId");

        if (!authorizeRoom(roomId, fromUserId, principal)) return;

        relayCallMessage("CALL_ICE", buildRelayPayload(payload, roomId, fromUserId), principal, ha);
    }

    /** /app/call.end — caller or callee ends/hangs up. */
    @MessageMapping("/call.end")
    public void handleCallEnd(@Payload Map<String, Object> payload,
                              Principal principal,
                              SimpMessageHeaderAccessor ha) {
        Long fromId = resolveSignalUserId(payload, ha);
        String roomId = getString(payload, "roomId");
        Map<String, Object> relayPayload = buildRelayPayload(payload, roomId, fromId);
        Long toId = getLong(relayPayload, "toUserId");

        if (!authorizeRoom(roomId, fromId, principal)) return;

        // Try to find the call where 'fromId' was either caller or callee
        callRecordRepository.findTopByRoomIdAndCallerIdAndCalleeIdOrderByStartedAtDesc(roomId, fromId, toId)
            .or(() -> callRecordRepository.findTopByRoomIdAndCallerIdAndCalleeIdOrderByStartedAtDesc(roomId, toId, fromId))
            .ifPresent(call -> {
                if (call.getStatus() == CallStatus.INITIATED) {
                    call.setStatus(CallStatus.MISSED);
                    broadcastSystemMessage(roomId, "Missed " + call.getCallType() + " call from " + call.getCallerName());
                } else {
                    call.setStatus(CallStatus.ENDED);
                    if (call.getAnsweredAt() != null) {
                        long duration = java.time.Duration.between(call.getAnsweredAt(), LocalDateTime.now()).getSeconds();
                        call.setDurationSeconds((int) duration);
                        broadcastSystemMessage(roomId, call.getCallType() + " call ended • " + formatDuration(duration));
                    }
                }
                call.setEndedAt(LocalDateTime.now());
                callRecordRepository.save(call);
            });

        relayCallMessage("CALL_END", relayPayload, principal, ha);
    }

    /** /app/call.reject — callee rejects the call. */
    @MessageMapping("/call.reject")
    public void handleCallReject(@Payload Map<String, Object> payload,
                                 Principal principal,
                                 SimpMessageHeaderAccessor ha) {
        Long calleeId = resolveSignalUserId(payload, ha);
        String roomId = getString(payload, "roomId");
        Map<String, Object> relayPayload = buildRelayPayload(payload, roomId, calleeId);
        Long callerId = getLong(relayPayload, "toUserId");

        if (!authorizeRoom(roomId, calleeId, principal)) return;

        if (callerId != null) {
            callRecordRepository.findTopByRoomIdAndCallerIdAndCalleeIdOrderByStartedAtDesc(roomId, callerId, calleeId)
                .ifPresent(call -> {
                    call.setStatus(CallStatus.REJECTED);
                    call.setEndedAt(LocalDateTime.now());
                    callRecordRepository.save(call);
                    broadcastSystemMessage(roomId, "Call rejected by " + call.getCalleeName());
                });
        }

        relayCallMessage("CALL_REJECT", relayPayload, principal, ha);
    }


    private void broadcastSystemMessage(String roomId, String content) {
        broadcast(roomId, Map.of(
            "type", "SYSTEM",
            "roomId", roomId,
            "content", content,
            "timestamp", now()
        ));
    }

    private String formatDuration(long seconds) {
        return String.format("%d:%02d", seconds / 60, seconds % 60);
    }

    /**
     * Relay a call signaling message to a specific user via /user/{toUserId}/queue/call.
     * The payload must contain a "toUserId" string/number field.
     */
    private void relayCallMessage(String type,
                                  Map<String, Object> payload,
                                  Principal principal,
                                  SimpMessageHeaderAccessor ha) {
        Long toUserId = toLong(payload.get("toUserId"));
        if (toUserId == null) return;
        
        // Resolve ID to Email (Principal name)
        String toUserEmail = userRepository.findById(toUserId)
            .map(tn.esprit._4se2.pi.entities.User::getEmail)
            .map(String::toLowerCase)
            .orElse(null);

        if (toUserEmail == null) {
            log.warn("[WebRTC] Could not resolve toUserId {} to an email for signaling", toUserId);
            return;
        }

        Map<String, Object> envelope = new java.util.HashMap<>(payload);
        Long fromUserId = resolveSignalUserId(payload, ha);
        String fromUserName = resolveSignalUserName(payload, ha, principal);
        if (fromUserId != null) {
            envelope.put("fromUserId", fromUserId);
        }
        if (fromUserName != null) {
            envelope.put("fromUserName", fromUserName);
        }
        envelope.put("type", type);
        envelope.put("timestamp", now());

        log.debug("[WebRTC] Relaying {} from {} to {}", type, fromUserId, toUserEmail);
        messaging.convertAndSendToUser(toUserEmail, "/queue/call", envelope);
    }

    private Map<String, Object> buildRelayPayload(Map<String, Object> payload, String roomId, Long fromUserId) {
        Map<String, Object> relayPayload = new HashMap<>(payload);
        Long toUserId = resolveTargetUserId(relayPayload, roomId, fromUserId);
        if (toUserId != null) {
            relayPayload.put("toUserId", toUserId);
        }
        return relayPayload;
    }

    private Long resolveTargetUserId(Map<String, Object> payload, String roomId, Long fromUserId) {
        Long toUserId = getLong(payload, "toUserId");
        if (toUserId != null) {
            return toUserId;
        }

        PrivateRoomInfo privateRoom = extractPrivateRoomInfo(roomId);
        if (privateRoom == null || fromUserId == null) {
            return null;
        }

        if (fromUserId.equals(privateRoom.firstUserId())) {
            return privateRoom.secondUserId();
        }

        if (fromUserId.equals(privateRoom.secondUserId())) {
            return privateRoom.firstUserId();
        }

        return null;
    }

    private Long resolveSignalUserId(Map<String, Object> payload, SimpMessageHeaderAccessor ha) {
        Long sessionUserId = getSessionUserId(ha);
        return sessionUserId != null ? sessionUserId : getLong(payload, "fromUserId");
    }

    private String resolveSignalUserName(Map<String, Object> payload,
                                         SimpMessageHeaderAccessor ha,
                                         Principal principal) {
        String sessionUserName = getSessionUserName(ha, principal);
        if (sessionUserName != null && !sessionUserName.isBlank() && !"Member".equals(sessionUserName)) {
            return sessionUserName;
        }
        String payloadUserName = getString(payload, "fromUserName");
        return payloadUserName != null ? payloadUserName : sessionUserName;
    }

    private String formatDisplayName(tn.esprit._4se2.pi.entities.User user) {
        String fullName = ((user.getFirstName() != null ? user.getFirstName() : "") + " "
            + (user.getLastName() != null ? user.getLastName() : "")).trim();
        return fullName.isBlank() ? user.getEmail() : fullName;
    }

    private record PrivateRoomInfo(Long teamId, Long firstUserId, Long secondUserId) {}
}
