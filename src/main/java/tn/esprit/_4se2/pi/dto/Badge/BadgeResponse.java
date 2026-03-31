package tn.esprit._4se2.pi.dto.Badge;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BadgeResponse {
    private Long id;
    private String name;
    private String description;
    private int level;
    private int requiredXp;
    private String iconUrl;
}