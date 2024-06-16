package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreService {

    Genre getGenreById(int genreId);

    List<Genre> getAllGenre();
}
