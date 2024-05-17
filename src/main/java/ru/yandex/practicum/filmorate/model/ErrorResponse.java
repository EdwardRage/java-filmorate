package ru.yandex.practicum.filmorate.model;

import lombok.Getter;

@Getter
public class ErrorResponse {
    //private String error;
    private String description;

    public ErrorResponse(/*String error,*/ String description) {
        //this.error = error;
        this.description = description;
    }
}
