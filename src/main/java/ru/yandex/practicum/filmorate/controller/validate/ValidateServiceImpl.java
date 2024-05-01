package ru.yandex.practicum.filmorate.controller.validate;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

@Slf4j
public class ValidateServiceImpl implements ValidationService {
    private final LocalDate movieBirthday = LocalDate.of(1895, Month.DECEMBER, 28);

    @Override
    public void validateCreate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.debug("Не указано название фильма");
            throw new ConditionsNotMetException("Название фильма должно быть указано");
        }
        if (film.getDescription().length() > 200) {
            log.debug("Описание фильма больше 200 символов");
            throw new ConditionsNotMetException("Описание фильма не должно превышать 200 символов");
        }
        if (film.getReleaseDate().isBefore(movieBirthday)) {
            log.debug("Дата релиза раньше 28 декабря 1985");
            throw new ConditionsNotMetException("Дата релиза фильма не можеть быть раньше 28 декабря 1985");
        }
        if (film.getDuration() <= 0) {
            log.debug("В продолжительности фильма указано отрицательное число");
            throw  new ConditionsNotMetException("Продолжительность фильма может быть только положительным числом");
        }
    }

    @Override
    public void validateCreate(User user) {

        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            log.debug("Указан пустой имейл или без символа @");
            throw new ConditionsNotMetException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.debug("Указан постой логин или логин с пробелами");
            throw new ConditionsNotMetException("Логин не должен быть пустым и содержать пробелы");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.debug("Указана дата рождения из будущего");
            throw new ConditionsNotMetException("Дата рождения не может быть в будущем");
        }
    }

    @Override
    public void validateUpdate(Film newFilm) {

        if (newFilm.getDescription() != null) {
            if (newFilm.getDescription().length() > 200) {
                log.debug("Описание фильма больше 200 символов");
                throw new ConditionsNotMetException("Описание фильма не должно превышать 200 символов");
            }
        }
        if (newFilm.getReleaseDate() != null) {
            if (newFilm.getReleaseDate().isBefore(movieBirthday)) {
                log.debug("Дата релиза укана ранее 28 декабря 1985");
                throw new ConditionsNotMetException("Дата релиза фильма не можеть быть раньше 28 декабря 1985");
            }
        }
        if (newFilm.getDuration() != null) {
            if (newFilm.getDuration() <= 0) {
                log.debug("Указано отрицательное число в продолжительности фильма");
                throw  new ConditionsNotMetException("Продолжительность фильма может быть только положительным числом");
            }
        }
    }

    @Override
    public void validateUpdate(User newUser) {
        if (newUser.getBirthday() != null) {
            if (newUser.getBirthday().isAfter(LocalDate.now())) {
                log.debug("Указана дата рождения из будущего");
                throw new ConditionsNotMetException("Дата рождения не может быть в будущем");
            }
        }
        if (newUser.getLogin() != null) {
            if (newUser.getLogin().contains(" ")) {
                log.debug("Логин содержит пробелы");
                throw new ConditionsNotMetException("Логин не должен содержать пробелы");
            }
        }
        if (newUser.getEmail() != null) {
            if (!newUser.getEmail().contains("@")) {
                log.debug("В имейле отстутствует символ @");
                throw new ConditionsNotMetException("Электронная почта должна содержать символ @");
            }
        }
    }
}
