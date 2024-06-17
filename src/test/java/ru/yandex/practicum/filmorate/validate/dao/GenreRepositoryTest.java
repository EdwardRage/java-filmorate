package ru.yandex.practicum.filmorate.validate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.GenreRepository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreRepositoryTest {
    private final GenreRepository genreRepository;

    @Test
    void getGenreByIdTest() {
        Genre genre1 = genreRepository.getGenreById(1).orElseThrow();
        Genre genre2 = genreRepository.getGenreById(2).orElseThrow();
        Genre genre3 = genreRepository.getGenreById(3).orElseThrow();

        assertEquals("Комедия", genre1.getName());
        assertEquals("Драма", genre2.getName());
        assertEquals("Мультфильм", genre3.getName());
    }

    @Test
    void getAllGenreTest() {
        List<Genre> genre = genreRepository.getAllGenre();

        assertEquals(6, genre.size());
    }
}
