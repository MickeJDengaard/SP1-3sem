package app.Entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "production_companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductionCompany {

    @Id
    private Long id;
    private String name;
    private String logoPath;
    private String originCountry;
}
