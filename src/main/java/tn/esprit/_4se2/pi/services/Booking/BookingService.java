package tn.esprit._4se2.pi.services.Booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.dto.BookingRequest;
import tn.esprit._4se2.pi.dto.BookingResponse;
import tn.esprit._4se2.pi.entities.Booking;
import tn.esprit._4se2.pi.mappers.BookingMapper;
import tn.esprit._4se2.pi.repositories.BookingRepository;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookingService implements IBookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingResponse createBooking(BookingRequest request) {
        log.info("Creating booking for user: {} and sport space: {}", request.getUserId(), request.getSportSpaceId());

        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new RuntimeException("End time must be after start time");
        }

        Booking booking = bookingMapper.toEntity(request);
        booking.setUserId(request.getUserId());
        booking.setSportSpaceId(request.getSportSpaceId());

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created successfully with id: {}", savedBooking.getId());

        return bookingMapper.toResponse(savedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long id) {
        log.info("Fetching booking with id: {}", id);
        return bookingRepository.findById(id)
                .map(bookingMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookings() {
        log.info("Fetching all bookings");
        return bookingRepository.findAll()
                .stream()
                .map(bookingMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByUserId(Long userId) {
        log.info("Fetching bookings for user: {}", userId);
        return bookingRepository.findByUserId(userId)
                .stream()
                .map(bookingMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsBySportSpaceId(Long sportSpaceId) {
        log.info("Fetching bookings for sport space: {}", sportSpaceId);
        return bookingRepository.findBySportSpaceId(sportSpaceId)
                .stream()
                .map(bookingMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByStatus(String status) {
        log.info("Fetching bookings with status: {}", status);
        return bookingRepository.findByStatus(status)
                .stream()
                .map(bookingMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BookingResponse updateBooking(Long id, BookingRequest request) {
        log.info("Updating booking with id: {}", id);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));

        bookingMapper.updateEntity(request, booking);
        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Booking updated successfully with id: {}", id);

        return bookingMapper.toResponse(updatedBooking);
    }

    @Override
    public void deleteBooking(Long id) {
        log.info("Deleting booking with id: {}", id);

        if (!bookingRepository.existsById(id)) {
            throw new RuntimeException("Booking not found with id: " + id);
        }

        bookingRepository.deleteById(id);
        log.info("Booking deleted successfully with id: {}", id);
    }

    @Override
    public void cancelBooking(Long id) {
        log.info("Cancelling booking with id: {}", id);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));

        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
        log.info("Booking cancelled successfully with id: {}", id);
    }

    @Override
    public void confirmBooking(Long id) {
        log.info("Confirming booking with id: {}", id);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));

        booking.setStatus("CONFIRMED");
        bookingRepository.save(booking);
        log.info("Booking confirmed successfully with id: {}", id);
    }
}