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
import tn.esprit._4se2.pi.dto.Booking.BookingRequest;
import tn.esprit._4se2.pi.dto.Booking.BookingResponse;
import tn.esprit._4se2.pi.security.CustomUserDetailsService;
import tn.esprit._4se2.pi.security.jwt.JwtService;
import tn.esprit._4se2.pi.services.Booking.IBookingService;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingRestController.class)
@AutoConfigureMockMvc(addFilters = false)
public class BookingRestControllerTest {

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IBookingService bookingService;
    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllBookings() throws Exception {
        BookingResponse b1 = new BookingResponse();
        b1.setId(1L);
        b1.setStatus("CONFIRMED");

        Mockito.when(bookingService.getAllBookings()).thenReturn(Arrays.asList(b1));

        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].status").value("CONFIRMED"));
    }

    @Test
    void testGetBookingById() throws Exception {
        BookingResponse b1 = new BookingResponse();
        b1.setId(5L);

        Mockito.when(bookingService.getBookingById(5L)).thenReturn(b1);

        mockMvc.perform(get("/api/bookings/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    void testCreateBooking() throws Exception {
        BookingRequest request = new BookingRequest();
        request.setUserId(2L);
        request.setSportSpaceId(3L);
        request.setStartTime(LocalDateTime.now().plusDays(1));
        request.setEndTime(LocalDateTime.now().plusDays(1).plusHours(1));

        BookingResponse response = new BookingResponse();
        response.setId(10L);
        response.setStatus("PENDING");

        Mockito.when(bookingService.createBooking(any(BookingRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void testCancelBooking() throws Exception {
        Mockito.doNothing().when(bookingService).cancelBooking(10L);

        mockMvc.perform(patch("/api/bookings/10/cancel"))
                .andExpect(status().isNoContent());

        Mockito.verify(bookingService).cancelBooking(10L);
    }
}
