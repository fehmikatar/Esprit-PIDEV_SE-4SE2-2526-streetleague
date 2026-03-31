package tn.esprit._4se2.pi.mappers;

import tn.esprit._4se2.pi.dto.MessageRequest;
import tn.esprit._4se2.pi.dto.MessageResponse;
import tn.esprit._4se2.pi.entities.Message;
import tn.esprit._4se2.pi.entities.Team;
import tn.esprit._4se2.pi.entities.User;

import java.time.LocalDateTime;

public class MessageMapper {
    public static Message toEntity(
            MessageRequest dto,
            User sender,
            Team team) {
        Message message = new Message();
        message.setContent(dto.getContent());
        message.setSender(sender);
        message.setTeam(team);
        message.setSentAt(LocalDateTime.now());
        return message;
    }

    public static MessageResponse toDto(Message message) {
        MessageResponse dto = new MessageResponse();
        dto.setId(message.getId());
        dto.setContent(message.getContent());
        dto.setSentAt(message.getSentAt());
        dto.setSenderId(message.getSender().getId());
        dto.setTeamId(message.getTeam().getId());
        return dto;
    }
}
