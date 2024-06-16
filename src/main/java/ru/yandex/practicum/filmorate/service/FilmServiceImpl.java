package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.FilmRepository;
import ru.yandex.practicum.filmorate.dao.UserRepository;
import ru.yandex.practicum.filmorate.validate.ValidationService;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {

    private final FilmRepository jdbcFilm;
    private final UserRepository jdbcUser;
    private final ValidationService validate;

    @Override
    public Collection<Film> get() {
        return jdbcFilm.get();
    }

    @Override
    public Film getFilmById(long filmId) {
        return jdbcFilm.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с идентификатором id = " + filmId + " не найден"));
    }

    @Override
    public Film create(Film film) {
        validate.validateCreate(film);
        return jdbcFilm.create(film);
    }

    public Film update(Film newFilm) {
        Film oldFilm = jdbcFilm.getFilmById(newFilm.getId())
                .orElseThrow(() -> new NotFoundException("Фильм с идентификатором id = " + newFilm.getId() + " не найден"));

        log.info("Получен пользователь по идентификатору");
        if (newFilm.getName() != null) {
            oldFilm.setName(newFilm.getName());
        }
        if (newFilm.getDescription() != null) {
            validate.validateUpdate(newFilm);
            oldFilm.setDescription(newFilm.getDescription());
        }
        if (newFilm.getReleaseDate() != null) {
            validate.validateUpdate(newFilm);
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
        }
        if (newFilm.getDuration() != null) {
            validate.validateUpdate(newFilm);
            oldFilm.setDuration(newFilm.getDuration());
        }
        if (newFilm.getMpa() != null) {
            oldFilm.setMpa(newFilm.getMpa());
        }
        if (newFilm.getGenres() != null) {
            oldFilm.setGenres(newFilm.getGenres());
        }
        log.info("Пользователь обновлен");
        return jdbcFilm.update(oldFilm);
    }

    @Override
    public void addLike(long filmId, long userId) {
        Film film = jdbcFilm.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден."));
        User user = jdbcUser.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден."));

        jdbcFilm.addLike(film.getId(), user.getId());
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        Film film = jdbcFilm.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден."));
        User user = jdbcUser.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден."));

        jdbcFilm.deleteLike(film.getId(), user.getId());
    }

    @Override
    public List<Integer> getLikes(long filmId) {
        Film film = jdbcFilm.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден."));
        return jdbcFilm.getLikes(film.getId());
    }

    @Override
    public Film getFilmWithGenre(long filmId) {
        return jdbcFilm.getFilmWithGenre(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден."));
    }

    @Override
    public List<Film> getTopPopularFilms(int count) {
        return jdbcFilm.getTopPopular(count);
    }
}
