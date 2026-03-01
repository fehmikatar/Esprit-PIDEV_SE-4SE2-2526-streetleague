package tn.esprit._4se2.pi.services.Booking;

import tn.esprit._4se2.pi.dto.BookingRequest;
import tn.esprit._4se2.pi.dto.BookingResponse;
import java.util.List;

public interface IBookingService {
    BookingResponse createBooking(BookingRequest request);
    BookingResponse getBookingById(Long id);
    List<BookingResponse> getAllBookings();
    List<BookingResponse> getBookingsByUserId(Long userId);
    List<BookingResponse> getBookingsBySportSpaceId(Long sportSpaceId);
    List<BookingResponse> getBookingsByStatus(String status);
    BookingResponse updateBooking(Long id, BookingRequest request);
    void deleteBooking(Long id);
    void cancelBooking(Long id);
    void confirmBooking(Long id);
}