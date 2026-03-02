package tn.esprit._4se2.pi.mappers;

import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.dto.TeamBookingRequest;
import tn.esprit._4se2.pi.dto.TeamBookingResponse;
import tn.esprit._4se2.pi.entities.TeamBooking;
import java.time.LocalDateTime;

@Component
public class TeamBookingMapper {

    public TeamBooking toEntity(TeamBookingRequest request) {
        if (request == null) return null;

        TeamBooking teamBooking = new TeamBooking();
        teamBooking.setBookingDate(LocalDateTime.now());
        teamBooking.setStartTime(request.getStartTime());
        teamBooking.setEndTime(request.getEndTime());
        teamBooking.setPlayerCount(request.getPlayerCount());
        teamBooking.setStatus("PENDING");
        teamBooking.setCreatedAt(LocalDateTime.now());
        return teamBooking;
    }

    public TeamBookingResponse toResponse(TeamBooking entity) {
        if (entity == null) return null;

        return TeamBookingResponse.builder()
                .id(entity.getId())
                .teamId(entity.getTeamId())
                .sportSpaceId(entity.getSportSpaceId())
                .bookingDate(entity.getBookingDate())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .playerCount(entity.getPlayerCount())
                .totalPrice(entity.getTotalPrice())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public void updateEntity(TeamBookingRequest request, TeamBooking teamBooking) {
        if (request == null || teamBooking == null) return;

        teamBooking.setStartTime(request.getStartTime());
        teamBooking.setEndTime(request.getEndTime());
        teamBooking.setPlayerCount(request.getPlayerCount());
    }
}