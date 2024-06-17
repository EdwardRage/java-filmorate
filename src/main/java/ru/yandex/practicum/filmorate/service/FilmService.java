package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmService {

    Film create(Film film);

    Film update(Film film);

    Collection<Film> get();

    Film getFilmById(long filmId);

    void addLike(long filmId, long userId);

    void deleteLike(long filmId, long userId);

    List<Integer> getLikes(long filmId);

    Film getFilmWithGenre(long filmId);

    List<Film> getTopPopularFilms(int count);
}
