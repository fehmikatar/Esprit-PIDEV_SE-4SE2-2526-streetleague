package tn.esprit._4se2.pi.services.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.dto.chat.ChatMessageDTO;
import tn.esprit._4se2.pi.dto.chat.ChatRoomDTO;
import tn.esprit._4se2.pi.entities.*;
import tn.esprit._4se2.pi.repositories.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final CommunityRepository communityRepository;
    private final TeamMemberRepository teamMemberRepository;

    public ChatMessageDTO saveMessage(ChatMessageDTO messageDTO, String senderEmail) {
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        validateAndNormalizeTeamContext(messageDTO, sender.getId());

        String roomType = normalizeRoomType(messageDTO.getRoomType());
        messageDTO.setRoomType(roomType);
        ChatRoom room = ensureRoomExists(messageDTO);

        ChatMessage message = ChatMessage.builder()
                .content(messageDTO.getContent())
                .sender(sender)
                .roomId(messageDTO.getRoomId())
            .roomType(roomType)
                .sentAt(LocalDateTime.now())
                .isRead(false)
                .isDeleted(false)
                .sentimentScore(messageDTO.getSentimentScore())
                .sentimentLabel(messageDTO.getSentimentLabel())
                .build();

        switch (roomType) {
            case "PRIVATE" -> {
                if (messageDTO.getReceiverId() != null) {
                    userRepository.findById(messageDTO.getReceiverId()).ifPresent(message::setReceiver);
                }
            }
            case "TEAM" -> {
                if (messageDTO.getTeamId() != null) {
                    teamRepository.findById(messageDTO.getTeamId()).ifPresent(message::setTeam);
                }
            }
            case "COMMUNITY" -> {
                if (messageDTO.getCommunityId() != null) {
                    communityRepository.findById(messageDTO.getCommunityId()).ifPresent(message::setCommunity);
                }
            }
        }

        room.setLastActivityAt(LocalDateTime.now());
        chatRoomRepository.save(room);

        ChatMessage saved = chatMessageRepository.save(message);
        return convertToDTO(saved, sender.getId());
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDTO> getMessages(String roomId, Long userId, int limit, Long lastMessageId) {
        validateTeamRoomAccess(roomId, userId);

        int safeLimit = Math.max(1, limit);
        List<ChatMessage> messages = lastMessageId == null
                ? chatMessageRepository.findByRoomIdOrderBySentAtDesc(roomId, PageRequest.of(0, safeLimit))
                : chatMessageRepository.findByRoomIdAndIdLessThanOrderBySentAtDesc(roomId, lastMessageId, PageRequest.of(0, safeLimit));

        chatMessageRepository.markMessagesAsRead(roomId, userId);

        return messages.stream()
                .map(msg -> convertToDTO(msg, userId))
                .collect(Collectors.toList());
    }

    public void markMessagesAsRead(String roomId, Long userId) {
        chatMessageRepository.markMessagesAsRead(roomId, userId);
    }

    @Transactional(readOnly = true)
    public List<ChatRoomDTO> getUserChatRooms(Long userId) {
        List<String> roomIds = chatMessageRepository.findDistinctRoomIdsByUserId(userId);

        return roomIds.stream()
                .map(roomId -> chatRoomRepository.findByRoomId(roomId).map(room -> {
                    List<ChatMessage> lastMessages = chatMessageRepository.findByRoomIdOrderBySentAtDesc(roomId, PageRequest.of(0, 1));
                    long unreadCount = chatMessageRepository.countUnreadMessages(roomId, userId);
                    return ChatRoomDTO.builder()
                            .roomId(room.getRoomId())
                            .roomType(room.getRoomType())
                            .roomName(getRoomName(room, userId))
                            .entityId(room.getEntityId())
                            .lastActivityAt(room.getLastActivityAt())
                            .unreadCount(unreadCount)
                            .lastMessage(!lastMessages.isEmpty() ? convertToDTO(lastMessages.get(0), userId) : null)
                            .build();
                }).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public String getOrCreatePrivateRoom(Long user1Id, Long user2Id) {
        String roomId = String.format("private_%d_%d", Math.min(user1Id, user2Id), Math.max(user1Id, user2Id));
        chatRoomRepository.findByRoomId(roomId).orElseGet(() -> chatRoomRepository.save(ChatRoom.builder()
                .roomId(roomId)
                .roomType("PRIVATE")
                .roomName("Discussion privée")
                .entityId(null)
                .lastActivityAt(LocalDateTime.now())
                .build()));
        return roomId;
    }

    public String getOrCreateTeamRoom(Long teamId) {
        String roomId = "team_" + teamId;
        chatRoomRepository.findByRoomId(roomId).orElseGet(() -> {
            Team team = teamRepository.findById(teamId).orElse(null);
            return chatRoomRepository.save(ChatRoom.builder()
                    .roomId(roomId)
                    .roomType("TEAM")
                    .roomName(team != null ? "Équipe: " + team.getName() : "Chat d'équipe")
                    .entityId(teamId)
                    .lastActivityAt(LocalDateTime.now())
                    .build());
        });
        return roomId;
    }

    public String getOrCreateTeamRoom(Long teamId, Long userId) {
        validateTeamMembership(teamId, userId);
        return getOrCreateTeamRoom(teamId);
    }

    public void validateTeamMembership(Long teamId, Long userId) {
        if (teamId == null) {
            throw new IllegalArgumentException("Team id is required");
        }
        if (userId == null) {
            throw new AccessDeniedException("Authenticated user is required");
        }
        if (!teamMemberRepository.existsByTeam_IdAndUser_Id(teamId, userId)) {
            throw new AccessDeniedException("Only approved team members can access this team chatroom");
        }
    }

    public void validateTeamRoomAccess(String roomId, Long userId) {
        Long teamId = extractTeamIdFromRoomId(roomId);
        if (teamId == null) {
            return;
        }
        validateTeamMembership(teamId, userId);
    }

    public String getOrCreateCommunityRoom(Long communityId) {
        String roomId = "community_" + communityId;
        chatRoomRepository.findByRoomId(roomId).orElseGet(() -> {
            Community community = communityRepository.findById(communityId).orElse(null);
            return chatRoomRepository.save(ChatRoom.builder()
                    .roomId(roomId)
                    .roomType("COMMUNITY")
                    .roomName(community != null ? community.getName() : "Chat communauté")
                    .entityId(communityId)
                    .lastActivityAt(LocalDateTime.now())
                    .build());
        });
        return roomId;
    }

    private ChatRoom ensureRoomExists(ChatMessageDTO messageDTO) {
        String roomType = normalizeRoomType(messageDTO.getRoomType());
        String roomId = messageDTO.getRoomId() == null || messageDTO.getRoomId().isBlank()
            ? roomType.toLowerCase() + "_room"
            : messageDTO.getRoomId();

        return chatRoomRepository.findByRoomId(roomId)
                .orElseGet(() -> chatRoomRepository.save(ChatRoom.builder()
                .roomId(roomId)
                .roomType(roomType)
                        .roomName(resolveRoomName(messageDTO))
                        .entityId(resolveEntityId(messageDTO))
                        .lastActivityAt(LocalDateTime.now())
                        .build()));
    }

        private String normalizeRoomType(String roomType) {
        return roomType == null || roomType.isBlank() ? "PRIVATE" : roomType.trim().toUpperCase();
        }

        private void validateAndNormalizeTeamContext(ChatMessageDTO messageDTO, Long userId) {
            boolean isTeamType = "TEAM".equals(normalizeRoomType(messageDTO.getRoomType()));
            boolean isTeamRoom = extractTeamIdFromRoomId(messageDTO.getRoomId()) != null;

            if (!isTeamType && !isTeamRoom) {
                return;
            }

            Long teamIdFromRoom = extractTeamIdFromRoomId(messageDTO.getRoomId());
            Long teamIdFromPayload = messageDTO.getTeamId();

            if (teamIdFromPayload != null && teamIdFromRoom != null && !teamIdFromPayload.equals(teamIdFromRoom)) {
                throw new IllegalArgumentException("Team id does not match room id");
            }

            Long resolvedTeamId = teamIdFromPayload != null ? teamIdFromPayload : teamIdFromRoom;
            if (resolvedTeamId == null) {
                throw new IllegalArgumentException("Team chat message requires a valid team room id or team id");
            }

            validateTeamMembership(resolvedTeamId, userId);
            messageDTO.setRoomType("TEAM");
            messageDTO.setTeamId(resolvedTeamId);
            messageDTO.setRoomId("team_" + resolvedTeamId);
        }

        private Long extractTeamIdFromRoomId(String roomId) {
            if (roomId == null || !roomId.startsWith("team_")) {
                return null;
            }
            String suffix = roomId.substring("team_".length());
            if (suffix.isBlank()) {
                throw new IllegalArgumentException("Invalid team room id");
            }
            try {
                return Long.parseLong(suffix);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Invalid team room id", ex);
            }
        }

    private Long resolveEntityId(ChatMessageDTO messageDTO) {
        return switch (messageDTO.getRoomType()) {
            case "TEAM" -> messageDTO.getTeamId();
            case "COMMUNITY" -> messageDTO.getCommunityId();
            default -> null;
        };
    }

    private String resolveRoomName(ChatMessageDTO messageDTO) {
        return switch (messageDTO.getRoomType()) {
            case "TEAM" -> {
                if (messageDTO.getTeamId() != null) {
                    yield teamRepository.findById(messageDTO.getTeamId())
                            .map(Team::getName)
                            .map(name -> "Équipe: " + name)
                            .orElse("Chat d'équipe");
                }
                yield "Chat d'équipe";
            }
            case "COMMUNITY" -> {
                if (messageDTO.getCommunityId() != null) {
                    yield communityRepository.findById(messageDTO.getCommunityId())
                            .map(Community::getName)
                            .orElse("Chat communauté");
                }
                yield "Chat communauté";
            }
            default -> "Discussion privée";
        };
    }

    private String getRoomName(ChatRoom room, Long currentUserId) {
        if ("PRIVATE".equals(room.getRoomType()) && room.getEntityId() == null) {
            String[] parts = room.getRoomId().split("_");
            if (parts.length >= 3) {
                Long otherId = Long.parseLong(parts[1].equals(currentUserId.toString()) ? parts[2] : parts[1]);
                User otherUser = userRepository.findById(otherId).orElse(null);
                if (otherUser != null) {
                    return buildFullName(otherUser);
                }
            }
        }
        return room.getRoomName();
    }

    private ChatMessageDTO convertToDTO(ChatMessage message, Long currentUserId) {
        User receiver = message.getReceiver();
        return ChatMessageDTO.builder()
                .id(message.getId())
                .content(message.getContent())
                .senderId(message.getSender().getId())
                .senderName(buildFullName(message.getSender()))
                .senderFirstName(message.getSender().getFirstName())
                .senderLastName(message.getSender().getLastName())
                .senderProfileImage(message.getSender().getProfileImageUrl())
                .receiverId(receiver != null ? receiver.getId() : null)
                .receiverName(receiver != null ? buildFullName(receiver) : null)
                .teamId(message.getTeam() != null ? message.getTeam().getId() : null)
                .teamName(message.getTeam() != null ? message.getTeam().getName() : null)
                .communityId(message.getCommunity() != null ? message.getCommunity().getId() : null)
                .communityName(message.getCommunity() != null ? message.getCommunity().getName() : null)
                .roomId(message.getRoomId())
                .roomType(message.getRoomType())
                .sentAt(message.getSentAt())
                .isRead(message.getIsRead())
                .sentimentScore(message.getSentimentScore())
                .sentimentLabel(message.getSentimentLabel())
                .isOwnMessage(message.getSender().getId().equals(currentUserId))
                .build();
    }

    private String buildFullName(User user) {
        String firstName = user.getFirstName() == null ? "" : user.getFirstName();
        String lastName = user.getLastName() == null ? "" : user.getLastName();
        String fullName = (firstName + " " + lastName).trim();
        return fullName.isEmpty() ? user.getEmail() : fullName;
    }
}