package app.Services;

import app.DTO.MovieDTO;
import app.Entities.Genre;
import app.Entities.Movie;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class MovieService {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String API_KEY = dotenv.get("TMDB_API_KEY");
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Map til at gemme genre-ID'er og deres tilsvarende navne
    private static final Map<Integer, String> genreMap = new HashMap<>();

    public MovieService() throws IOException, InterruptedException {
        fetchGenres(); // Hent genrer, når service initialiseres
    }

    // Henter alle genrer fra TMDb og gemmer dem i genreMap
    private void fetchGenres() throws IOException, InterruptedException {
        String url = BASE_URL + "genre/movie/list?api_key=" + API_KEY + "&language=en-US";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonNode rootNode = objectMapper.readTree(response.body());
        JsonNode genresArray = rootNode.get("genres");

        for (JsonNode node : genresArray) {
            int id = node.get("id").asInt();
            String name = node.get("name").asText();
            genreMap.put(id, name);
        }
    }

    // Matcher genre-ID'er med navne og returnerer en kommasepareret liste
    public String getGenres(List<Integer> genreIds) {
        List<String> genreNames = new ArrayList<>();
        for (Integer id : genreIds) {
            if (genreMap.containsKey(id)) {
                genreNames.add(genreMap.get(id));
            }
        }
        return String.join(", ", genreNames);
    }

    // Henter film fra TMDb API
    public List<MovieDTO> fetchAllMovies(int totalPages) throws IOException, InterruptedException {
        List<MovieDTO> allMovies = new ArrayList<>();
        int page = 1;

        while (page <= totalPages) {
            String jsonResponse = fetchMovies(page);
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode results = rootNode.get("results");

            for (JsonNode node : results) {
                MovieDTO movie = objectMapper.treeToValue(node, MovieDTO.class);
                allMovies.add(movie);
            }
            page++;
        }
        return allMovies;
    }

    private static String fetchMovies(int page) throws IOException, InterruptedException {
        String url = BASE_URL + "discover/movie?api_key=" + API_KEY + "&page=" + page;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public Movie convertDTOtoEntity(MovieDTO dto) {
        Movie movie = new Movie();
        movie.setId(dto.getId());
        movie.setTitle(dto.getTitle());
        movie.setReleaseDate(dto.getReleaseDate());
        movie.setPopularity(dto.getPopularity());
        movie.setVoteAverage(dto.getVoteAverage());
        movie.setVoteCount(dto.getVoteCount());

        List<Genre> genres = dto.getGenreIds().stream()
                .map(id -> new Genre(id, genreMap.get(id), null))
                .toList();
        movie.setGenres(genres);

        return movie;
    }

}
