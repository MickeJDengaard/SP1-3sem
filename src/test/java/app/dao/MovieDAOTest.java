package app.dao;

import Populators.MoviePopulator;
import app.Entities.Movie;
import app.config.HibernateConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;

class MovieDAOTest {
    // Brug test-databasekonfigurationen fra HibernateConfig
    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
    private static final MovieDAO movieDAO = new MovieDAO(emf);

    private static Movie m1;
    private static Movie m2;

    @BeforeEach
    void setUp() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            // Slet alle poster fra den faktiske tabel og nulstil identiteten
            em.createNativeQuery("TRUNCATE TABLE movies RESTART IDENTITY CASCADE").executeUpdate();
            em.getTransaction().commit();

            // Populér testdata
            Movie[] movies = MoviePopulator.populate(movieDAO);
            m1 = movies[0];
            m2 = movies[1];
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void save() {
        // Arrange: Opret en ny film
        Movie m3 = new Movie(0, "Interstellar", "2014-11-07", 8.6, 8.9, 18000, new ArrayList<>(), new ArrayList<>());

        // Act: Gem den nye film og hent alle film
        movieDAO.save(m3);
        List<Movie> movies = movieDAO.getAllMovies();

        // Assert: Der skal nu være 3 film, og "Interstellar" skal være blandt dem
        assertThat("Antallet af film skal være 3", movies, hasSize(3));
        assertThat("Listen over film skal indeholde 'Interstellar'", movies,
                hasItem(hasProperty("title", equalTo("Interstellar"))));
    }

    @Test
    void getAllMovies() {
        // Act: Hent alle film
        List<Movie> movies = movieDAO.getAllMovies();

        // Assert: Der skal være præcis 2 film (m1 og m2) med de forventede titler
        assertThat("Der skal være 2 film i databasen", movies, hasSize(2));
        assertThat("Listen skal indeholde 'Inception'", movies,
                hasItem(hasProperty("title", equalTo("Inception"))));
        assertThat("Listen skal indeholde 'The Dark Knight'", movies,
                hasItem(hasProperty("title", equalTo("The Dark Knight"))));
    }

    @Test
    void findByTitle() {
        // Act & Assert: Søg efter "inception"
        List<Movie> resultInception = movieDAO.findByTitle("inception");
        assertThat("Der skal findes 1 film med 'Inception'", resultInception, hasSize(1));
        assertThat(resultInception.get(0).getTitle(), equalTo("Inception"));

        // Act & Assert: Søg efter "dark" for at finde "The Dark Knight"
        List<Movie> resultDark = movieDAO.findByTitle("dark");
        assertThat("Der skal findes 1 film med 'dark'", resultDark, hasSize(1));
        assertThat(resultDark.get(0).getTitle(), equalTo("The Dark Knight"));
    }

    @Test
    void getRandomMovies() {
        // Act: Hent tilfældige film med angivne grænser
        List<Movie> randomOne = movieDAO.getRandomMovies(1);
        List<Movie> randomTwo = movieDAO.getRandomMovies(2);

        // Assert: Tjek at antallet af returnerede film er korrekt
        assertThat("Skal returnere 1 tilfældig film", randomOne, hasSize(1));
        assertThat("Skal returnere 2 tilfældige film", randomTwo, hasSize(2));

        // Yderligere kontrol: De tilfældigt valgte film skal findes i den samlede liste
        List<Movie> allMovies = movieDAO.getAllMovies();
        randomTwo.forEach(movie ->
                assertThat("Den tilfældigt valgte film skal findes i den samlede liste", allMovies,
                        hasItem(movie))
        );
    }
}
