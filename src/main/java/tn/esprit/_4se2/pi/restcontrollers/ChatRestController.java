package tn.esprit._4se2.pi.restcontrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.dto.chat.ChatMessageDTO;
import tn.esprit._4se2.pi.dto.chat.ChatRoomDTO;
import tn.esprit._4se2.pi.repositories.UserRepository;
import tn.esprit._4se2.pi.services.chat.ChatService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatRestController {

    private final ChatService chatService;
    private final UserRepository userRepository;

    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomDTO>> getUserRooms(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        return ResponseEntity.ok(chatService.getUserChatRooms(userId));
    }

    @GetMapping("/messages/{roomId}")
    public ResponseEntity<List<ChatMessageDTO>> getMessages(
            @PathVariable String roomId,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(required = false) Long lastMessageId,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        return ResponseEntity.ok(chatService.getMessages(roomId, userId, limit, lastMessageId));
    }

    @PostMapping("/private-room")
    public ResponseEntity<Map<String, String>> createPrivateRoom(
            @RequestBody Map<String, Long> request,
            Authentication authentication) {
        Long currentUserId = getUserIdFromAuth(authentication);
        Long otherUserId = request.get("otherUserId");
        String roomId = chatService.getOrCreatePrivateRoom(currentUserId, otherUserId);
        Map<String, String> response = new HashMap<>();
        response.put("roomId", roomId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/team-room/{teamId}")
    public ResponseEntity<Map<String, String>> getTeamRoom(@PathVariable Long teamId, Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        String roomId = chatService.getOrCreateTeamRoom(teamId, userId);
        Map<String, String> response = new HashMap<>();
        response.put("roomId", roomId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/community-room/{communityId}")
    public ResponseEntity<Map<String, String>> getCommunityRoom(@PathVariable Long communityId) {
        String roomId = chatService.getOrCreateCommunityRoom(communityId);
        Map<String, String> response = new HashMap<>();
        response.put("roomId", roomId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/mark-read/{roomId}")
    public ResponseEntity<Void> markMessagesAsRead(@PathVariable String roomId, Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        chatService.markMessagesAsRead(roomId, userId);
        return ResponseEntity.ok().build();
    }

    private Long getUserIdFromAuth(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }
}