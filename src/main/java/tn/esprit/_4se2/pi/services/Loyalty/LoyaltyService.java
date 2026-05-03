package tn.esprit._4se2.pi.services.Loyalty;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.dto.Loyalty.*;
import tn.esprit._4se2.pi.entities.*;
import tn.esprit._4se2.pi.exception.DuplicateResourceException;
import tn.esprit._4se2.pi.exception.ResourceNotFoundException;
import tn.esprit._4se2.pi.mappers.*;
import tn.esprit._4se2.pi.repositories.*;
import tn.esprit._4se2.pi.services.User.UserService; // hypothétique

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LoyaltyService implements ILoyaltyService {

    private final LoyaltyProgramRepository programRepository;
    private final LoyaltyTierRepository tierRepository;
    private final LoyaltyClientRepository clientRepository;
    private final LoyaltyTransactionRepository transactionRepository;
    private final LoyaltyProgramMapper programMapper;
    private final LoyaltyTierMapper tierMapper;
    private final LoyaltyClientMapper clientMapper;
    private final LoyaltyTransactionMapper transactionMapper;
    private final UserService userService; // pour valider l'utilisateur

    // ---------- Program ----------
    @Override
    public LoyaltyProgramResponse createProgram(LoyaltyProgramRequest request) {
        log.info("Creating loyalty program: {}", request.getName());
        LoyaltyProgram program = programMapper.toEntity(request);
        return programMapper.toResponse(programRepository.save(program));
    }

    @Override
    public LoyaltyProgramResponse updateProgram(Long id, LoyaltyProgramRequest request) {
        LoyaltyProgram program = programRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with id: " + id));
        program.setName(request.getName());
        program.setDescription(request.getDescription());
        program.setPointsPerCurrencyUnit(request.getPointsPerCurrencyUnit());
        return programMapper.toResponse(programRepository.save(program));
    }

    @Override
    public void deleteProgram(Long id) {
        if (!programRepository.existsById(id))
            throw new ResourceNotFoundException("Program not found with id: " + id);
        programRepository.deleteById(id);
    }

    @Override
    public List<LoyaltyProgramResponse> getAllPrograms() {
        return programRepository.findAll().stream()
                .map(programMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public LoyaltyProgramResponse getProgramById(Long id) {
        return programRepository.findById(id)
                .map(programMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with id: " + id));
    }

    // ---------- Tiers ----------
    @Override
    public LoyaltyTierResponse createTier(LoyaltyTierRequest request) {
        log.info("Creating tier: {} for program {}", request.getName(), request.getProgramId());
        LoyaltyTier tier = tierMapper.toEntity(request);
        return tierMapper.toResponse(tierRepository.save(tier));
    }

    @Override
    public LoyaltyTierResponse updateTier(Long id, LoyaltyTierRequest request) {
        LoyaltyTier tier = tierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tier not found with id: " + id));
        tierMapper.updateEntity(request, tier);
        return tierMapper.toResponse(tierRepository.save(tier));
    }

    @Override
    public void deleteTier(Long id) {
        if (!tierRepository.existsById(id))
            throw new ResourceNotFoundException("Tier not found with id: " + id);
        tierRepository.deleteById(id);
    }

    @Override
    public List<LoyaltyTierResponse> getAllTiersByProgram(Long programId) {
        return tierRepository.findByProgramIdOrderByPointsRequiredAsc(programId).stream()
                .map(tierMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public LoyaltyTierResponse getTierById(Long id) {
        return tierRepository.findById(id)
                .map(tierMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Tier not found with id: " + id));
    }

    // ---------- Clients ----------
    @Override
    public LoyaltyClientResponse enrollUser(EnrollRequest request) {
        // Vérifier que l'utilisateur existe
        userService.getUserById(request.getUserId()); // à implémenter

        // Vérifier qu'il n'est pas déjà inscrit
        if (clientRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new DuplicateResourceException("User already enrolled in a loyalty program");
        }

        LoyaltyProgram program = programRepository.findById(request.getProgramId())
                .orElseThrow(() -> new ResourceNotFoundException("Program not found"));

        // Récupérer le tier par défaut (le plus bas)
        LoyaltyTier defaultTier = tierRepository.findByProgramIdOrderByPointsRequiredAsc(request.getProgramId())
                .stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No tier defined for this program"));

        User user = new User(); // à remplacer par la vraie récupération
        user.setId(request.getUserId());

        LoyaltyClient client = LoyaltyClient.builder()
                .user(user)
                .program(program)
                .currentTier(defaultTier)
                .totalPoints(0)
                .build();
        return clientMapper.toResponse(clientRepository.save(client));
    }

    @Override
    public LoyaltyClientResponse getUserLoyalty(Long userId) {
        LoyaltyClient client = clientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not enrolled in loyalty program"));
        return clientMapper.toResponse(client);
    }

    @Override
    public List<LoyaltyClientResponse> getAllClients() {
        return clientRepository.findAll().stream()
                .map(clientMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ---------- Points & Transactions ----------
    @Override
    public void addPoints(AddPointsRequest request) {
        LoyaltyClient client = clientRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not enrolled"));

        client.setTotalPoints(client.getTotalPoints() + request.getPoints());
        clientRepository.save(client);

        LoyaltyTransaction tx = LoyaltyTransaction.builder()
                .client(client)
                .points(request.getPoints())
                .reason(request.getReason())
                .relatedEntityType(request.getRelatedEntityType())
                .relatedEntityId(request.getRelatedEntityId())
                .build();
        transactionRepository.save(tx);

        // Recalcul automatique du tier
        updateUserTier(request.getUserId());
    }

    @Override
    public void redeemPoints(Long userId, Integer points, String reason) {
        LoyaltyClient client = clientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not enrolled"));
        if (client.getTotalPoints() < points) {
            throw new IllegalArgumentException("Insufficient points");
        }
        client.setTotalPoints(client.getTotalPoints() - points);
        clientRepository.save(client);

        LoyaltyTransaction tx = LoyaltyTransaction.builder()
                .client(client)
                .points(-points)
                .reason("Redeem: " + reason)
                .build();
        transactionRepository.save(tx);

        updateUserTier(userId);
    }

    @Override
    public List<LoyaltyTransactionResponse> getUserTransactions(Long userId) {
        LoyaltyClient client = clientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not enrolled"));
        return transactionRepository.findByClientIdOrderByTransactionDateDesc(client.getId())
                .stream().map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void updateUserTier(Long userId) {
        LoyaltyClient client = clientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not enrolled"));
        Integer currentPoints = client.getTotalPoints();

        // Trouver le tier le plus élevé que le client peut atteindre
        var optionalTier = tierRepository.findByProgramIdAndPointsRequiredLessThanEqualOrderByPointsRequiredDesc(
                client.getProgram().getId(), currentPoints);
        if (optionalTier.isPresent()) {
            LoyaltyTier newTier = optionalTier.get();
            if (!newTier.equals(client.getCurrentTier())) {
                client.setCurrentTier(newTier);
                clientRepository.save(client);
                log.info("User {} upgraded to tier {}", userId, newTier.getName());
            }
        }
    }
}