package tn.esprit._4se2.pi.restcontrollers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit._4se2.pi.dto.HealthProfile.HealthProfileRequest;
import tn.esprit._4se2.pi.dto.HealthProfile.HealthProfileResponse;
import tn.esprit._4se2.pi.services.HealthProfile.IHealthProfileService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class HealthProfileRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IHealthProfileService healthProfileService;

    @Test
    void createHealthProfile_ReturnsCreated() throws Exception {
        HealthProfileRequest request = new HealthProfileRequest();
        request.setUserId(1L);
        request.setWeight(70.0);
        request.setHeight(175.0);
        request.setAge(25);

        HealthProfileResponse response = new HealthProfileResponse();
        response.setId(10L);
        response.setUserId(1L);

        when(healthProfileService.createHealthProfile(any())).thenReturn(response);

        mockMvc.perform(post("/api/health-profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void getHealthProfileByUserId_ReturnsOk() throws Exception {
        Long userId = 1L;
        HealthProfileResponse response = new HealthProfileResponse();
        response.setId(5L);
        response.setUserId(userId);

        when(healthProfileService.getHealthProfileByUserId(userId)).thenReturn(response);

        mockMvc.perform(get("/api/health-profiles/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId));
    }

    @Test
    void getAllHealthProfiles_ReturnsList() throws Exception {
        when(healthProfileService.getAllHealthProfiles()).thenReturn(List.of(new HealthProfileResponse()));
        mockMvc.perform(get("/api/health-profiles"))
                .andExpect(status().isOk());
    }
}