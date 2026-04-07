package tn.esprit._4se2.pi.restcontrollers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit._4se2.pi.dto.User.UserRequest;
import tn.esprit._4se2.pi.dto.User.UserResponse;
import tn.esprit._4se2.pi.security.CustomUserDetailsService;
import tn.esprit._4se2.pi.security.jwt.JwtService;
import tn.esprit._4se2.pi.services.User.IUserService;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserRestController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserRestControllerTest {

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetUserById() throws Exception {
        UserResponse u = new UserResponse();
        u.setId(1L);
        u.setFirstName("John");
        u.setLastName("Doe");

        Mockito.when(userService.getUserById(1L)).thenReturn(u);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void testGetAllUsers() throws Exception {
        UserResponse u = new UserResponse();
        u.setId(10L);

        Mockito.when(userService.getAllUsers()).thenReturn(Arrays.asList(u));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateProfile() throws Exception {
        Map<String, String> profileData = new HashMap<>();
        profileData.put("firstName", "Jane");
        profileData.put("lastName", "Doe");
        profileData.put("email", "jane@example.com");

        UserResponse response = new UserResponse();
        response.setId(5L);
        response.setFirstName("Jane");
        response.setEmail("jane@example.com");

        // "any()" est utilisé pour simplifier, car le contrôleur effectue d'abord des extractions de la Map
        Mockito.when(userService.updateProfile(eq(5L), any(), any(), any(), any()))
                .thenReturn(response);

        mockMvc.perform(patch("/api/users/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.email").value("jane@example.com"));
    }

    @Test
    void testDeactivateUser() throws Exception {
        Mockito.doNothing().when(userService).deactivateUser(3L);

        mockMvc.perform(patch("/api/users/3/deactivate"))
                .andExpect(status().isNoContent());

        Mockito.verify(userService).deactivateUser(3L);
    }
}
