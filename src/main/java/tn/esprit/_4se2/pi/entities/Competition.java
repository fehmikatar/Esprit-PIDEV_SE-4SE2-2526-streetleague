package tn.esprit._4se2.pi.Entities;


import jakarta.persistence.*;
import tn.esprit._4se2.pi.Enum.CompetitionFormat;
import tn.esprit._4se2.pi.Enum.CompetitionStatus;

import java.time.LocalDate;

@Entity
@Table(name ="competitions")
public class Competition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String name;

    @Column(length=2000)
    private String description;

    @Column(length=4000)
    private String rules;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private CompetitionFormat format = CompetitionFormat.LEAGUE;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private CompetitionStatus status = CompetitionStatus.DRAFT;

    private LocalDate startDate;
    private LocalDate endDate;

    private String location;

    // simplifié: organiserId au lieu de relation User
    private Long organizerId;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRules() { return rules; }
    public void setRules(String rules) { this.rules = rules; }
    public CompetitionFormat getFormat() { return format; }
    public void setFormat(CompetitionFormat format) { this.format = format; }
    public CompetitionStatus getStatus() { return status; }
    public void setStatus(CompetitionStatus status) { this.status = status; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Long getOrganizerId() { return organizerId; }
    public void setOrganizerId(Long organizerId) { this.organizerId = organizerId; }
}