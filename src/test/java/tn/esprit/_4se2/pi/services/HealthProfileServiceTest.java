package tn.esprit._4se2.pi.services;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit._4se2.pi.dto.HealthProfile.HealthProfileRequest;
import tn.esprit._4se2.pi.dto.HealthProfile.HealthProfileResponse;
import tn.esprit._4se2.pi.entities.HealthProfile;
import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.mappers.HealthProfileMapper;
import tn.esprit._4se2.pi.repositories.HealthProfileRepository;
import tn.esprit._4se2.pi.repositories.UserRepository;
import tn.esprit._4se2.pi.services.HealthProfile.HealthProfileService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
class HealthProfileServiceTest {

    @Mock private HealthProfileRepository healthProfileRepository;
    @Mock private UserRepository userRepository;
    @Mock private HealthProfileMapper healthProfileMapper;
    @InjectMocks private HealthProfileService healthProfileService;

    private User testUser;
    private HealthProfile testProfile;
    private HealthProfileRequest validRequest;
    private HealthProfileResponse expectedResponse;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("Alice");

        testProfile = new HealthProfile();
        testProfile.setId(100L);
        testProfile.setUser(testUser);
        testProfile.setWeight(70.0);
        testProfile.setHeight(175.0);
        testProfile.setAge(25);

        validRequest = new HealthProfileRequest();
        validRequest.setUserId(1L);
        validRequest.setWeight(70.0);
        validRequest.setHeight(175.0);
        validRequest.setAge(25);

        expectedResponse = new HealthProfileResponse();
        expectedResponse.setId(100L);
        expectedResponse.setUserId(1L);
        expectedResponse.setWeight(70.0);
    }

    @Test
    void createHealthProfile_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(healthProfileRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(healthProfileMapper.toEntity(validRequest)).thenReturn(testProfile);
        when(healthProfileRepository.save(any(HealthProfile.class))).thenReturn(testProfile);
        when(healthProfileMapper.toResponse(testProfile)).thenReturn(expectedResponse);

        HealthProfileResponse response = healthProfileService.createHealthProfile(validRequest);
        assertThat(response.getId()).isEqualTo(100L);
    }

    @Test
    void createHealthProfile_UserAlreadyHasProfile_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(healthProfileRepository.findByUserId(1L)).thenReturn(Optional.of(testProfile));

        assertThatThrownBy(() -> healthProfileService.createHealthProfile(validRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("already has a health profile");
    }

    @Test
    void getHealthProfileByUserId_ReturnsProfile() {
        when(healthProfileRepository.findByUserId(1L)).thenReturn(Optional.of(testProfile));
        when(healthProfileMapper.toResponse(testProfile)).thenReturn(expectedResponse);

        HealthProfileResponse response = healthProfileService.getHealthProfileByUserId(1L);
        assertThat(response.getId()).isEqualTo(100L);
    }

    @Test
    void getHealthProfileByUserId_NotFound_ThrowsException() {
        when(healthProfileRepository.findByUserId(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> healthProfileService.getHealthProfileByUserId(999L))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void getAllHealthProfiles_ReturnsList() {
        when(healthProfileRepository.findAll()).thenReturn(java.util.List.of(testProfile));
        when(healthProfileMapper.toResponse(testProfile)).thenReturn(expectedResponse);
        var result = healthProfileService.getAllHealthProfiles();
        assertThat(result).hasSize(1);
    }
}