package tn.esprit._4se2.pi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tn.esprit._4se2.pi.dto.UserRequest;
import tn.esprit._4se2.pi.dto.UserResponse;
import tn.esprit._4se2.pi.entites.User;

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