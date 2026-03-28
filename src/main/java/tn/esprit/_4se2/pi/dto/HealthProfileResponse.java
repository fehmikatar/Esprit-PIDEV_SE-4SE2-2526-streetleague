package tn.esprit._4se2.pi.dto;

import lombok.*;
import tn.esprit._4se2.pi.entites.FitnessStatus;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthProfileResponse {
    private Long id;
    private Long userId;
    private String userFirstName;
    private String userLastName;
    private Double weight;
    private Double height;
    private Integer age;
    private String sportPosition;
    private FitnessStatus fitnessStatus;
    private LocalDate lastUpdated;
    private String emergencyContact;
    private String emergencyPhone;
    private String bloodType;
    private String allergies;
    private String medicalConditions;
    private Double bmi;
    private String bmiCategory;
}