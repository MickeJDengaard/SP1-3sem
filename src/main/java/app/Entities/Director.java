package app.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "directors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Director {

    @Id
    private int id;

    private String name;

    @ManyToMany(mappedBy = "directors", fetch = FetchType.LAZY)
    private List<Movie> movies;
}
