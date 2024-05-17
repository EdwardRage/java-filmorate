package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component
public class FilmStorageImpl implements FilmStorage {
    private final Map<Long, Film> filmsMap = new HashMap<>();

    private final Map<Long, Set<Long>> likesMap = new HashMap<>();
    private long id = 0;

    @Override
    public Collection<Film> get() {
        return filmsMap.values();
    }

    @Override
    public Optional<Film> getFilmById(long filmId) {
        return Optional.ofNullable(filmsMap.get(filmId));
    }

    @Override
    public Film create(Film film) {
        film.setId(generateId());
        filmsMap.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        filmsMap.put(film.getId(), film);
        return film;
    }

    @Override
    public void addLike(Film film, User user) {
        Set<Long> likesUserId = likesMap.computeIfAbsent(film.getId(), id -> new HashSet<>());
        likesUserId.add(user.getId());
    }

    @Override
    public void deleteLike(Film film, User user) {
        Set<Long> likesUserId = likesMap.computeIfAbsent(film.getId(), id -> new HashSet<>());
        likesUserId.remove(user.getId());
    }

    @Override
    public Set<Long> getLikes(Film film) {
        return likesMap.get(film.getId());
    }

    private long generateId() {
        return ++id;
    }

}
