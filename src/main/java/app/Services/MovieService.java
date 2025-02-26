package app.Services;

import app.DTO.MovieDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class MovieService {
    private static final Dotenv dotenv = Dotenv.load(); // Henter .env filen
    private static final String API_KEY = dotenv.get("TMDB_API_KEY"); // Henter API key
    private static final String BASE_URL = "https://api.themoviedb.org/3/discover/movie";

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static List<MovieDTO> fetchAllMovies(int totalPages) throws IOException, InterruptedException {
        List<MovieDTO> allMovies = new ArrayList<>();
        int page = 1;

        while (page <= totalPages) {
            // Henter én side ad gangen
            String jsonResponse = fetchMovies(page);

            // Konverter JSON til et Jackson-node-træ
            JsonNode rootNode = objectMapper.readTree(jsonResponse);


            // Henter listen af film
            JsonNode results = rootNode.get("results");
            for (JsonNode node : results) {
                MovieDTO movie = objectMapper.treeToValue(node, MovieDTO.class);
                allMovies.add(movie);
            }

            page++; // Gå til næste side
        }

        return allMovies;
    }


    private static String fetchMovies(int page) throws IOException, InterruptedException {
        String url = BASE_URL + "?api_key=" + API_KEY + "&page=" + page;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
