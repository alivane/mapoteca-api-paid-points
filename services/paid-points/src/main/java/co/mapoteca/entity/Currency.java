package co.mapoteca.entity;

import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Currency {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(nullable = false)
    private String country;
    @Column(nullable = false)
    private String currency;
}
