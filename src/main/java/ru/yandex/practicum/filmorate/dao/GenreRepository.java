package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreRepository {

    Genre getGenreById(int id);

    List<Genre> getAllGenre();

    List<Genre> getGenreByFilm();

    /*Genre createGenre(Genre genre);

    Genre deleteGenre(Genre genre );*/
}
