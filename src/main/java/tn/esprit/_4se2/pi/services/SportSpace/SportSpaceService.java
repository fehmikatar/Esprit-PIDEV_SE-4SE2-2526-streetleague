package tn.esprit._4se2.pi.services.SportSpace;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.dto.SportSpace.SportSpaceRequest;
import tn.esprit._4se2.pi.dto.SportSpace.SportSpaceResponse;
import tn.esprit._4se2.pi.dto.SportSpace.SportspacestatsResponse;
import tn.esprit._4se2.pi.dto.SportSpace.SportspacestatsResponse;
import tn.esprit._4se2.pi.entities.Feedback;
import tn.esprit._4se2.pi.entities.SportSpace;
import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.mappers.SportSpaceMapper;
import tn.esprit._4se2.pi.repositories.BookingRepository;
import tn.esprit._4se2.pi.repositories.FeedbackRepository;
import tn.esprit._4se2.pi.repositories.SportSpaceRepository;
import tn.esprit._4se2.pi.repositories.UserRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SportSpaceService implements ISportSpaceService {

    private final SportSpaceRepository sportSpaceRepository;
    private final SportSpaceMapper sportSpaceMapper;
    private final FeedbackRepository feedbackRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    // ── Méthodes existantes (inchangées) ──────────────────────────────────────

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
                .map(this::mapWithStats)
                .orElseThrow(() -> new RuntimeException("Sport space not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SportSpaceResponse> getAllSportSpaces() {
        log.info("Fetching all sport spaces");
        return sportSpaceRepository.findAll()
                .stream()
                .map(this::mapWithStats)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SportSpaceResponse> getSportSpacesByFieldOwnerId(Long fieldOwnerId) {
        log.info("Fetching sport spaces for field owner: {}", fieldOwnerId);
        return sportSpaceRepository.findByFieldOwnerId(fieldOwnerId)
                .stream()
                .map(this::mapWithStats)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SportSpaceResponse> getSportSpacesByFieldOwnerEmail(String ownerEmail) {
        Long ownerId = userRepository.findByEmail(ownerEmail)
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("Owner not found with email: " + ownerEmail));

        return getSportSpacesByFieldOwnerId(ownerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SportSpaceResponse> getSportSpacesBySportType(String sportType) {
        log.info("Fetching sport spaces by type: {}", sportType);
        return sportSpaceRepository.findBySportType(sportType)
                .stream()
                .map(this::mapWithStats)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SportSpaceResponse> getAvailableSportSpaces() {
        log.info("Fetching available sport spaces");
        return sportSpaceRepository.findByIsAvailableTrue()
                .stream()
                .map(this::mapWithStats)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SportSpaceResponse> searchSportSpacesByLocation(String location) {
        log.info("Searching sport spaces by location: {}", location);
        return sportSpaceRepository.findByLocationContainingIgnoreCase(location)
                .stream()
                .map(this::mapWithStats)
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
        return mapWithStats(updatedSportSpace);
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

    // ── TÂCHE 2 : méthode JPQL avec JOIN ──────────────────────────────────────

    /**
     * Appelle la requête JPQL findAvailableSpacesWithStats() du repository.
     * Chaque Object[] retourné par la requête est structuré ainsi :
     * [0] = SportSpace entity
     * [1] = Double → moyenne des ratings (COALESCE = 0.0 si aucun feedback)
     * [2] = Long → nombre de feedbacks APPROVED
     * [3] = Long → nombre de réservations non annulées
     *
     * On cast chaque élément et on construit un SportSpaceStatsResponse.
     * @Transactional(readOnly=true) : Hibernate ne flush pas → optimisation
     * lecture.
     */
    @Override
    @Transactional(readOnly = true)
    public List<SportspacestatsResponse> getAvailableSpacesWithStats() {
        log.info("Fetching available sport spaces with JPQL stats");

        return sportSpaceRepository.findAvailableSpacesWithStats()
                .stream()
                .map(row -> {
                    SportSpace s = (SportSpace) row[0];
                    Double avgRating = (Double) row[1];
                    Long feedbacks = (Long) row[2];
                    Long bookings = (Long) row[3];

                    return SportspacestatsResponse.builder()
                            .id(s.getId())
                            .name(s.getName())
                            .sportType(s.getSportType())
                            .location(s.getLocation())
                            .hourlyRate(s.getHourlyRate())
                            .isAvailable(s.getIsAvailable())
                            .averageRating(avgRating)
                            .feedbackCount(feedbacks)
                            .bookingCount(bookings)
                            .build();
                })
                .collect(Collectors.toList());
    }

    // ── TÂCHE 3 : méthode keyword query ───────────────────────────────────────

    /**
     * Utilise la derived query :
     * findBySportTypeAndIsAvailableTrueAndHourlyRateLessThanEqual
     * Spring Data génère automatiquement :
     * WHERE sport_type = :sportType AND is_available = 1 AND hourly_rate <=
     * :maxHourlyRate
     *
     * On mappe ensuite chaque SportSpace en SportSpaceResponse via mapWithStats
     * pour inclure les infos de stats (rating, bookings) dans la réponse.
     */

    @Transactional(readOnly = true)
    public List<SportSpaceResponse> filterBySportTypeAndMaxRate(String sportType, BigDecimal maxHourlyRate) {
        log.info("Filtering sport spaces by type={} and maxRate={}", sportType, maxHourlyRate);

        return sportSpaceRepository
                .findBySportTypeAndIsAvailableTrueAndHourlyRateLessThanEqual(sportType, maxHourlyRate)
                .stream()
                .map(this::mapWithStats)
                .collect(Collectors.toList());
    }

    // ── Méthode utilitaire existante ───────────────────────────────────────────

    private SportSpaceResponse mapWithStats(SportSpace sportSpace) {
        SportSpaceResponse response = sportSpaceMapper.toResponse(sportSpace);

        List<Feedback> approvedFeedbacks = feedbackRepository
                .findBySportSpaceIdAndStatus(sportSpace.getId(), "APPROVED");

        double averageRating = approvedFeedbacks.stream()
                .mapToInt(Feedback::getRating)
                .average()
                .orElse(0.0);

        int totalBookings = (int) bookingRepository.findBySportSpaceId(sportSpace.getId()).stream()
                .filter(b -> !"CANCELLED".equalsIgnoreCase(b.getStatus()))
                .count();

        response.setAverageRating(approvedFeedbacks.isEmpty() ? null : averageRating);
        response.setReviewCount((long) approvedFeedbacks.size());
        response.setTotalBookings(totalBookings);

        return response;
    }
}
