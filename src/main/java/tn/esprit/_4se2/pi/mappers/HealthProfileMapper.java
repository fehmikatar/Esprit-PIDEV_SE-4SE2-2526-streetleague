package tn.esprit._4se2.pi.mappers;

import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.dto.HealthProfile.HealthProfileRequest;
import tn.esprit._4se2.pi.dto.HealthProfile.HealthProfileResponse;
import tn.esprit._4se2.pi.entities.HealthProfile;
import tn.esprit._4se2.pi.services.User.IUserService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class HealthProfileMapper {

    private final IUserService userService;

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

        if (request.getUserId() != null) {
            profile.setUser(userService.getUserById(request.getUserId()));
        }

        return profile;
    }

    public HealthProfileResponse toResponse(HealthProfile profile) {
        if (profile == null) return null;

        return HealthProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUser() != null ? profile.getUser().getId() : null)
                .userFirstName(profile.getUser() != null ? profile.getUser().getFirstName() : null)
                .userLastName(profile.getUser() != null ? profile.getUser().getLastName() : null)
                .weight(profile.getWeight())
                .height(profile.getHeight())
                .age(profile.getAge())
                .sportPosition(profile.getSportPosition())
                .fitnessStatus(profile.getFitnessStatus())
                .lastUpdated(profile.getLastUpdated())
                .emergencyContact(profile.getEmergencyContact())
                .emergencyPhone(profile.getEmergencyPhone())
                .bloodType(profile.getBloodType())
                .allergies(profile.getAllergies())
                .medicalConditions(profile.getMedicalConditions())
                .bmi(profile.getBmi())
                .bmiCategory(profile.getBmiCategory())
                .build();
    }
}