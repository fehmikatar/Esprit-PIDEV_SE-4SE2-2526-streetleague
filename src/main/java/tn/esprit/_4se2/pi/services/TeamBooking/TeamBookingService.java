package tn.esprit._4se2.pi.services.TeamBooking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.dto.TeamBookingRequest;
import tn.esprit._4se2.pi.dto.TeamBookingResponse;
import tn.esprit._4se2.pi.entities.TeamBooking;
import tn.esprit._4se2.pi.mappers.TeamBookingMapper;
import tn.esprit._4se2.pi.repositories.TeamBookingRepository;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TeamBookingService implements ITeamBookingService {

    private final TeamBookingRepository teamBookingRepository;
    private final TeamBookingMapper teamBookingMapper;

    @Override
    public TeamBookingResponse createTeamBooking(TeamBookingRequest request) {
        log.info("Creating team booking for team: {} and sport space: {}", request.getTeamId(), request.getSportSpaceId());

        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new RuntimeException("End time must be after start time");
        }

        TeamBooking teamBooking = teamBookingMapper.toEntity(request);
        teamBooking.setTeamId(request.getTeamId());
        teamBooking.setSportSpaceId(request.getSportSpaceId());

        TeamBooking savedTeamBooking = teamBookingRepository.save(teamBooking);
        log.info("Team booking created successfully with id: {}", savedTeamBooking.getId());

        return teamBookingMapper.toResponse(savedTeamBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public TeamBookingResponse getTeamBookingById(Long id) {
        log.info("Fetching team booking with id: {}", id);
        return teamBookingRepository.findById(id)
                .map(teamBookingMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Team booking not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamBookingResponse> getAllTeamBookings() {
        log.info("Fetching all team bookings");
        return teamBookingRepository.findAll()
                .stream()
                .map(teamBookingMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamBookingResponse> getTeamBookingsByTeamId(Long teamId) {
        log.info("Fetching team bookings for team: {}", teamId);
        return teamBookingRepository.findByTeamId(teamId)
                .stream()
                .map(teamBookingMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamBookingResponse> getTeamBookingsBySportSpaceId(Long sportSpaceId) {
        log.info("Fetching team bookings for sport space: {}", sportSpaceId);
        return teamBookingRepository.findBySportSpaceId(sportSpaceId)
                .stream()
                .map(teamBookingMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamBookingResponse> getTeamBookingsByStatus(String status) {
        log.info("Fetching team bookings with status: {}", status);
        return teamBookingRepository.findByStatus(status)
                .stream()
                .map(teamBookingMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TeamBookingResponse updateTeamBooking(Long id, TeamBookingRequest request) {
        log.info("Updating team booking with id: {}", id);

        TeamBooking teamBooking = teamBookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team booking not found with id: " + id));

        teamBookingMapper.updateEntity(request, teamBooking);
        TeamBooking updatedTeamBooking = teamBookingRepository.save(teamBooking);
        log.info("Team booking updated successfully with id: {}", id);

        return teamBookingMapper.toResponse(updatedTeamBooking);
    }

    @Override
    public void deleteTeamBooking(Long id) {
        log.info("Deleting team booking with id: {}", id);

        if (!teamBookingRepository.existsById(id)) {
            throw new RuntimeException("Team booking not found with id: " + id);
        }

        teamBookingRepository.deleteById(id);
        log.info("Team booking deleted successfully with id: {}", id);
    }

    @Override
    public void confirmTeamBooking(Long id) {
        log.info("Confirming team booking with id: {}", id);

        TeamBooking teamBooking = teamBookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team booking not found with id: " + id));

        teamBooking.setStatus("CONFIRMED");
        teamBookingRepository.save(teamBooking);
        log.info("Team booking confirmed successfully with id: {}", id);
    }
}