package tn.esprit._4se2.pi.restcontrollers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit._4se2.pi.dto.SportSpace.SportSpaceRequest;
import tn.esprit._4se2.pi.dto.SportSpace.SportSpaceResponse;
import tn.esprit._4se2.pi.security.CustomUserDetailsService;
import tn.esprit._4se2.pi.security.jwt.JwtService;
import tn.esprit._4se2.pi.services.SportSpace.ISportSpaceService;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;

import java.util.Arrays;
import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SportSpaceRestController.class)
@AutoConfigureMockMvc(addFilters = false)
public class SportSpaceRestControllerTest {

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ISportSpaceService sportSpaceService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllSportSpaces() throws Exception {
        SportSpaceResponse s = new SportSpaceResponse();
        s.setId(1L);
        s.setName("Terrain Synthétique");

        Mockito.when(sportSpaceService.getAllSportSpaces()).thenReturn(Arrays.asList(s));

        mockMvc.perform(get("/api/sport-spaces"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("Terrain Synthétique"));
    }

    @Test
    void testGetSportSpaceById() throws Exception {
        SportSpaceResponse s = new SportSpaceResponse();
        s.setId(2L);
        s.setName("Salle Couverte");

        Mockito.when(sportSpaceService.getSportSpaceById(2L)).thenReturn(s);

        mockMvc.perform(get("/api/sport-spaces/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Salle Couverte"));
    }

    @Test
    void testCreateSportSpace() throws Exception {
        SportSpaceRequest request = new SportSpaceRequest();
        request.setName("Nouveau Terrain");
        request.setIsAvailable(true);
        request.setCapacity(10);
        request.setDescription("A nice place");
        request.setHourlyRate(new BigDecimal("20.00"));
        request.setLocation("Tunis");
        request.setAddress("Avenue Habib Bourguiba");
        request.setSportType("Football");

        SportSpaceResponse response = new SportSpaceResponse();
        response.setId(3L);
        response.setName("Nouveau Terrain");

        Mockito.when(sportSpaceService.createSportSpace(any(SportSpaceRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/sport-spaces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Nouveau Terrain"));
    }
}
