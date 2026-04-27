package tn.esprit._4se2.pi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import tn.esprit._4se2.pi.Enum.TeamStatus;
import tn.esprit._4se2.pi.entities.converters.SportTypeConverter;
import tn.esprit._4se2.pi.entities.enums.SkillLevel;
import tn.esprit._4se2.pi.entities.enums.SportType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teams")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})  // ← ajoute ça

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Convert(converter = SportTypeConverter.class)
    private SportType sport;

    @Enumerated(EnumType.STRING)
    private SkillLevel level;

    @Column(length = 1000)
    private String description;

    private String city;

    private String logo;

    private Double latitude;

    private Double longitude;

    @ElementCollection
    @CollectionTable(name = "team_availability", joinColumns = @JoinColumn(name = "team_id"))
    private List<AvailabilitySlot> schedule;

    @ElementCollection
    @CollectionTable(name = "team_position_requirements", joinColumns = @JoinColumn(name = "team_id"))
    private List<PositionRequirement> requiredPositions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TeamStatus status = TeamStatus.ACTIVE;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
<<<<<<< Updated upstream
    @JoinColumn(name = "created_by_id", nullable = false)
=======
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "created_by_id", nullable = true, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
>>>>>>> Stashed changes
    private User createdBy;

    @JsonIgnore
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TeamMember> members = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TeamJoinRequest> joinRequests = new ArrayList<>();
}