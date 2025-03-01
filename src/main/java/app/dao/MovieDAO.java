package app.dao;

import app.Entities.Genre;
import app.Entities.Movie;
import app.Entities.ProductionCompany;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

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

            // Gem genrer, hvis de ikke allerede findes
            for (Genre genre : movie.getGenres()) {
                if (em.find(Genre.class, genre.getId()) == null) {
                    em.persist(genre);
                }
            }

            // Gem produktionsselskaber, hvis de ikke allerede findes
            for (ProductionCompany company : movie.getProductionCompanies()) {
                if (em.find(ProductionCompany.class, company.getId()) == null) {
                    em.persist(company);
                }
            }

            // Gem selve filmen
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

    public List<Movie> findByTitle(String title) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            TypedQuery<Movie> query = em.createQuery(
                    "SELECT DISTINCT m FROM Movie m LEFT JOIN FETCH m.genres WHERE LOWER(m.title) LIKE LOWER(:title)",
                    Movie.class
            );
            query.setParameter("title", "%" + title + "%");
            List<Movie> movies = query.getResultList();
            em.getTransaction().commit();
            return movies;
        } finally {
            em.close();
        }
    }

    public List<Movie> getRandomMovies(int limit) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT m FROM Movie m ORDER BY FUNCTION('RANDOM')", Movie.class)
                    .setMaxResults(limit)
                    .getResultList();
        } finally {
            em.close();
        }
    }



}
