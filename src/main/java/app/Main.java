package app;

import app.DTO.MovieDTO;
import app.Entities.Movie;
import app.Services.MovieService;
import app.config.HibernateConfig;
import app.dao.MovieDAO;
import jakarta.persistence.EntityManagerFactory;
import app.Entities.Genre;
import app.Entities.Actor;
import app.Entities.Director;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        MovieService movieService = new MovieService();
        MovieDAO movieDAO = new MovieDAO(emf);


        if (!movieDAO.hasMovies()) {
            System.out.println("🎥 Henter film fra The Movie Database API...");
            List<MovieDTO> movies = movieService.fetchAllMovies(58);

            for (MovieDTO dto : movies) {
                System.out.println("Gemmer filmen: " + dto.getTitle());
                Movie movie = movieService.convertDTOtoEntity(dto);
                movieDAO.save(movie);
            }
            System.out.println("✅ Filmene er gemt i databasen!");
        } else {
            long movieCount = movieDAO.countMovies();
            System.out.println("🎥 Filmene er allerede gemt i databasen!");
            System.out.println("🎥 Der er i alt " + movieCount + " film i databasen.");
        }


        System.out.println("🔍 Du kan nu søge efter film i databasen.");

        //Søgefelt for at finde gemte film i databasen
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n🔍 Indtast en film-titel for at søge, skriv 'random' for at få 5 tilfældige film, eller 'exit' for at afslutte:");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            if (input.equalsIgnoreCase("random")) {
                List<Movie> randomMovies = movieDAO.getRandomMovies(5);
                System.out.println("🎲 Her er 5 tilfældige film du kan se:");
                for (Movie movie : randomMovies) {
                    System.out.println("- " + movie.getTitle() + " (" + movie.getReleaseDate() + ")");
                }
                continue;
            }

            if (input.equalsIgnoreCase("list")) {
                System.out.println("📜 Henter alle film fra databasen... | Dette kan tage lidt tid..");
                List<Movie> allMovies = movieDAO.getAllMovies();
                System.out.println("📜 Her er en liste over alle film i databasen:");
                for (Movie movie : allMovies) {
                    System.out.println("- " + movie.getTitle() + " (" + movie.getReleaseDate() + ")");
                }
                continue;
            }

            // 🔍 Søg efter film
            List<Movie> foundMovies = movieDAO.findByTitle(input);
            if (foundMovies.isEmpty()) {
                System.out.println("❌ Ingen film fundet med titlen: " + input);
            } else {
                for (Movie movie : foundMovies) {
                    System.out.println("- " + movie.getTitle() + " (" + movie.getReleaseDate() + ")");

                    // 🎭 Vis genrer
                    if (movie.getGenres() == null || movie.getGenres().isEmpty()) {
                        System.out.println("  ❌ ERROR: Filmen har ingen genrer!");
                    } else {
                        String genreNames = movie.getGenres().stream()
                                .map(Genre::getName)
                                .toList()
                                .toString();
                        System.out.println("  🎭 Genrer: " + genreNames);
                    }

                    // 🎬 Vis instruktører
                    if (movie.getDirectors() == null || movie.getDirectors().isEmpty()) {
                        System.out.println("  🎬 Instruktører: Ingen instruktører fundet!");
                    } else {
                        String directorNames = movie.getDirectors().stream()
                                .map(Director::getName)
                                .toList()
                                .toString();
                        System.out.println("  🎬 Instruktører: " + directorNames);
                    }

                    // 🎭 Vis skuespillere
                    if (movie.getActors() == null || movie.getActors().isEmpty()) {
                        System.out.println("  🎭 Skuespillere: Ingen skuespillere fundet!");
                    } else {
                        String actorNames = movie.getActors().stream()
                                .map(Actor::getName)
                                .toList()
                                .toString();
                        System.out.println("  🎭 Skuespillere: " + actorNames);
                    }
                }
            }
        }



    }
}


