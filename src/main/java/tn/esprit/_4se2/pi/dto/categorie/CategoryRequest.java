package tn.esprit._4se2.pi.dto.categorie;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryRequest {
    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 50, message = "Category name must be between 2 and 50 characters")
    private String name;

    private String description;

    @NotNull(message = "Category capacity is required")
    @Positive(message = "Category capacity must be greater than zero")
    private Integer capacity;
}
