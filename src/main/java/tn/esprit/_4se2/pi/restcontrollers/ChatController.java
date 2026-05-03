package tn.esprit._4se2.pi.restcontrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import tn.esprit._4se2.pi.entities.ChatMessage;
import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.repositories.ChatMessageRepository;
import tn.esprit._4se2.pi.repositories.TeamMemberRepository;
import tn.esprit._4se2.pi.repositories.UserRepository;
import tn.esprit._4se2.pi.dto.Athlet.ChatRequest;
import tn.esprit._4se2.pi.dto.Athlet.ChatResponse;
import tn.esprit._4se2.pi.services.HealthScore.ChatService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final TeamMemberRepository teamMemberRepository;
    
    @Autowired
    private ChatService chatService;

    private static final String UPLOAD_DIR = "uploads/chat/";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_DATE_TIME;

    /** GET /api/chat/messages/{roomId}?limit=50&lastMessageId=X — cursor-based history */
    @GetMapping("/messages/{roomId}")
    public ResponseEntity<Map<String, Object>> getMessages(
            @PathVariable String roomId,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(required = false) Long lastMessageId,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = resolveUser(userDetails);
        if (!canAccessRoom(roomId, user)) {
            return ResponseEntity.status(403).build();
        }

        limit = Math.min(Math.max(limit, 1), 100);

        List<ChatMessage> messages = lastMessageId != null
            ? chatMessageRepository.findByRoomIdAndIdLessThanOrderByIdDesc(roomId, lastMessageId, PageRequest.of(0, limit + 1))
            : chatMessageRepository.findByRoomIdOrderByIdDesc(roomId, PageRequest.of(0, limit + 1));

        boolean hasMore = messages.size() > limit;
        if (hasMore) messages = messages.subList(0, limit);

        Collections.reverse(messages); // chronological order

        List<Map<String, Object>> items = messages.stream().map(this::toMap).toList();
        Long newCursor = messages.isEmpty() ? null : messages.get(0).getId();

        Map<String, Object> result = new HashMap<>();
        result.put("items", items);
        result.put("lastMessageId", newCursor != null ? newCursor : "");
        result.put("hasMore", hasMore);
        return ResponseEntity.ok(result);
    }

    /** GET /api/chat/rooms — list team rooms for the current user */
    @GetMapping("/rooms")
    public ResponseEntity<List<Map<String, Object>>> getRooms(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = resolveUser(userDetails);
        if (user == null) return ResponseEntity.status(401).build();

        List<Map<String, Object>> rooms = teamMemberRepository
            .findByUserId(user.getId())
            .stream()
            .map(tm -> {
                Map<String, Object> room = new HashMap<>();
                room.put("roomId", "team_" + tm.getTeam().getId());
                room.put("roomName", tm.getTeam().getName());
                room.put("teamId", tm.getTeam().getId());
                return room;
            })
            .toList();

        return ResponseEntity.ok(rooms);
    }

    /** POST /api/chat/team-room/{teamId} — idempotent: returns the room descriptor */
    @PostMapping("/team-room/{teamId}")
    public ResponseEntity<Map<String, Object>> createTeamRoom(@PathVariable Long teamId) {
        return ResponseEntity.ok(Map.of(
            "roomId", "team_" + teamId,
            "teamId", teamId
        ));
    }

    /** POST /api/chat/community-room/{communityId} */
    @PostMapping("/community-room/{communityId}")
    public ResponseEntity<Map<String, Object>> createCommunityRoom(@PathVariable Long communityId) {
        return ResponseEntity.ok(Map.of(
            "roomId", "community_" + communityId,
            "communityId", communityId
        ));
    }

    /** POST /api/chat/private-room */
    @PostMapping("/private-room")
    public ResponseEntity<Map<String, Object>> createPrivateRoom(
            @RequestParam Long teamId,
            @RequestParam Long memberId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = resolveUser(userDetails);
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }

        if (currentUser.getId().equals(memberId)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Cannot open a private chat with yourself."));
        }

        if (!teamMemberRepository.existsByTeamIdAndUserId(teamId, currentUser.getId())
                || !teamMemberRepository.existsByTeamIdAndUserId(teamId, memberId)) {
            return ResponseEntity.status(403).body(Map.of("message", "Both users must belong to the same team."));
        }

        User otherUser = userRepository.findById(memberId).orElse(null);
        if (otherUser == null) {
            return ResponseEntity.notFound().build();
        }

        Long firstUserId = Math.min(currentUser.getId(), memberId);
        Long secondUserId = Math.max(currentUser.getId(), memberId);
        String roomId = buildPrivateRoomId(teamId, firstUserId, secondUserId);

        return ResponseEntity.ok(Map.of(
            "roomId", roomId,
            "roomName", formatDisplayName(otherUser),
            "teamId", teamId,
            "memberId", otherUser.getId(),
            "memberName", formatDisplayName(otherUser),
            "memberProfileImageUrl", otherUser.getProfileImageUrl() != null ? otherUser.getProfileImageUrl() : ""
        ));
    }

    /** POST /api/chat/mark-read/{roomId} */
    @PostMapping("/mark-read/{roomId}")
    public ResponseEntity<Void> markRead(@PathVariable String roomId) {
        return ResponseEntity.ok().build();
    }
    
    /** POST /api/chat — Intelligent chat endpoint from integration */
    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {
        return chatService.traiterMessage(request.getMessage());
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private User resolveUser(UserDetails userDetails) {
        if (userDetails == null) return null;
        return userRepository.findByEmail(userDetails.getUsername()).orElse(null);
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

    private boolean canAccessRoom(String roomId, User user) {
        Long teamId = extractTeamId(roomId);
        if (teamId == null) {
            return true;
        }

        if (user == null) {
            return false;
        }

        PrivateRoomInfo privateRoom = extractPrivateRoomInfo(roomId);
        if (privateRoom != null) {
            boolean participant = user.getId().equals(privateRoom.firstUserId()) || user.getId().equals(privateRoom.secondUserId());
            boolean sameTeam = teamMemberRepository.existsByTeamIdAndUserId(privateRoom.teamId(), privateRoom.firstUserId())
                && teamMemberRepository.existsByTeamIdAndUserId(privateRoom.teamId(), privateRoom.secondUserId());
            return participant && sameTeam;
        }

        return teamMemberRepository.existsByTeamIdAndUserId(teamId, user.getId());
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

    private String buildPrivateRoomId(Long teamId, Long firstUserId, Long secondUserId) {
        return "team_" + teamId + "_private_" + firstUserId + "_" + secondUserId;
    }

    private String formatDisplayName(User user) {
        String fullName = ((user.getFirstName() != null ? user.getFirstName() : "") + " "
            + (user.getLastName() != null ? user.getLastName() : "")).trim();
        return fullName.isBlank() ? user.getEmail() : fullName;
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

    private record PrivateRoomInfo(Long teamId, Long firstUserId, Long secondUserId) {}

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("roomId") String roomId) {
        
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
            }

            if (file.getSize() > MAX_FILE_SIZE) {
                return ResponseEntity.badRequest().body(Map.of("error", "File too large (max 10MB)"));
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid filename"));
            }

            String extension = "";
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex > 0) {
                extension = originalFilename.substring(dotIndex);
            }

            String uniqueFilename = UUID.randomUUID().toString() + extension;

            Path uploadPath = Paths.get(UPLOAD_DIR + roomId);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "/uploads/chat/" + roomId + "/" + uniqueFilename;

            Map<String, String> response = new HashMap<>();
            response.put("fileUrl", fileUrl);
            response.put("filename", originalFilename);
            response.put("size", String.valueOf(file.getSize()));

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload file: " + e.getMessage()));
        }
    }
}
