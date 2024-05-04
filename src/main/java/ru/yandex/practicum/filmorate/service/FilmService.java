package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.controller.validate.ValidateServiceImpl;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class FilmService {
    private final Map<Integer, Film> films = new HashMap<>();
    private final ValidateServiceImpl validate = new ValidateServiceImpl();
    private int id = 0;

    public Collection<Film> getFilms() {
        return films.values();
    }

    public Film create(Film film) {
        validate.validateCreate(film);
        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("Добавлен фильм");
        return film;
    }

    public Film updateFilm(@RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
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
            log.trace("Данные фильма обновлены");
            return oldFilm;
        } else {
            log.debug("Фильм не найден");
            throw new NotFoundException("Фильм с идентификатором id = " + newFilm.getId() + " не найден");
        }
    }

    private int generateId() {
        return ++id;
    }
}
