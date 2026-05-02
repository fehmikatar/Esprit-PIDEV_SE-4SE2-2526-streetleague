package tn.esprit._4se2.pi.dto.HealthProfile;

import lombok.*;
import lombok.experimental.FieldDefaults;
import tn.esprit._4se2.pi.Enum.FitnessStatus;
import tn.esprit._4se2.pi.Enum.Gender;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HealthProfileResponse {
    Long id;
    Long userId;
    Double weight;
    Double height;
    Integer age;
    String sportPosition;
    FitnessStatus fitnessStatus;
    LocalDate lastUpdated;
    String emergencyContact;
    String emergencyPhone;
    String bloodType;
    String allergies;
    String medicalConditions;
    Double bmi;               // calculated
    String bmiCategory;       // calculated
    Gender gender;
    
    // Nouveaux champs calculés (Métier Avancé)
    Integer bmr;
    Integer maintenanceCalories;
    Integer weightLossCalories;
    Integer weightGainCalories;
    Integer healthScore;
    String healthScoreMessage;
    String personalizedAdvice;
}