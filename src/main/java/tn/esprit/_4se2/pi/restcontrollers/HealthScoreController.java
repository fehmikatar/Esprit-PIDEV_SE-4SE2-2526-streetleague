package tn.esprit._4se2.pi.restcontrollers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.dto.Athlet.AthleteRequest;
import tn.esprit._4se2.pi.services.HealthScore.HealthScoreService;

@RestController
@RequestMapping("/api")
@Tag(name = "Prédiction Health Score", description = "Estime le Health Score d’un athlète à partir de ses 12 caractéristiques")
public class HealthScoreController {

    @Autowired
    private HealthScoreService healthScoreService;

    @PostMapping("/predict")
    @Operation(summary = "Prédire le Health Score", description = "Fournissez toutes les caractéristiques (12 champs)")
    public double predict(@RequestBody AthleteRequest request) {
        return healthScoreService.predict(request);
    }
    @GetMapping("/predict")
    @Operation(summary = "Prédire le Health Score via paramètres d'URL")
    public double predictGet(
            @RequestParam double duration,
            @RequestParam int intensity,
            @RequestParam double trainingLoad,
            @RequestParam double sleepHours,
            @RequestParam int sleepQuality,
            @RequestParam int fatigueLevel,
            @RequestParam double hydrationLiters,
            @RequestParam double bmi,
            @RequestParam double weightKg,
            @RequestParam double caloriesIn,
            @RequestParam int nutritionAdherence,
            @RequestParam int sorenessCode) {

        AthleteRequest request = new AthleteRequest();
        request.setDuration(duration);
        request.setIntensity(intensity);
        request.setTrainingLoad(trainingLoad);
        request.setSleepHours(sleepHours);
        request.setSleepQuality(sleepQuality);
        request.setFatigueLevel(fatigueLevel);
        request.setHydrationLiters(hydrationLiters);
        request.setBmi(bmi);
        request.setWeightKg(weightKg);
        request.setCaloriesIn(caloriesIn);
        request.setNutritionAdherence(nutritionAdherence);
        request.setSorenessCode(sorenessCode);

        return healthScoreService.predict(request);
    }
}