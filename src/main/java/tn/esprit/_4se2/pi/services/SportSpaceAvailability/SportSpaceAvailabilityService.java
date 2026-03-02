package tn.esprit._4se2.pi.services.SportSpaceAvailability;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.dto.SportSpaceAvailabilityRequest;
import tn.esprit._4se2.pi.dto.SportSpaceAvailabilityResponse;
import tn.esprit._4se2.pi.entities.SportSpaceAvailability;
import tn.esprit._4se2.pi.mappers.SportSpaceAvailabilityMapper;
import tn.esprit._4se2.pi.repositories.SportSpaceAvailabilityRepository;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SportSpaceAvailabilityService implements ISportSpaceAvailabilityService {

    private final SportSpaceAvailabilityRepository availabilityRepository;
    private final SportSpaceAvailabilityMapper availabilityMapper;

    @Override
    public SportSpaceAvailabilityResponse createAvailability(SportSpaceAvailabilityRequest request) {
        log.info("Creating availability for sport space: {}", request.getSportSpaceId());

        if (request.getAvailableTo().isBefore(request.getAvailableFrom())) {
            throw new RuntimeException("Available to must be after available from");
        }

        SportSpaceAvailability availability = availabilityMapper.toEntity(request);
        availability.setSportSpaceId(request.getSportSpaceId());

        SportSpaceAvailability savedAvailability = availabilityRepository.save(availability);
        log.info("Availability created successfully with id: {}", savedAvailability.getId());

        return availabilityMapper.toResponse(savedAvailability);
    }

    @Override
    @Transactional(readOnly = true)
    public SportSpaceAvailabilityResponse getAvailabilityById(Long id) {
        log.info("Fetching availability with id: {}", id);
        return availabilityRepository.findById(id)
                .map(availabilityMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Availability not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SportSpaceAvailabilityResponse> getAllAvailabilities() {
        log.info("Fetching all availabilities");
        return availabilityRepository.findAll()
                .stream()
                .map(availabilityMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SportSpaceAvailabilityResponse> getAvailabilitiesBySportSpaceId(Long sportSpaceId) {
        log.info("Fetching availabilities for sport space: {}", sportSpaceId);
        return availabilityRepository.findBySportSpaceId(sportSpaceId)
                .stream()
                .map(availabilityMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SportSpaceAvailabilityResponse updateAvailability(Long id, SportSpaceAvailabilityRequest request) {
        log.info("Updating availability with id: {}", id);

        SportSpaceAvailability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Availability not found with id: " + id));

        availabilityMapper.updateEntity(request, availability);
        SportSpaceAvailability updatedAvailability = availabilityRepository.save(availability);
        log.info("Availability updated successfully with id: {}", id);

        return availabilityMapper.toResponse(updatedAvailability);
    }

    @Override
    public void deleteAvailability(Long id) {
        log.info("Deleting availability with id: {}", id);

        if (!availabilityRepository.existsById(id)) {
            throw new RuntimeException("Availability not found with id: " + id);
        }

        availabilityRepository.deleteById(id);
        log.info("Availability deleted successfully with id: {}", id);
    }
}