package tn.esprit._4se2.pi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private Long id;
    private String nom;
    private String description;
    private Long parentId;
    private List<CategoryDTO> subCategories;
    private LocalDateTime createdAt;
}
