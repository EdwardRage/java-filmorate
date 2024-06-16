package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmServiceImpl;

    @GetMapping
    public Collection<Film> getFilms() {
        return filmServiceImpl.get();
    }

    @GetMapping("/{filmId}")
    public Film getFilmWithGenre(@PathVariable long filmId) {
        return filmServiceImpl.getFilmWithGenre(filmId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@RequestBody Film film) {
        return filmServiceImpl.create(film);
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        return filmServiceImpl.update(newFilm);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void addLike(@PathVariable int filmId, @PathVariable int userId) {
        filmServiceImpl.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void deleteLike(@PathVariable int filmId, @PathVariable int userId) {
        filmServiceImpl.deleteLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getMostPopularFilms(@RequestParam(required = false) Integer count) {
        return filmServiceImpl.getTopPopularFilms(count);
    }
}
