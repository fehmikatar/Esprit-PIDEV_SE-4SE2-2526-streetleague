package tn.esprit._4se2.pi.restcontrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.entities.CallRecord;
import tn.esprit._4se2.pi.repositories.CallRecordRepository;

import java.util.List;

@RestController
@RequestMapping("/api/chat/calls")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CallController {

    private final CallRecordRepository callRecordRepository;

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<CallRecord>> getCallHistoryByRoom(@PathVariable String roomId) {
        return ResponseEntity.ok(callRecordRepository.findByRoomIdOrderByStartedAtDesc(roomId));
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<CallRecord>> getCallHistoryByTeam(@PathVariable Long teamId) {
        return ResponseEntity.ok(callRecordRepository.findByTeamIdOrderByStartedAtDesc(teamId));
    }

    @GetMapping("/my-calls/{userId}")
    public ResponseEntity<List<CallRecord>> getMyCallHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(callRecordRepository.findByCallerIdOrCalleeIdOrderByStartedAtDesc(userId, userId));
    }
}
