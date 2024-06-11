package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaRepository {

    Mpa getMpaById(int id);

    List<Mpa> getAllMpa();
    /*Mpa createMpa(Mpa mpa);
    Mpa deleteMpa(Mpa mpa);*/
}
