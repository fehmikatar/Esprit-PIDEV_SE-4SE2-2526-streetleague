package tn.esprit._4se2.pi.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tn.esprit._4se2.pi.dto.User.UserRequest;
import tn.esprit._4se2.pi.dto.User.UserResponse;
import tn.esprit._4se2.pi.entities.User;

@Mapper(componentModel = "spring")
public interface UserMapperMapStruct {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "appointments", ignore = true)
    @Mapping(target = "healthProfile", ignore = true)
    @Mapping(target = "doctorAppointments", ignore = true)
    @Mapping(target = "treatedRecords", ignore = true)
    User toEntity(UserRequest request);

    UserResponse toResponse(User user);
}