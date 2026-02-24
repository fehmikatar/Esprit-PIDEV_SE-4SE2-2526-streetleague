package tn.esprit._4se2.pi.mappers;

import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.dto.SportSpaceAvailabilityRequest;
import tn.esprit._4se2.pi.dto.SportSpaceAvailabilityResponse;
import tn.esprit._4se2.pi.entities.SportSpaceAvailability;

@Component
public class SportSpaceAvailabilityMapper {

    public SportSpaceAvailability toEntity(SportSpaceAvailabilityRequest request) {
        if (request == null) return null;

        SportSpaceAvailability availability = new SportSpaceAvailability();
        availability.setAvailableFrom(request.getAvailableFrom());
        availability.setAvailableTo(request.getAvailableTo());
        availability.setTotalSlots(request.getTotalSlots());
        availability.setBookedSlots(0);
        availability.setStatus("AVAILABLE");
        return availability;
    }

    public SportSpaceAvailabilityResponse toResponse(SportSpaceAvailability entity) {
        if (entity == null) return null;

        return SportSpaceAvailabilityResponse.builder()
                .id(entity.getId())
                .sportSpaceId(entity.getSportSpaceId())
                .availableFrom(entity.getAvailableFrom())
                .availableTo(entity.getAvailableTo())
                .totalSlots(entity.getTotalSlots())
                .bookedSlots(entity.getBookedSlots())
                .availableSlots(entity.getTotalSlots() - entity.getBookedSlots())
                .status(entity.getStatus())
                .build();
    }

    public void updateEntity(SportSpaceAvailabilityRequest request, SportSpaceAvailability availability) {
        if (request == null || availability == null) return;

        availability.setAvailableFrom(request.getAvailableFrom());
        availability.setAvailableTo(request.getAvailableTo());
        availability.setTotalSlots(request.getTotalSlots());
    }
}