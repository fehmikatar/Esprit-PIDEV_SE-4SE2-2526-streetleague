package tn.esprit._4se2.pi.websocket;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatSessionRegistry {

    // roomId -> { sessionId -> UserInfo }
    private final Map<String, Map<String, UserInfo>> roomSessions = new ConcurrentHashMap<>();

    // sessionId -> roomId  (for disconnect cleanup)
    private final Map<String, String> sessionRoom = new ConcurrentHashMap<>();

    // roomId -> set of user names currently typing
    private final Map<String, Set<String>> typingUsers = new ConcurrentHashMap<>();

    public void joinRoom(String sessionId, String roomId, Long userId, String userName, String email) {
        sessionRoom.put(sessionId, roomId);
        roomSessions
            .computeIfAbsent(roomId, k -> new ConcurrentHashMap<>())
            .put(sessionId, new UserInfo(userId, userName, sessionId, email));
    }

    /** Removes the session from its room and returns info needed for the leave broadcast. */
    public LeaveResult leaveRoom(String sessionId) {
        String roomId = sessionRoom.remove(sessionId);
        if (roomId == null) return null;

        Map<String, UserInfo> sessions = roomSessions.get(roomId);
        UserInfo user = sessions != null ? sessions.remove(sessionId) : null;

        if (sessions != null && sessions.isEmpty()) {
            roomSessions.remove(roomId);
        }

        if (user != null) {
            Set<String> typing = typingUsers.get(roomId);
            if (typing != null) typing.remove(user.userName);
        }

        return new LeaveResult(roomId, user);
    }

    public Collection<UserInfo> getRoomMembers(String roomId) {
        Map<String, UserInfo> sessions = roomSessions.get(roomId);
        return sessions != null ? List.copyOf(sessions.values()) : Collections.emptyList();
    }

    public void setTyping(String roomId, String userName, boolean isTyping) {
        if (userName == null || userName.isBlank()) return;
        Set<String> typing = typingUsers.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet());
        if (isTyping) typing.add(userName);
        else typing.remove(userName);
    }

    public List<String> getTypingUsers(String roomId) {
        Set<String> typing = typingUsers.get(roomId);
        return typing != null ? List.copyOf(typing) : Collections.emptyList();
    }

    public record UserInfo(Long userId, String userName, String sessionId, String email) {}

    public record LeaveResult(String roomId, UserInfo user) {}
}
