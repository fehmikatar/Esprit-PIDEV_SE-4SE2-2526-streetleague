package tn.esprit._4se2.pi.restcontrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import tn.esprit._4se2.pi.dto.Athlet.ChatRequest;
import tn.esprit._4se2.pi.dto.Athlet.ChatResponse;
import tn.esprit._4se2.pi.services.HealthScore.ChatService;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {
        return chatService.traiterMessage(request.getMessage());
    }
}