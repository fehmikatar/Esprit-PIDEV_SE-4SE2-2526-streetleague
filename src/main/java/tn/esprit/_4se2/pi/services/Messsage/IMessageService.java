package tn.esprit._4se2.pi.services.Messsage;

import tn.esprit._4se2.pi.entities.Message;

import java.util.List;

public interface IMessageService {
    Message addMessage(Message message);

    List<Message> getAllMessages();

    Message getMessageById(Long id);
}
