package tn.esprit._4se2.pi.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.List;

@Builder
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "FieldOwner")
@Table(name = "field_owners")
public class FieldOwner extends User {

    String businessName;

    @OneToMany(mappedBy = "owner")
    List<SportSpace> sportSpaces;
}