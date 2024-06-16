package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository jdbcGenre;

    @Override
    public Genre getGenreById(int genreId) {
        return jdbcGenre.getGenreById(genreId)
                .orElseThrow(() -> new NotFoundException("Фильм с идентификатором id = " + genreId + " не найден"));
    }

    @Override
    public List<Genre> getAllGenre() {
        return jdbcGenre.getAllGenre();
    }
}
