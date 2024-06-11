package ru.yandex.practicum.filmorate.exception;

import org.springframework.dao.DataAccessException;

public class NotFoundException extends DataAccessException {
    public NotFoundException(String message) {
        super(message);
    }
}
