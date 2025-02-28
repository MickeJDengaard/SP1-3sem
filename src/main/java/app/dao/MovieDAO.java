package app.dao;

import app.Entities.Genre;
import app.Entities.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class MovieDAO {

    private EntityManagerFactory emf;

    public MovieDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void save(Movie movie) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Først gemmer vi genrerne én ad gangen, hvis de ikke findes i databasen
            for (Genre genre : movie.getGenres()) {
                if (em.find(Genre.class, genre.getId()) == null) {
                    em.persist(genre);
                }
            }

            // Derefter gemmer vi selve filmen
            em.persist(movie);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public List<Movie> getAllMovies() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT m FROM Movie m", Movie.class).getResultList();
        } finally {
            em.close();
        }
    }
}
