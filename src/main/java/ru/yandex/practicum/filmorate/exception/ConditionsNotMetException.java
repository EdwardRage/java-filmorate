package ru.yandex.practicum.filmorate.exception;

public class ConditionsNotMetException extends RuntimeException {
    public ConditionsNotMetException(String message) {
        super(message);
    }
    /*@Override
    public String getMessage() {
        return super.getMessage();
    }*/
}
