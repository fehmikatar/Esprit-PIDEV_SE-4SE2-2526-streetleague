package tn.esprit._4se2.pi.mappers;

import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.dto.SportSpaceRequest;
import tn.esprit._4se2.pi.dto.SportSpaceResponse;
import tn.esprit._4se2.pi.entities.SportSpace;
import java.time.LocalDateTime;

@Component
public class SportSpaceMapper {

    public SportSpace toEntity(SportSpaceRequest request) {
        if (request == null) return null;

        SportSpace sportSpace = new SportSpace();
        sportSpace.setName(request.getName());
        sportSpace.setDescription(request.getDescription());
        sportSpace.setAddress(request.getAddress());
        sportSpace.setLocation(request.getLocation());
        sportSpace.setSportType(request.getSportType());
        sportSpace.setCapacity(request.getCapacity());
        sportSpace.setHourlyRate(request.getHourlyRate());
        sportSpace.setAmenities(request.getAmenities());
        sportSpace.setLatitude(request.getLatitude());
        sportSpace.setLongitude(request.getLongitude());

        sportSpace.setIsAvailable(request.getIsAvailable());
        return sportSpace;
    }

    public SportSpaceResponse toResponse(SportSpace entity) {
        if (entity == null) return null;

        return SportSpaceResponse.builder()
                .id(entity.getId())
                .fieldOwnerId(entity.getFieldOwnerId())
                .name(entity.getName())
                .description(entity.getDescription())
                .address(entity.getAddress())
                .location(entity.getLocation())
                .sportType(entity.getSportType())
                .capacity(entity.getCapacity())
                .hourlyRate(entity.getHourlyRate())
                .amenities(entity.getAmenities())
                .isAvailable(entity.getIsAvailable())
                .build();
    }

    public void updateEntity(SportSpaceRequest request, SportSpace sportSpace) {
        if (request == null || sportSpace == null) return;

        sportSpace.setName(request.getName());
        sportSpace.setDescription(request.getDescription());
        sportSpace.setAddress(request.getAddress());
        sportSpace.setLocation(request.getLocation());
        sportSpace.setSportType(request.getSportType());
        sportSpace.setCapacity(request.getCapacity());
        sportSpace.setHourlyRate(request.getHourlyRate());
        sportSpace.setAmenities(request.getAmenities());
        sportSpace.setIsAvailable(request.getIsAvailable());
    }
}