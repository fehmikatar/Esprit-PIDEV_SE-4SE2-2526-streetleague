package tn.esprit._4se2.pi.services.SportSpace;

import tn.esprit._4se2.pi.dto.SportSpace.SportSpaceRequest;
import tn.esprit._4se2.pi.dto.SportSpace.SportSpaceResponse;
import tn.esprit._4se2.pi.dto.SportSpace.SportspacestatsResponse;

import java.util.List;

public interface ISportSpaceService {
    SportSpaceResponse createSportSpace(SportSpaceRequest request);

    SportSpaceResponse getSportSpaceById(Long id);

    List<SportSpaceResponse> getAllSportSpaces();

    List<SportSpaceResponse> getSportSpacesByFieldOwnerId(Long fieldOwnerId);

    List<SportSpaceResponse> getSportSpacesByFieldOwnerEmail(String ownerEmail);

    List<SportSpaceResponse> getSportSpacesBySportType(String sportType);

    List<SportSpaceResponse> getAvailableSportSpaces();

    List<SportSpaceResponse> searchSportSpacesByLocation(String location);

    SportSpaceResponse updateSportSpace(Long id, SportSpaceRequest request);

    void deleteSportSpace(Long id);

    // Tâche 2 : résultats JPQL avec stats (JOIN feedbacks + bookings)
    List<SportspacestatsResponse> getAvailableSpacesWithStats();
}
