package tn.esprit._4se2.pi.services.SportSpace;

import tn.esprit._4se2.pi.dto.SportSpaceRequest;
import tn.esprit._4se2.pi.dto.SportSpaceResponse;
import java.util.List;

public interface ISportSpaceService {
    SportSpaceResponse createSportSpace(SportSpaceRequest request);
    SportSpaceResponse getSportSpaceById(Long id);
    List<SportSpaceResponse> getAllSportSpaces();
    List<SportSpaceResponse> getSportSpacesByFieldOwnerId(Long fieldOwnerId);
    List<SportSpaceResponse> getSportSpacesBySportType(String sportType);
    List<SportSpaceResponse> getAvailableSportSpaces();
    List<SportSpaceResponse> searchSportSpacesByLocation(String location);
    SportSpaceResponse updateSportSpace(Long id, SportSpaceRequest request);
    void deleteSportSpace(Long id);
}