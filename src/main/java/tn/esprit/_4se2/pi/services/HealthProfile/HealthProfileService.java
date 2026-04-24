package tn.esprit._4se2.pi.services.HealthProfile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.dto.HealthProfile.HealthProfileRequest;
import tn.esprit._4se2.pi.dto.HealthProfile.HealthProfileResponse;
import tn.esprit._4se2.pi.entities.HealthProfile;
import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.mappers.HealthProfileMapper;
import tn.esprit._4se2.pi.repositories.HealthProfileRepository;
import tn.esprit._4se2.pi.repositories.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class HealthProfileService implements IHealthProfileService {

    private final HealthProfileRepository healthProfileRepository;
    private final UserRepository userRepository;
    private final HealthProfileMapper healthProfileMapper;

    @Override
    public HealthProfileResponse createHealthProfile(HealthProfileRequest request) {
        log.info("Creating health profile for user id: {}", request.getUserId());

        // Check if user exists
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

        // Check if user already has a health profile
        if (healthProfileRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new RuntimeException("User already has a health profile");
        }

        HealthProfile profile = healthProfileMapper.toEntity(request);
        profile.setUser(user);

        HealthProfile saved = healthProfileRepository.save(profile);
        log.info("Health profile created with id: {}", saved.getId());

        return healthProfileMapper.toResponse(saved);
    }
    @Override
    public List<HealthProfileResponse> getAllHealthProfiles() {
        return healthProfileRepository.findAll()
                .stream()
                .map(healthProfileMapper::toResponse)   // ✅ utilisation du mapper
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public HealthProfileResponse getHealthProfileById(Long id) {
        log.info("Fetching health profile with id: {}", id);
        return healthProfileRepository.findById(id)
                .map(healthProfileMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Health profile not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public HealthProfileResponse getHealthProfileByUserId(Long userId) {
        log.info("Fetching health profile for user id: {}", userId);
        return healthProfileRepository.findByUserId(userId)
                .map(healthProfileMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Health profile not found for user id: " + userId));
    }

    @Override
    public HealthProfileResponse updateHealthProfile(Long id, HealthProfileRequest request) {
        log.info("Updating health profile with id: {}", id);

        HealthProfile existing = healthProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Health profile not found with id: " + id));

        // If user changed, verify and update
        if (!existing.getUser().getId().equals(request.getUserId())) {
            User newUser = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));
            // Check that new user doesn't already have a profile (optional)
            if (healthProfileRepository.findByUserId(request.getUserId()).isPresent() &&
                    !healthProfileRepository.findByUserId(request.getUserId()).get().getId().equals(id)) {
                throw new RuntimeException("Another health profile already exists for user " + request.getUserId());
            }
            existing.setUser(newUser);
        }

        healthProfileMapper.updateEntity(request, existing);
        HealthProfile updated = healthProfileRepository.save(existing);
        log.info("Health profile updated with id: {}", id);

        return healthProfileMapper.toResponse(updated);
    }

    @Override
    public void deleteHealthProfile(Long id) {
        log.info("Deleting health profile with id: {}", id);
        if (!healthProfileRepository.existsById(id)) {
            throw new RuntimeException("Health profile not found with id: " + id);
        }
        healthProfileRepository.deleteById(id);
        log.info("Health profile deleted with id: {}", id);
    }
}