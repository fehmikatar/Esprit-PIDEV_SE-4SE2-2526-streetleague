package tn.esprit._4se2.pi.mappers;

import org.mapstruct.*;
import tn.esprit._4se2.pi.dto.DietPlan.DietPlanRequest;
import tn.esprit._4se2.pi.dto.DietPlan.DietPlanResponse;
import tn.esprit._4se2.pi.entities.DietPlan;
import tn.esprit._4se2.pi.entities.HealthProfile;

@Mapper(componentModel = "spring", uses = {HealthProfileMapperMapStruct.class})
public interface DietPlanMapperMapStruct {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "healthProfileId", target = "healthProfile.id")
    DietPlan toEntity(DietPlanRequest request);

    @Mapping(source = "healthProfile.id", target = "healthProfileId")
    DietPlanResponse toResponse(DietPlan plan);

    @Named("mapHealthProfileIdToHealthProfile")
    default HealthProfile mapHealthProfileIdToHealthProfile(Long healthProfileId) {
        if (healthProfileId == null) return null;
        HealthProfile profile = new HealthProfile();
        profile.setId(healthProfileId);
        return profile;
    }
}