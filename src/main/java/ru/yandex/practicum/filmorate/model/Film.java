package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

/**
 * Film.
 */
@Data
@Component
public class Film {
    private long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private Mpa mpa;
    private Set<Genre> genre;
}



