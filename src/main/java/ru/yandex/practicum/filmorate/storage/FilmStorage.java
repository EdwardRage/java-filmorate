package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {
    Collection<Film> get();

    Optional<Film> getFilmById(long filmId);

    Film create(Film film);

    Film updateFilm(Film newFilm);

    void addLike(Film film, User user);

    void deleteLike(Film film, User user);

    Set<Long> getLikes(Film film);
}
