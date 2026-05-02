package tn.esprit._4se2.pi.services.HealthScore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit._4se2.pi.dto.Athlet.AthleteRequest;
import tn.esprit._4se2.pi.dto.Athlet.ChatResponse;

@Service
public class ChatService {

    @Autowired
    private HealthScoreService healthScoreService;

    public ChatResponse traiterMessage(String message) {
        String msg = message.toLowerCase();
        if (msg.contains("score")) {
            AthleteRequest req = new AthleteRequest();
            req.setDuration(45.0);
            req.setIntensity(5);
            req.setTrainingLoad(225.0);
            req.setSleepHours(7.0);
            req.setSleepQuality(3);
            req.setFatigueLevel(2);
            req.setHydrationLiters(2.2);
            req.setBmi(22.5);
            req.setWeightKg(75.0);
            req.setCaloriesIn(2500);
            req.setNutritionAdherence(3);
            req.setSorenessCode(2);

            double score = healthScoreService.predict(req);

            ChatResponse res = new ChatResponse();
            res.setReply("Score : " + Math.round(score));
            res.setHealthScore(score);
            return res;
        }
        return new ChatResponse("Posez une question sur le score ou le sommeil.");
    }
}