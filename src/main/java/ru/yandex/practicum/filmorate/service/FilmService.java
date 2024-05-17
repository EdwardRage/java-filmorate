package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validate.ValidateServiceImpl;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    private final ValidateServiceImpl validate = new ValidateServiceImpl();

    public Collection<Film> get() {
        return filmStorage.get();
    }

    public Film getFilmById(int filmId) {
        return filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с идентификатором id = " + filmId + " не найден"));
    }

    public Film create(Film film) {
        validate.validateCreate(film);
        return filmStorage.create(film);
    }

    public Film update(Film newFilm) {
        Film oldFilm = filmStorage.getFilmById(newFilm.getId())
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
        return filmStorage.updateFilm(oldFilm);
    }

    public void addLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден."));
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден."));

        filmStorage.addLike(film, user);
    }

    public void deleteLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден."));
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден."));

        filmStorage.deleteLike(film, user);
    }

    public List<Film> getMostPopularFilms(Integer count) {
        if (count == 0) {
            count = 10;
        }

        Map<Long, Integer> likesMap = new HashMap<>();
        for (Film film : filmStorage.get()) {
            if (filmStorage.getLikes(film) != null) {
                int setSize = filmStorage.getLikes(film).size();
                likesMap.put(film.getId(), setSize);
            }
        }

        Map<Long, Integer> sortedMap = likesMap.entrySet()
                .stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));

        List<Film> popularFilms = new ArrayList<>();
        for (Long sortedFilmIdD : sortedMap.keySet()) {
            Film film = filmStorage.getFilmById(sortedFilmIdD)
                    .orElseThrow(() -> new NotFoundException("Фильм с идентификатором id = "
                            + sortedFilmIdD + " не найден"));
            popularFilms.add(film);
        }
        return popularFilms.stream().limit(count).collect(Collectors.toList());
    }
}
