package app.Services;

import app.DTO.MovieDTO;
import app.Entities.*;
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

    //Metode der bliver brugt til at hente de korrekte film fra TMDb API
    private static String fetchMovies(int page) throws IOException, InterruptedException {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int startYear = currentYear - 5; // Beregner det tidligste år

        String url = BASE_URL + "discover/movie?api_key=" + API_KEY +
                "&page=" + page +
                "&with_original_language=da" +  // Kun danske film
                "&primary_release_date.gte=" + startYear + "-01-01" + // Film fra de sidste 5 år
                "&sort_by=release_date.desc"; // Nyeste først

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    // Henter cast og crew fra TMDb API
    public List<Actor> fetchActors(int movieId) throws IOException, InterruptedException {
        String url = BASE_URL + "movie/" + movieId + "/credits?api_key=" + API_KEY;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode rootNode = objectMapper.readTree(response.body());
        JsonNode castArray = rootNode.get("cast");

        List<Actor> actors = new ArrayList<>();
        for (JsonNode node : castArray) {
            if (actors.size() >= 10) break; // Begræns antal skuespillere per film

            int id = node.get("id").asInt();
            String name = node.get("name").asText();
            actors.add(new Actor(id, name, new ArrayList<>()));
        }

        return actors;
    }


    public List<Director> fetchDirectors(int movieId) throws IOException, InterruptedException {
        String url = BASE_URL + "movie/" + movieId + "/credits?api_key=" + API_KEY;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode rootNode = objectMapper.readTree(response.body());
        JsonNode crewArray = rootNode.get("crew");

        List<Director> directors = new ArrayList<>();
        for (JsonNode node : crewArray) {
            if (node.get("job").asText().equals("Director")) {
                int id = node.get("id").asInt();
                String name = node.get("name").asText();
                directors.add(new Director(id, name, new ArrayList<>()));
            }
        }

        return directors;
    }


    public Movie convertDTOtoEntity(MovieDTO dto) throws IOException, InterruptedException {
        Movie movie = new Movie();
        movie.setId(dto.getId());
        movie.setTitle(dto.getTitle());
        movie.setReleaseDate(dto.getReleaseDate());
        movie.setPopularity(dto.getPopularity());
        movie.setVoteAverage(dto.getVoteAverage());
        movie.setVoteCount(dto.getVoteCount());

        // Hent genrer
        List<Genre> genres = dto.getGenreIds().stream()
                .map(id -> new Genre(id, genreMap.get(id), null))
                .toList();
        movie.setGenres(genres);

        // Hent produktionsselskaber
        List<ProductionCompany> companies = (dto.getProductionCompanies() != null) ?
                dto.getProductionCompanies().stream()
                        .map(pc -> new ProductionCompany(pc.getId(), pc.getName(), pc.getLogoPath(), pc.getOriginCountry()))
                        .toList()
                : List.of();
        movie.setProductionCompanies(companies);

    // Hent og tilføj skuespillere
        Set<Actor> actors = new HashSet<>(fetchActors(dto.getId())); // Konverter List til Set
        movie.setActors(actors != null ? actors : new HashSet<>());

    // Hent og tilføj instruktører
        Set<Director> directors = new HashSet<>(fetchDirectors(dto.getId())); // Konverter List til Set
        movie.setDirectors(directors != null ? directors : new HashSet<>());


        return movie;
    }


}
