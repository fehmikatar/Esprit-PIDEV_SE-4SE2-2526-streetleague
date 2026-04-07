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
import tn.esprit._4se2.pi.dto.Feedback.FeedbackRequest;
import tn.esprit._4se2.pi.dto.Feedback.FeedbackResponse;
import tn.esprit._4se2.pi.security.CustomUserDetailsService;
import tn.esprit._4se2.pi.security.jwt.JwtService;
import tn.esprit._4se2.pi.services.Feedback.IFeedbackService;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FeedbackRestController.class)
@AutoConfigureMockMvc(addFilters = false)
public class FeedbackRestControllerTest {

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IFeedbackService feedbackService;
    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateFeedback() throws Exception {
        FeedbackRequest request = new FeedbackRequest();
        request.setComment("Super terrain");
        request.setRating(5);
        request.setUserId(2L);
        request.setBookingId(3L);
        request.setSportSpaceId(4L);

        FeedbackResponse response = new FeedbackResponse();
        response.setId(1L);
        response.setComment("Super terrain");
        response.setRating(5);

        Mockito.when(feedbackService.createFeedback(any(FeedbackRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/feedbacks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.rating").value(5));
    }

    @Test
    void testGetAllFeedbacks() throws Exception {
        FeedbackResponse fb = new FeedbackResponse();
        fb.setId(1L);
        fb.setComment("Excellent!");

        Mockito.when(feedbackService.getAllFeedbacks()).thenReturn(Arrays.asList(fb));

        mockMvc.perform(get("/api/feedbacks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].comment").value("Excellent!"));
    }

    @Test
    void testDeleteFeedback() throws Exception {
        Mockito.doNothing().when(feedbackService).deleteFeedback(2L);

        mockMvc.perform(delete("/api/feedbacks/2"))
                .andExpect(status().isNoContent());

        Mockito.verify(feedbackService).deleteFeedback(2L);
    }
}
