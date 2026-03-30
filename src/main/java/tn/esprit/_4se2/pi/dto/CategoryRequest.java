package tn.esprit._4se2.pi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CategoryRequest {
    @NotBlank(message = "Category name is required")
    private String name;

    private String description;

    @NotNull(message = "Category capacity is required")
    @Positive(message = "Category capacity must be greater than zero")
    private Integer capacity;
}
