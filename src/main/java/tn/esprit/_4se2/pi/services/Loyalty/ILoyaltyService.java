package tn.esprit._4se2.pi.services.Loyalty;

import tn.esprit._4se2.pi.dto.Loyalty.*;
import java.util.List;

public interface ILoyaltyService {

    // Program
    LoyaltyProgramResponse createProgram(LoyaltyProgramRequest request);
    LoyaltyProgramResponse updateProgram(Long id, LoyaltyProgramRequest request);
    void deleteProgram(Long id);
    List<LoyaltyProgramResponse> getAllPrograms();
    LoyaltyProgramResponse getProgramById(Long id);

    // Tiers
    LoyaltyTierResponse createTier(LoyaltyTierRequest request);
    LoyaltyTierResponse updateTier(Long id, LoyaltyTierRequest request);
    void deleteTier(Long id);
    List<LoyaltyTierResponse> getAllTiersByProgram(Long programId);
    LoyaltyTierResponse getTierById(Long id);

    // Clients
    LoyaltyClientResponse enrollUser(EnrollRequest request);
    LoyaltyClientResponse getUserLoyalty(Long userId);
    List<LoyaltyClientResponse> getAllClients();

    // Points & transactions
    void addPoints(AddPointsRequest request);
    void redeemPoints(Long userId, Integer points, String reason);
    List<LoyaltyTransactionResponse> getUserTransactions(Long userId);
    void updateUserTier(Long userId);  // recalcule et met à jour le tier
}