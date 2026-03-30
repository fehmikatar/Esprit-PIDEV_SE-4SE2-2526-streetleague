package tn.esprit._4se2.pi.dto;

import lombok.*;
import tn.esprit._4se2.pi.Enum.JoinRequestStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JoinRequestResponse {

    private Long id;

    // Applicant info
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;

    private String message;
    private JoinRequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;

    // Reviewer info (nullable)
    private Long reviewedById;
    private String reviewedByName;
}
