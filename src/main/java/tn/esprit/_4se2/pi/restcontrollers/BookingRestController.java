package tn.esprit._4se2.pi.restcontrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.security.jwt.JwtService;
import tn.esprit._4se2.pi.dto.Booking.BookingConfirmationRequest;
import tn.esprit._4se2.pi.dto.Booking.BookingRequest;
import tn.esprit._4se2.pi.dto.Booking.BookingResponse;
import tn.esprit._4se2.pi.services.Booking.IBookingService;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingRestController {

    private final IBookingService bookingService;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @GetMapping
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getBookingsByUserId(userId));
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingResponse>> getMyBookings(
            Authentication authentication,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        Long userId = extractUserIdFromToken(authorizationHeader);

        if (userId == null && (authentication == null || authentication.getName() == null)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<BookingResponse> reservations = userId != null
                ? bookingService.getBookingsByUserId(userId)
                : bookingService.getBookingsByUserEmail(authentication.getName());

        System.out.println("Reservations trouvées: " + reservations.size());
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/my-team-bookings")
    public ResponseEntity<List<BookingResponse>> getMyTeamBookings(
            Authentication authentication,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        Long userId = extractUserIdFromToken(authorizationHeader);

        if (userId == null && (authentication == null || authentication.getName() == null)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<BookingResponse> reservations = userId != null
                ? bookingService.getBookingsByTeamMemberUserId(userId)
                : bookingService.getBookingsByTeamMemberUserEmail(authentication.getName());

        return ResponseEntity.ok(reservations);
    }

    private Long extractUserIdFromToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }

        try {
            return jwtService.extractUserId(authorizationHeader.substring(7));
        } catch (Exception ignored) {
            return null;
        }
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByOwnerId(@PathVariable Long ownerId) {
        return ResponseEntity.ok(bookingService.getBookingsByOwnerId(ownerId));
    }

    @GetMapping("/owner/me")
    public ResponseEntity<List<BookingResponse>> getMyOwnerBookings(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(bookingService.getBookingsByOwnerEmail(authentication.getName()));
    }

    @GetMapping("/sport-space/{sportSpaceId}")
    public ResponseEntity<List<BookingResponse>> getBookingsBySportSpaceId(@PathVariable Long sportSpaceId) {
        return ResponseEntity.ok(bookingService.getBookingsBySportSpaceId(sportSpaceId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<BookingResponse>> getBookingsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(bookingService.getBookingsByStatus(status));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookingResponse> updateBooking(
            @PathVariable Long id,
            @Valid @RequestBody BookingRequest request) {
        return ResponseEntity.ok(bookingService.updateBooking(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<Void> confirmBooking(
            @PathVariable Long id,
            @RequestBody(required = false) BookingConfirmationRequest request) {
        bookingService.confirmBooking(id, request == null ? "CONFIRMER" : request.getResponse());
        return ResponseEntity.noContent().build();
    }
}
