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

        return HealthProfileResponse.builder()
                .id(entity.getId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .weight(entity.getWeight())
                .height(entity.getHeight())
                .age(entity.getAge())
                .sportPosition(entity.getSportPosition())
                .fitnessStatus(entity.getFitnessStatus())
                .lastUpdated(entity.getLastUpdated())
                .emergencyContact(entity.getEmergencyContact())
                .emergencyPhone(entity.getEmergencyPhone())
                .bloodType(entity.getBloodType())
                .allergies(entity.getAllergies())
                .medicalConditions(entity.getMedicalConditions())
                .bmi(entity.getBmi())
                .bmiCategory(entity.getBmiCategory())
                .gender(entity.getGender())  // Ajout
                .build();
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
        profile.setGender(request.getGender());  // Ajout
    }
}