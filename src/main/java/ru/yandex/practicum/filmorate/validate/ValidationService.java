package ru.yandex.practicum.filmorate.validate;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

public interface ValidationService {

    void validateCreate(Film film);

    void validateCreate(User user);

    void validateUpdate(Film film);

    void validateUpdate(User user);
}
