package tn.esprit._4se2.pi.mappers;

import org.mapstruct.*;
import tn.esprit._4se2.pi.dto.HealthMetrics.HealthMetricsRequest;
import tn.esprit._4se2.pi.dto.HealthMetrics.HealthMetricsResponse;
import tn.esprit._4se2.pi.entities.HealthMetrics;
import tn.esprit._4se2.pi.entities.HealthProfile;

@Mapper(componentModel = "spring", uses = {HealthProfileMapperMapStruct.class})
public interface HealthMetricsMapperMapStruct {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "healthProfileId", target = "healthProfile.id")
    @Mapping(target = "measuredAt", ignore = true)
    HealthMetrics toEntity(HealthMetricsRequest request);

    @Mapping(source = "healthProfile.id", target = "healthProfileId")
    HealthMetricsResponse toResponse(HealthMetrics metrics);

    default HealthProfile mapHealthProfileIdToHealthProfile(Long healthProfileId) {
        if (healthProfileId == null) return null;
        HealthProfile profile = new HealthProfile();
        profile.setId(healthProfileId);
        return profile;
    }
}