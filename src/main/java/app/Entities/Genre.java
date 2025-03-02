package app.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "genres")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Genre {

    @Id
    private int id;
    private String name;

    @ManyToMany(mappedBy = "genres")
    private List<app.Entities.Movie> movies;
}
