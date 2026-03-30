package tn.esprit._4se2.pi.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDate createdAt;

    @ManyToOne
    @JoinColumn(name = "responsible_id")
    private User responsible;

    @OneToMany(mappedBy = "team")
    private List<TeamMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "team")
    private List<Message> messages = new ArrayList<>();
}