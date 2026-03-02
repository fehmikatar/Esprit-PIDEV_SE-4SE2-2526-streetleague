package tn.esprit._4se2.pi.services.Messsage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.entities.Message;
import tn.esprit._4se2.pi.entities.Team;
import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.repositories.MessageRepository;
import tn.esprit._4se2.pi.repositories.TeamRepository;
import tn.esprit._4se2.pi.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService implements IMessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    @Override
    @Transactional
    public Message addMessage(Message message) {
        // Date d'envoi automatique
        message.setSentAt(LocalDateTime.now());

        // Gérer l'expéditeur
        if (message.getSender() != null && message.getSender().getId() != null) {
            User sender = userRepository.findById(message.getSender().getId())
                    .orElseThrow(() -> new RuntimeException("Expéditeur non trouvé avec l'id : " + message.getSender().getId()));
            message.setSender(sender);
        } else {
            throw new RuntimeException("L'expéditeur est requis");
        }

        // Gérer l'équipe destinataire
        if (message.getTeam() != null && message.getTeam().getId() != null) {
            Team team = teamRepository.findById(message.getTeam().getId())
                    .orElseThrow(() -> new RuntimeException("Équipe non trouvée avec l'id : " + message.getTeam().getId()));
            message.setTeam(team);
        } else {
            throw new RuntimeException("L'équipe destinataire est requise");
        }

        return messageRepository.save(message);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Message getMessageById(Long id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message non trouvé avec l'id : " + id));
    }
}