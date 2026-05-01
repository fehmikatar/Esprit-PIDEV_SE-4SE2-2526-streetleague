package tn.esprit._4se2.pi.dto.SportSpace;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;

/**
 * DTO de réponse pour la requête JPQL "stats par terrain".
 * Contient les données d'un SportSpace enrichies avec des agrégats
 * calculés à partir des tables feedbacks et bookings.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SportspacestatsResponse {

    Long id;
    String name;
    String sportType;
    String location;
    BigDecimal hourlyRate;
    Boolean isAvailable;

    /** Moyenne des ratings issus des feedbacks APPROVED (JOIN avec feedbacks). */
    Double averageRating;

    /** Nombre total de feedbacks APPROVED pour ce terrain. */
    Long feedbackCount;

    /** Nombre total de réservations (tous statuts sauf CANCELLED). */
    Long bookingCount;
}