package tn.esprit._4se2.pi.mappers;

import org.mapstruct.*;
import tn.esprit._4se2.pi.dto.MedicalRecordRequest;
import tn.esprit._4se2.pi.dto.MedicalRecordResponse;
import tn.esprit._4se2.pi.entities.MedicalRecord;
import tn.esprit._4se2.pi.entities.HealthProfile;
import tn.esprit._4se2.pi.entities.User;

@Mapper(componentModel = "spring", uses = {HealthProfileMapperMapStruct.class, UserMapperMapStruct.class})
public interface MedicalRecordMapperMapStruct {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "healthProfileId", target = "healthProfile.id")
    @Mapping(source = "treatedByDoctorId", target = "treatedBy.id")
    MedicalRecord toEntity(MedicalRecordRequest request);

    @Mapping(source = "healthProfile.id", target = "healthProfileId")
    @Mapping(source = "treatedBy.id", target = "treatedByDoctorId")
    MedicalRecordResponse toResponse(MedicalRecord record);

    @Named("mapHealthProfileIdToHealthProfile")
    default HealthProfile mapHealthProfileIdToHealthProfile(Long healthProfileId) {
        if (healthProfileId == null) return null;
        HealthProfile profile = new HealthProfile();
        profile.setId(healthProfileId);
        return profile;
    }

    @Named("mapDoctorIdToUser")
    default User mapDoctorIdToUser(Long doctorId) {
        if (doctorId == null) return null;
        User user = new User();
        user.setId(doctorId);
        return user;
    }
}