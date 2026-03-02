package tn.esprit._4se2.pi.restcontrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.dto.BookingRequest;
import tn.esprit._4se2.pi.dto.BookingResponse;
import tn.esprit._4se2.pi.services.Booking.IBookingService;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingRestController {

    private final IBookingService bookingService;

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
    public ResponseEntity<Void> confirmBooking(@PathVariable Long id) {
        bookingService.confirmBooking(id);
        return ResponseEntity.noContent().build();
    }
}