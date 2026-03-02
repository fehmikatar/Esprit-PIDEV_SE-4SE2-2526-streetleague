package tn.esprit._4se2.pi.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PromotionRequest {

    @NotBlank(message = "Promotion name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    String name;

    @NotBlank(message = "Promo code is required")
    @Pattern(regexp = "^[A-Z0-9]{4,20}$", message = "Promo code must be 4-20 uppercase letters or digits")
    String promoCode;

    @NotNull(message = "Discount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Discount must be greater than 0")
    @DecimalMax(value = "100.0", message = "Discount cannot exceed 100%")
    Double discount; // percentage

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be today or in the future")
    LocalDate startDate;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    LocalDate endDate;
}