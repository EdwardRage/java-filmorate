package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmRepository {
    Collection<Film> get();

    Optional<Film> getFilmById(long filmId);

    Film create(Film film);  // insert

    Film update(Film newFilm); // update уже не используется

    void addLike(long filmId, long userId);

    void deleteLike(long filmId, long userId);

    List<Integer> getLikes(long filmsId);
    List<Film> getTopPopular(int count);
}
