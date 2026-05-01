package tn.esprit._4se2.pi.services.HealthScore;

import org.springframework.stereotype.Service;
import tn.esprit._4se2.pi.dto.Athlet.AthleteRequest;

@Service
public class HealthScoreService {

    // Coeffs extraits du modèle (ordre strict des 12 features)
    private static final double[] COEFFS = {
            -1.6990426753051222e-14,   // Duration
            -2.000000000000375,        // Intensity
            9.325873406851315e-15,     // Training_Load
            9.999999999999957,         // Sleep_Hours
            5.017752706393264e-13,     // Sleep_Quality
            3.117899818536052e-14,     // Fatigue_Level
            -1.0534035503693279e-11,   // Hydration_Liters
            3.205766255481423e-11,     // BMI
            1.0250914700415947e-14,    // Weight_kg
            10.000000000000004,        // Calories_In
            -1.0784645745887111e-13,   // Nutrition_Adherence
            8.093216174271875e-14      // Soreness_Code
    };
    private static final double INTERCEPT = -1.4551915228366852e-11;

    public double predict(AthleteRequest request) {
        double[] features = {
                request.getDuration(),
                request.getIntensity(),
                request.getTrainingLoad(),
                request.getSleepHours(),
                request.getSleepQuality(),
                request.getFatigueLevel(),
                request.getHydrationLiters(),
                request.getBmi(),
                request.getWeightKg(),
                request.getCaloriesIn(),
                request.getNutritionAdherence(),
                request.getSorenessCode()
        };
        double sum = INTERCEPT;
        for (int i = 0; i < features.length; i++) {
            sum += COEFFS[i] * features[i];
        }
        return Math.round(sum);   // Health_Score est un entier
    }
}