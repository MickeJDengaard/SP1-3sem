package app.Services;

import app.DTO.MovieDTO;
import app.Entities.Movie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;

class MovieServiceTest {

    private MovieService movieService;

    @BeforeEach
    void setUp() throws Exception {
        // Opretter service-instansen.
        movieService = new MovieService();
        Field genreMapField = MovieService.class.getDeclaredField("genreMap");
        genreMapField.setAccessible(true);
        @SuppressWarnings("unchecked") Map<Integer, String> genreMap = (Map<Integer, String>) genreMapField.get(null);
        genreMap.clear();
        // Tilføj testdata
        genreMap.put(28, "Action");
        genreMap.put(12, "Adventure");
        genreMap.put(99, "Documentary");
    }

    @Test
    void testGetGenres() {
        // Arrange
        List<Integer> genreIds = List.of(28, 12, 100); // 100 findes ikke i testdata

        // Act
        String genres = movieService.getGenres(genreIds);

        // Assert
        assertEquals("Action, Adventure", genres, "Skal returnere de fundne genrenavne, separeret med komma.");
    }

    @Test
    void testConvertDTOtoEntity() {
        // Arrange: opret en MovieDTO med kendte værdier
        MovieDTO dto = new MovieDTO();
        dto.setId(0);
        dto.setTitle("Test Movie");
        dto.setReleaseDate("2025-01-01");
        dto.setPopularity(7.5);
        dto.setVoteAverage(8.2);
        dto.setVoteCount(1000);
        // Sæt genreIds
        dto.setGenreIds(List.of(28, 99));
        // Sæt produktionsselskaber til tom liste
        dto.setProductionCompanies(List.of());

        // Act
        Movie movie = movieService.convertDTOtoEntity(dto);

        // Assert: sammenlign felter
        assertEquals(dto.getId(), movie.getId());
        assertEquals(dto.getTitle(), movie.getTitle());
        assertEquals(dto.getReleaseDate(), movie.getReleaseDate());
        assertEquals(dto.getPopularity(), movie.getPopularity(), 0.001);
        assertEquals(dto.getVoteAverage(), movie.getVoteAverage(), 0.001);
        assertEquals(dto.getVoteCount(), movie.getVoteCount());
        // Tjek at genrerne er korrekt konverteret
        assertThat("Skal have 2 genrer", movie.getGenres(), hasSize(2));
        // Vi kan f.eks. tjekke at der findes en genre med id 28 og navn "Action"
        boolean hasAction = movie.getGenres().stream().anyMatch(g -> g.getId() == 28 && "Action".equals(g.getName()));
        assertTrue(hasAction, "Skal indeholde genre 'Action'");
        // Og tjekke for 'Documentary'
        boolean hasDoc = movie.getGenres().stream().anyMatch(g -> g.getId() == 99 && "Documentary".equals(g.getName()));
        assertTrue(hasDoc, "Skal indeholde genre 'Documentary'");
        // Tjek at produktionsselskaber er tomme
        assertThat("Skal have 0 produktionsselskaber", movie.getProductionCompanies(), hasSize(0));
    }

    @Test
    void testFetchAllMovies() throws Exception {
        // Denne test rører den eksterne API.
        List<MovieDTO> movies = movieService.fetchAllMovies(1);
        assertNotNull(movies);
        // Hvis API'et svarer korrekt, bør vi få en ikke-tom liste
        assertFalse(movies.isEmpty(), "API'et skal returnere mindst én film på side 1.");
    }
}
