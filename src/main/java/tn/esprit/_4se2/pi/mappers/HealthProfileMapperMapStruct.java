package tn.esprit._4se2.pi.mappers;

import org.mapstruct.*;
import tn.esprit._4se2.pi.dto.HealthProfile.HealthProfileRequest;
import tn.esprit._4se2.pi.dto.HealthProfile.HealthProfileResponse;
import tn.esprit._4se2.pi.entities.HealthProfile;
import tn.esprit._4se2.pi.entities.User;

@Mapper(componentModel = "spring", uses = {UserMapperMapStruct.class})
public interface HealthProfileMapperMapStruct {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "userId", qualifiedByName = "mapUserIdToUser")
    @Mapping(target = "lastUpdated", ignore = true)
    @Mapping(target = "medicalRecords", ignore = true)
    @Mapping(target = "healthMetrics", ignore = true)
    @Mapping(target = "dietPlans", ignore = true)
    HealthProfile toEntity(HealthProfileRequest request);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.firstName", target = "userFirstName")
    @Mapping(source = "user.lastName", target = "userLastName")
    @Mapping(target = "bmi", expression = "java(profile.getBmi())")
    @Mapping(target = "bmiCategory", expression = "java(profile.getBmiCategory())")
    HealthProfileResponse toResponse(HealthProfile profile);

    @Named("mapUserIdToUser")
    default User mapUserIdToUser(Long userId) {
        if (userId == null) return null;
        User user = new User();
        user.setId(userId);
        return user;
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "userId", qualifiedByName = "mapUserIdToUser")
    @Mapping(target = "medicalRecords", ignore = true)
    @Mapping(target = "healthMetrics", ignore = true)
    @Mapping(target = "dietPlans", ignore = true)
    void updateEntity(@MappingTarget HealthProfile entity, HealthProfileRequest request);
}