package tn.esprit._4se2.pi.mappers;

import org.mapstruct.*;
import tn.esprit._4se2.pi.dto.AppointmentRequest;
import tn.esprit._4se2.pi.dto.AppointmentResponse;
import tn.esprit._4se2.pi.entities.Appointment;
import tn.esprit._4se2.pi.entities.User;

@Mapper(componentModel = "spring", uses = {UserMapperMapStruct.class})
public interface AppointmentMapperMapStruct {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "doctorId", target = "doctor.id")
    Appointment toEntity(AppointmentRequest request);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "doctor.id", target = "doctorId")
    AppointmentResponse toResponse(Appointment appointment);

    @Named("mapUserIdToUser")
    default User mapUserIdToUser(Long userId) {
        if (userId == null) return null;
        User user = new User();
        user.setId(userId);
        return user;
    }
}