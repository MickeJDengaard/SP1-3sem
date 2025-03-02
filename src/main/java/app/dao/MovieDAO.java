package app.dao;

import app.Entities.*;
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

            // Gem genrer
            for (Genre genre : movie.getGenres()) {
                if (em.find(Genre.class, genre.getId()) == null) {
                    em.persist(genre);
                }
            }

            // Gem produktionsselskaber
            for (ProductionCompany company : movie.getProductionCompanies()) {
                if (em.find(ProductionCompany.class, company.getId()) == null) {
                    em.persist(company);
                }
            }

            // Gem skuespillere
            for (Actor actor : movie.getActors()) {
                if (em.find(Actor.class, actor.getId()) == null) {
                    em.persist(actor);
                }
            }

            // Gem instruktører
            for (Director director : movie.getDirectors()) {
                if (em.find(Director.class, director.getId()) == null) {
                    em.persist(director);
                }
            }

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
            TypedQuery<Movie> query = em.createQuery(
                    "SELECT DISTINCT m FROM Movie m " +
                            "LEFT JOIN FETCH m.genres " +
                            "LEFT JOIN FETCH m.directors " +
                            "LEFT JOIN FETCH m.actors " +
                            "WHERE LOWER(m.title) LIKE LOWER(:title)",
                    Movie.class
            );
            query.setParameter("title", "%" + title + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }


    //Metode til at tjekke om der er film gemt i databasen
    public boolean hasMovies() {
        EntityManager em = emf.createEntityManager();
        try {
            Long count = em.createQuery("SELECT COUNT(m) FROM Movie m", Long.class).getSingleResult();
            return count > 0; // Returnerer true, hvis der allerede er film i databasen
        } finally {
            em.close();
        }
    }

    public Long countMovies() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT COUNT(m) FROM Movie m", Long.class).getSingleResult();
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

    public double getAverageRating() {
        EntityManager em = emf.createEntityManager();
        try {
            Double avg = em.createQuery(
                            "SELECT AVG(m.voteAverage) FROM Movie m", Double.class)
                    .getSingleResult();
            return avg != null ? avg : 0.0; // Hvis ingen film, returner 0.0
        } finally {
            em.close();
        }
    }

    public List<Movie> getTop10HighestRatedMovies() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT m FROM Movie m ORDER BY m.voteAverage DESC", Movie.class)
                    .setMaxResults(10) // Begræns til top-10
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Movie> getTop10LowestRatedMovies() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT m FROM Movie m ORDER BY m.voteAverage ASC", Movie.class)
                    .setMaxResults(10)
                    .getResultList();
        } finally {
            em.close();
        }
    }
    public List<Movie> getTop10MostPopularMovies() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT m FROM Movie m ORDER BY m.popularity DESC", Movie.class)
                    .setMaxResults(10)
                    .getResultList();
        } finally {
            em.close();
        }
    }





}
