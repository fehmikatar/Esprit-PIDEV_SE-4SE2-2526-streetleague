package tn.esprit._4se2.pi.mappers;

import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.dto.HealthProfile.HealthProfileRequest;
import tn.esprit._4se2.pi.dto.HealthProfile.HealthProfileResponse;
import tn.esprit._4se2.pi.entities.HealthProfile;

@Component
public class HealthProfileMapper {

    public HealthProfile toEntity(HealthProfileRequest request) {
        if (request == null) return null;

        HealthProfile profile = new HealthProfile();
        profile.setWeight(request.getWeight());
        profile.setHeight(request.getHeight());
        profile.setAge(request.getAge());
        profile.setSportPosition(request.getSportPosition());
        profile.setFitnessStatus(request.getFitnessStatus());
        profile.setEmergencyContact(request.getEmergencyContact());
        profile.setEmergencyPhone(request.getEmergencyPhone());
        profile.setBloodType(request.getBloodType());
        profile.setAllergies(request.getAllergies());
        profile.setMedicalConditions(request.getMedicalConditions());
        profile.setGender(request.getGender());  // Ajout
        return profile;
    }

    public HealthProfileResponse toResponse(HealthProfile entity) {
        if (entity == null) return null;

        // Extraction sécurisée des données de base
        Double weight = entity.getWeight() != null ? entity.getWeight() : 0.0;
        Double height = entity.getHeight() != null ? entity.getHeight() : 0.0;
        Integer age = entity.getAge() != null ? entity.getAge() : 0;
        String genderStr = entity.getGender() != null ? entity.getGender().name() : "MALE";
        
        Double bmi = entity.getBmi() != null ? entity.getBmi() : 0.0;
        String bmiCat = entity.getBmiCategory() != null ? entity.getBmiCategory() : "Inconnu";
        
        // --- Calculs Métier Avancé ---
        
        // 1. BMR (Harris-Benedict)
        int bmr = 0;
        if (weight > 0 && height > 0 && age > 0) {
            if ("MALE".equals(genderStr)) {
                bmr = (int) Math.round(88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age));
            } else {
                bmr = (int) Math.round(447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age));
            }
        }
        
        // Sécurité si calcul tombe à 0
        if (bmr == 0 && weight > 0) bmr = (int)(weight * 24); // Approximation rapide

        int maintenance = (int) Math.round(bmr * 1.55);
        int loss = Math.max(1200, maintenance - 500);
        int gain = maintenance + 300;

        // 2. Health Score
        int score = calculateHealthScore(entity, bmi);
        String scoreMsg = getHealthScoreMessage(score);

        // 3. Advice
        String advice = generateAdvice(entity, bmi, age);

        return HealthProfileResponse.builder()
                .id(entity.getId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .weight(weight)
                .height(height)
                .age(age)
                .sportPosition(entity.getSportPosition())
                .fitnessStatus(entity.getFitnessStatus())
                .lastUpdated(entity.getLastUpdated())
                .emergencyContact(entity.getEmergencyContact())
                .emergencyPhone(entity.getEmergencyPhone())
                .bloodType(entity.getBloodType())
                .allergies(entity.getAllergies())
                .medicalConditions(entity.getMedicalConditions())
                .bmi(bmi)
                .bmiCategory(bmiCat)
                .gender(entity.getGender())
                .bmr(bmr)
                .maintenanceCalories(maintenance)
                .weightLossCalories(loss)
                .weightGainCalories(gain)
                .healthScore(score)
                .healthScoreMessage(scoreMsg)
                .personalizedAdvice(advice)
                .build();
    }

    private int calculateHealthScore(HealthProfile p, double bmi) {
        int score = 0;
        if (bmi >= 18.5 && bmi < 25) score += 40;
        else if (bmi < 18.5 || (bmi >= 25 && bmi < 30)) score += 25;
        else score += 10;

        if (p.getFitnessStatus() != null) {
            switch (p.getFitnessStatus()) {
                case ACTIVE -> score += 30;
                case RECOVERING -> score += 15;
                case LIMITED -> score += 10;
                case INJURED -> score += 5;
            }
        }

        if (p.getAge() != null) {
            if (p.getAge() < 50) score += 15;
            else if (p.getAge() < 65) score += 10;
            else score += 5;
        }

        boolean hasConditions = p.getMedicalConditions() != null && p.getMedicalConditions().length() > 5;
        score += hasConditions ? 5 : 15;

        return Math.min(100, score);
    }

    private String getHealthScoreMessage(int score) {
        if (score >= 80) return "🌟 Excellent état de santé ! Continuez ainsi.";
        if (score >= 60) return "👍 Bon état de santé, quelques améliorations possibles.";
        if (score >= 40) return "⚠️ Santé à surveiller. Suivez les recommandations.";
        return "❌ État critique. Consultation médicale recommandée.";
    }

    private String generateAdvice(HealthProfile p, double bmi, int age) {
        StringBuilder advice = new StringBuilder();
        if (bmi >= 30) advice.append("🆘 Obésité : consultez un nutritionniste. ");
        else if (bmi >= 25) advice.append("🏃‍♂️ Surpoids : privilégiez une activité cardio. ");
        else if (bmi < 18.5) advice.append("🍎 Sous-poids : enrichissez vos repas en protéines. ");
        else advice.append("✅ IMC idéal : maintenez votre équilibre. ");

        if (age >= 50) advice.append("📆 Bilan santé annuel conseillé. ");
        
        String cond = p.getMedicalConditions() != null ? p.getMedicalConditions().toLowerCase() : "";
        if (cond.contains("diabète")) advice.append("🍬 Surveillance sucre stricte. ");
        if (cond.contains("tension")) advice.append("❤️ Limitez le sel. ");

        if (advice.length() == 0) advice.append("Continuez vos bonnes habitudes sportives !");
        return advice.toString();
    }

    public void updateEntity(HealthProfileRequest request, HealthProfile profile) {
        if (request == null || profile == null) return;

        profile.setWeight(request.getWeight());
        profile.setHeight(request.getHeight());
        profile.setAge(request.getAge());
        profile.setSportPosition(request.getSportPosition());
        profile.setFitnessStatus(request.getFitnessStatus());
        profile.setEmergencyContact(request.getEmergencyContact());
        profile.setEmergencyPhone(request.getEmergencyPhone());
        profile.setBloodType(request.getBloodType());
        profile.setAllergies(request.getAllergies());
        profile.setMedicalConditions(request.getMedicalConditions());
        profile.setGender(request.getGender());
    }
}