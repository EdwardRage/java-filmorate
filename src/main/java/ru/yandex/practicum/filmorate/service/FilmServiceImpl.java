package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.FilmRepository;
import ru.yandex.practicum.filmorate.dao.UserRepository;
import ru.yandex.practicum.filmorate.validate.ValidateServiceImpl;
import ru.yandex.practicum.filmorate.validate.ValidationService;

import java.util.*;
import java.util.stream.Collectors;

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
        return jdbcFilm.create(oldFilm);
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
    public List<Film> getTopPopularFilms(int count) {
        if (count == 0) {
            count = 10;
        }

        Map<Long, Integer> likesMap = new HashMap<>();
        for (Film film : jdbcFilm.get()) {
            if (jdbcFilm.getLikes(film.getId()) != null) {
                int setSize = jdbcFilm.getLikes(film.getId()).size();
                likesMap.put(film.getId(), setSize);
            }
        }

        Map<Long, Integer> sortedMap = likesMap.entrySet()
                .stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));

        List<Film> popularFilms = new ArrayList<>();
        for (Long sortedFilmIdD : sortedMap.keySet()) {
            Film film = jdbcFilm.getFilmById(sortedFilmIdD)
                    .orElseThrow(() -> new NotFoundException("Фильм с идентификатором id = "
                            + sortedFilmIdD + " не найден"));
            popularFilms.add(film);
        }
        return popularFilms.stream().limit(count).collect(Collectors.toList());
    }
}
