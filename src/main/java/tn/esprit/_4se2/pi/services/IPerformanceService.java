package tn.esprit._4se2.pi.services;

import tn.esprit._4se2.pi.dto.PerformanceRequest;
import tn.esprit._4se2.pi.dto.PerformanceResponse;
import java.util.List;

public interface IPerformanceService {
    PerformanceResponse createPerformance(PerformanceRequest request);
    List<PerformanceResponse> getAllPerformances();
    PerformanceResponse getPerformanceById(Long id);
    PerformanceResponse updatePerformance(Long id, PerformanceRequest request);
    void deletePerformance(Long id);
}