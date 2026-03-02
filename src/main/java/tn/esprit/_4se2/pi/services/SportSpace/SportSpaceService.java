package tn.esprit._4se2.pi.services.SportSpace;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.dto.SportSpaceRequest;
import tn.esprit._4se2.pi.dto.SportSpaceResponse;
import tn.esprit._4se2.pi.entities.SportSpace;
import tn.esprit._4se2.pi.mappers.SportSpaceMapper;
import tn.esprit._4se2.pi.repositories.SportSpaceRepository;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SportSpaceService implements ISportSpaceService {

    private final SportSpaceRepository sportSpaceRepository;
    private final SportSpaceMapper sportSpaceMapper;

    @Override
    public SportSpaceResponse createSportSpace(SportSpaceRequest request) {
        log.info("Creating sport space: {}", request.getName());

        SportSpace sportSpace = sportSpaceMapper.toEntity(request);
        sportSpace.setFieldOwnerId(request.getFieldOwnerId());

        SportSpace savedSportSpace = sportSpaceRepository.save(sportSpace);
        log.info("Sport space created successfully with id: {}", savedSportSpace.getId());

        return sportSpaceMapper.toResponse(savedSportSpace);
    }

    @Override
    @Transactional(readOnly = true)
    public SportSpaceResponse getSportSpaceById(Long id) {
        log.info("Fetching sport space with id: {}", id);
        return sportSpaceRepository.findById(id)
                .map(sportSpaceMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Sport space not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SportSpaceResponse> getAllSportSpaces() {
        log.info("Fetching all sport spaces");
        return sportSpaceRepository.findAll()
                .stream()
                .map(sportSpaceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SportSpaceResponse> getSportSpacesByFieldOwnerId(Long fieldOwnerId) {
        log.info("Fetching sport spaces for field owner: {}", fieldOwnerId);
        return sportSpaceRepository.findByFieldOwnerId(fieldOwnerId)
                .stream()
                .map(sportSpaceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SportSpaceResponse> getSportSpacesBySportType(String sportType) {
        log.info("Fetching sport spaces by type: {}", sportType);
        return sportSpaceRepository.findBySportType(sportType)
                .stream()
                .map(sportSpaceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SportSpaceResponse> getAvailableSportSpaces() {
        log.info("Fetching available sport spaces");
        return sportSpaceRepository.findByIsAvailableTrue()
                .stream()
                .map(sportSpaceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SportSpaceResponse> searchSportSpacesByLocation(String location) {
        log.info("Searching sport spaces by location: {}", location);
        return sportSpaceRepository.findByLocationContainingIgnoreCase(location)
                .stream()
                .map(sportSpaceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SportSpaceResponse updateSportSpace(Long id, SportSpaceRequest request) {
        log.info("Updating sport space with id: {}", id);

        SportSpace sportSpace = sportSpaceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sport space not found with id: " + id));

        sportSpaceMapper.updateEntity(request, sportSpace);
        SportSpace updatedSportSpace = sportSpaceRepository.save(sportSpace);
        log.info("Sport space updated successfully with id: {}", id);

        return sportSpaceMapper.toResponse(updatedSportSpace);
    }

    @Override
    public void deleteSportSpace(Long id) {
        log.info("Deleting sport space with id: {}", id);

        if (!sportSpaceRepository.existsById(id)) {
            throw new RuntimeException("Sport space not found with id: " + id);
        }

        sportSpaceRepository.deleteById(id);
        log.info("Sport space deleted successfully with id: {}", id);
    }
}