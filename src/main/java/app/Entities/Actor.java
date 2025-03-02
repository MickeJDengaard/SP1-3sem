package app.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "actors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Actor {

    @Id
    private int id;

    private String name;

    @ManyToMany(mappedBy = "actors", fetch = FetchType.LAZY)
    private List<Movie> movies;
}
