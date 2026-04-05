package tn.esprit._4se2.pi.services.HealthMetrics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.dto.HealthMetrics.HealthMetricsRequest;
import tn.esprit._4se2.pi.dto.HealthMetrics.HealthMetricsResponse;
import tn.esprit._4se2.pi.entities.HealthMetrics;
import tn.esprit._4se2.pi.entities.HealthProfile;
import tn.esprit._4se2.pi.mappers.HealthMetricsMapper;
import tn.esprit._4se2.pi.repositories.HealthMetricsRepository;
import tn.esprit._4se2.pi.repositories.HealthProfileRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class HealthMetricsService implements IHealthMetricsService {

    private final HealthMetricsRepository healthMetricsRepository;
    private final HealthProfileRepository healthProfileRepository;
    private final HealthMetricsMapper healthMetricsMapper;

    @Override
    public HealthMetricsResponse createHealthMetrics(HealthMetricsRequest request) {
        log.info("Creating health metrics for health profile id: {}", request.getHealthProfileId());

        HealthProfile healthProfile = healthProfileRepository.findById(request.getHealthProfileId())
                .orElseThrow(() -> new RuntimeException("Health profile not found with id: " + request.getHealthProfileId()));

        HealthMetrics metrics = healthMetricsMapper.toEntity(request);
        metrics.setHealthProfile(healthProfile);

        // Set measuredAt to current time if not provided
        if (metrics.getMeasuredAt() == null) {
            metrics.setMeasuredAt(LocalDateTime.now());
        }

        HealthMetrics saved = healthMetricsRepository.save(metrics);
        log.info("Health metrics created with id: {}", saved.getId());

        return healthMetricsMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public HealthMetricsResponse getHealthMetricsById(Long id) {
        log.info("Fetching health metrics with id: {}", id);
        return healthMetricsRepository.findById(id)
                .map(healthMetricsMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Health metrics not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HealthMetricsResponse> getAllHealthMetrics() {
        log.info("Fetching all health metrics");
        return healthMetricsRepository.findAll()
                .stream()
                .map(healthMetricsMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HealthMetricsResponse> getHealthMetricsByHealthProfileId(Long healthProfileId) {
        log.info("Fetching health metrics for health profile id: {}", healthProfileId);
        return healthMetricsRepository.findByHealthProfileId(healthProfileId)
                .stream()
                .map(healthMetricsMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HealthMetricsResponse> getHealthMetricsByDateRange(Long healthProfileId, LocalDateTime start, LocalDateTime end) {
        log.info("Fetching health metrics for health profile {} between {} and {}", healthProfileId, start, end);
        return healthMetricsRepository.findByHealthProfileIdAndMeasuredAtBetween(healthProfileId, start, end)
                .stream()
                .map(healthMetricsMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public HealthMetricsResponse updateHealthMetrics(Long id, HealthMetricsRequest request) {
        log.info("Updating health metrics with id: {}", id);

        HealthMetrics existing = healthMetricsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Health metrics not found with id: " + id));

        // If health profile changed, fetch and set
        if (!existing.getHealthProfile().getId().equals(request.getHealthProfileId())) {
            HealthProfile newHealthProfile = healthProfileRepository.findById(request.getHealthProfileId())
                    .orElseThrow(() -> new RuntimeException("Health profile not found with id: " + request.getHealthProfileId()));
            existing.setHealthProfile(newHealthProfile);
        }

        healthMetricsMapper.updateEntity(request, existing);
        HealthMetrics updated = healthMetricsRepository.save(existing);
        log.info("Health metrics updated with id: {}", id);

        return healthMetricsMapper.toResponse(updated);
    }

    @Override
    public void deleteHealthMetrics(Long id) {
        log.info("Deleting health metrics with id: {}", id);
        if (!healthMetricsRepository.existsById(id)) {
            throw new RuntimeException("Health metrics not found with id: " + id);
        }
        healthMetricsRepository.deleteById(id);
        log.info("Health metrics deleted with id: {}", id);
    }
}