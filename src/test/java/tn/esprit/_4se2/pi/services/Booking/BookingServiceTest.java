package tn.esprit._4se2.pi.services.Booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import tn.esprit._4se2.pi.dto.Booking.BookingRequest;
import tn.esprit._4se2.pi.dto.Booking.BookingResponse;
import tn.esprit._4se2.pi.entities.Booking;
import tn.esprit._4se2.pi.entities.SportSpace;
import tn.esprit._4se2.pi.mappers.BookingMapper;
import tn.esprit._4se2.pi.repositories.BookingRepository;
import tn.esprit._4se2.pi.repositories.SportSpaceRepository;
import tn.esprit._4se2.pi.repositories.UserRepository;
import tn.esprit._4se2.pi.services.Notification.INotificationService;
import tn.esprit._4se2.pi.services.WebSocket.WebSocketNotificationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private WebSocketNotificationService webSocketNotificationService;

    @Mock
    private INotificationService notificationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SportSpaceRepository sportSpaceRepository;

    @InjectMocks
    private BookingService bookingService;

    private SportSpace sportSpace;

    @BeforeEach
    void setUp() {
        sportSpace = mock(SportSpace.class);
        when(sportSpace.getName()).thenReturn("Terrain Central");

        when(userRepository.findById(7L)).thenReturn(Optional.empty());
        when(sportSpaceRepository.findById(3L)).thenReturn(Optional.of(sportSpace));

        when(bookingMapper.toResponse(any(Booking.class))).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            BookingResponse response = new BookingResponse();
            response.setId(booking.getId());
            response.setUserId(booking.getUserId());
            response.setSportSpaceId(booking.getSportSpaceId());
            response.setStartTime(booking.getStartTime());
            response.setEndTime(booking.getEndTime());
            response.setStatus(booking.getStatus());
            return response;
        });

        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            if (booking.getId() == null) {
                booking.setId(100L);
            }
            return booking;
        });
    }

    @Test
    void createBookingRejectsRequestsMadeLessThanTwoHoursInAdvance() {
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        BookingRequest request = buildRequest(startTime, startTime.plusHours(1));

        when(bookingRepository.findBySportSpaceId(3L)).thenReturn(List.of());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.createBooking(request)
        );

        assertTrue(exception.getMessage().contains("⛔ Réservation impossible"));
        assertTrue(exception.getMessage().contains("Prochain créneau disponible :"));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBookingCreatesPendingConfirmationReservationWhenSlotIsMoreThanOneDayAway() {
        LocalDateTime startTime = LocalDateTime.now().plusDays(2).withSecond(0).withNano(0);
        BookingRequest request = buildRequest(startTime, startTime.plusHours(1));
        Booking mappedBooking = buildBooking(startTime, startTime.plusHours(1), "PENDING_CONFIRMATION");

        when(bookingRepository.findBySportSpaceId(3L)).thenReturn(List.of());
        when(bookingMapper.toEntity(request)).thenReturn(mappedBooking);

        BookingResponse response = bookingService.createBooking(request);

        assertEquals("PENDING_CONFIRMATION", response.getStatus());
        assertTrue(response.getMessage().contains("✅ Réservation confirmée !"));
        assertTrue(response.getMessage().contains("Terrain Central"));
        assertTrue(response.getMessage().contains("Joueur : joueur"));
        verify(webSocketNotificationService).sendReservationNotification(
                eq(7L),
                eq("Réservation confirmée !"),
                any(String.class),
                eq("Terrain Central"),
                any(String.class),
                any(String.class)
        );
        verify(webSocketNotificationService, never()).sendNotification(eq(7L), eq("BOOKING_REMINDER"), any(String.class), any(String.class));
    }

    @Test
    void createBookingKeepsPendingStatusUntilOneHourBeforeMatch() {
        LocalDateTime startTime = LocalDateTime.now().plusHours(5);
        BookingRequest request = buildRequest(startTime, startTime.plusHours(1));
        Booking mappedBooking = buildBooking(startTime, startTime.plusHours(1), "PENDING_CONFIRMATION");

        when(bookingRepository.findBySportSpaceId(3L)).thenReturn(List.of());
        when(bookingMapper.toEntity(request)).thenReturn(mappedBooking);

        BookingResponse response = bookingService.createBooking(request);

        assertEquals("PENDING_CONFIRMATION", response.getStatus());
        verify(webSocketNotificationService, never()).sendNotification(
                eq(7L),
                eq("BOOKING_REMINDER"),
                eq("Rappel de réservation"),
                any(String.class)
        );
    }

    @Test
    void confirmBookingAcceptsOuiAndMarksReservationAsConfirmed() {
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        Booking booking = buildPersistedBooking(startTime, startTime.plusHours(1), "PENDING_CONFIRMATION");

        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));

        bookingService.confirmBooking(100L, "OUI");

        assertEquals("CONFIRMED", booking.getStatus());
        verify(webSocketNotificationService).sendNotification(
                eq(7L),
                eq("BOOKING_CONFIRMATION"),
                eq("Présence confirmée"),
                any(String.class)
        );
    }

    @Test
    void sendBookingConfirmationRemindersMarksDueBookingsAsReminderSent() {
        LocalDateTime startTime = LocalDateTime.now().plusHours(1).minusMinutes(5);
        Booking booking = buildPersistedBooking(startTime, startTime.plusHours(1), "PENDING_CONFIRMATION");

        when(bookingRepository.findByStatus("PENDING_CONFIRMATION")).thenReturn(List.of(booking));

        bookingService.sendBookingConfirmationReminders();

        assertEquals("REMINDER_SENT", booking.getStatus());
        verify(webSocketNotificationService).sendNotification(
                eq(7L),
                eq("BOOKING_REMINDER"),
                eq("Rappel de réservation"),
                any(String.class)
        );
    }

    @Test
    void cancelExpiredUnconfirmedBookingsCancelsPendingReservations() {
        LocalDateTime startTime = LocalDateTime.now().minusMinutes(15);
        Booking pendingBooking = buildPersistedBooking(startTime, startTime.plusHours(1), "PENDING_CONFIRMATION");
        Booking remindedBooking = buildPersistedBooking(startTime.minusHours(1), startTime, "REMINDER_SENT");

        when(bookingRepository.findByStatus("PENDING_CONFIRMATION")).thenReturn(List.of(pendingBooking));
        when(bookingRepository.findByStatus("REMINDER_SENT")).thenReturn(List.of(remindedBooking));

        bookingService.cancelExpiredUnconfirmedBookings();

        assertEquals("CANCELLED", pendingBooking.getStatus());
        assertEquals("CANCELLED", remindedBooking.getStatus());
        verify(webSocketNotificationService, times(2)).sendCancellationNotification(
                eq(7L),
                eq("Réservation annulée"),
                any(String.class),
                eq("Terrain Central")
        );
    }

    private BookingRequest buildRequest(LocalDateTime startTime, LocalDateTime endTime) {
        BookingRequest request = new BookingRequest();
        request.setUserId(7L);
        request.setSportSpaceId(3L);
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        return request;
    }

    private Booking buildBooking(LocalDateTime startTime, LocalDateTime endTime, String status) {
        Booking booking = new Booking();
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setStatus(status);
        return booking;
    }

    private Booking buildPersistedBooking(LocalDateTime startTime, LocalDateTime endTime, String status) {
        Booking booking = buildBooking(startTime, endTime, status);
        booking.setId(100L);
        booking.setUserId(7L);
        booking.setSportSpaceId(3L);
        return booking;
    }
}
