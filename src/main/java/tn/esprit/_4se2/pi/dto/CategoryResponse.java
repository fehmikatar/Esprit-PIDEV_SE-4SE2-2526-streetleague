package tn.esprit._4se2.pi.dto;

import lombok.Data;

@Data
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private Integer capacity;
}
