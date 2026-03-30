package tn.esprit._4se2.pi.dto.HealthProfile;

import lombok.*;
import tn.esprit._4se2.pi.entities.FitnessStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthProfileRequest {
    private Long userId;
    private Double weight;
    private Double height;
    private Integer age;
    private String sportPosition;
    private FitnessStatus fitnessStatus;
    private String emergencyContact;
    private String emergencyPhone;
    private String bloodType;
    private String allergies;
    private String medicalConditions;
}