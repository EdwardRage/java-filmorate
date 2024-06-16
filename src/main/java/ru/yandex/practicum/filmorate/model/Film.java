package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.LinkedHashSet;

/**
 * Film.
 */
@Data
public class Film {
    private long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private Mpa mpa;
    private LinkedHashSet<Genre> genres = new LinkedHashSet<>();
}



