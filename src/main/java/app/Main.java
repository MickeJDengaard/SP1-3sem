package app;

import app.DTO.MovieDTO;
import app.Entities.Movie;
import app.Services.MovieService;
import app.config.HibernateConfig;
import app.dao.MovieDAO;
import jakarta.persistence.EntityManagerFactory;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        MovieService movieService = new MovieService();
        MovieDAO movieDAO = new MovieDAO(emf);

        List<MovieDTO> movies = movieService.fetchAllMovies(5);

        for (MovieDTO dto : movies) {
            Movie movie = movieService.convertDTOtoEntity(dto);
            movieDAO.save(movie);
        }

        System.out.println("✅ Filmene er gemt i databasen!");
    }
}


