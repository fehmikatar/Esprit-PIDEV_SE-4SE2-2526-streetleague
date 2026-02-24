package tn.esprit._4se2.pi.mappers;

import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.dto.BookingRequest;
import tn.esprit._4se2.pi.dto.BookingResponse;
import tn.esprit._4se2.pi.entities.Booking;
import java.time.LocalDateTime;

@Component
public class BookingMapper {

    public Booking toEntity(BookingRequest request) {
        if (request == null) return null;

        Booking booking = new Booking();
        booking.setBookingDate(LocalDateTime.now());
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setStatus("PENDING");
        booking.setCreatedAt(LocalDateTime.now());
        return booking;
    }

    public BookingResponse toResponse(Booking entity) {
        if (entity == null) return null;

        return BookingResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .sportSpaceId(entity.getSportSpaceId())
                .bookingDate(entity.getBookingDate())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .totalPrice(entity.getTotalPrice())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public void updateEntity(BookingRequest request, Booking booking) {
        if (request == null || booking == null) return;

        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
    }
}