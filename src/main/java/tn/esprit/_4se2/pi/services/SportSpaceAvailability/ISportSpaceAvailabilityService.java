package tn.esprit._4se2.pi.services.SportSpaceAvailability;

import tn.esprit._4se2.pi.dto.SportSpaceAvailabilityRequest;
import tn.esprit._4se2.pi.dto.SportSpaceAvailabilityResponse;
import java.util.List;

public interface ISportSpaceAvailabilityService {
    SportSpaceAvailabilityResponse createAvailability(SportSpaceAvailabilityRequest request);
    SportSpaceAvailabilityResponse getAvailabilityById(Long id);
    List<SportSpaceAvailabilityResponse> getAllAvailabilities();
    List<SportSpaceAvailabilityResponse> getAvailabilitiesBySportSpaceId(Long sportSpaceId);
    SportSpaceAvailabilityResponse updateAvailability(Long id, SportSpaceAvailabilityRequest request);
    void deleteAvailability(Long id);
}