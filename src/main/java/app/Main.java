package app;

import app.DTO.MovieDTO;
import app.Services.MovieService;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {

        MovieService movieService = new MovieService();



        List<MovieDTO> movies = movieService.fetchAllMovies(50);

        for (int i = 0; i < 1000; i++) {
            System.out.println(movies.get(i).getTitle());


        }
    }
}
