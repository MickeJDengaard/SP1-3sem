package Populators;

import app.Entities.Movie;
import app.dao.MovieDAO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.ArrayList;
import java.util.List;

public class MoviePopulator {

    public static Movie[] populate(MovieDAO movieDAO) {
        EntityManagerFactory emf = movieDAO.getEntityManagerFactory();
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();


            // Opret testfilm
            Movie m1 = new Movie(1, "Inception", "2010-07-16", 8.8, 9.0, 20000, new ArrayList<>(), new ArrayList<>());
            Movie m2 = new Movie(2, "The Dark Knight", "2008-07-18", 9.0, 9.2, 25000, new ArrayList<>(), new ArrayList<>());



            // Persistér filmene
            movieDAO.save(m1);
            movieDAO.save(m2);

            // Returner filmene som et array
            return new Movie[]{m1, m2};
        }
    }
}
