package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.JdbcMpaRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Service
@AllArgsConstructor
public class MpaServiceImpl implements MpaService {
    private final JdbcMpaRepository jdbcMpa;

    @Override
    public Mpa getMpaById(int id) {
        return jdbcMpa.getMpaById(id)
                .orElseThrow(() -> new NotFoundException("MPA с идентификатором id = " + id + " не найден"));
    }

    @Override
    public List<Mpa> getAllMpa() {
        return jdbcMpa.getAllMpa();
    }
}
