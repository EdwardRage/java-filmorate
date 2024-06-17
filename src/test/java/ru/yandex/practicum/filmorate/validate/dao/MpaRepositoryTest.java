package ru.yandex.practicum.filmorate.validate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.MpaRepository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaRepositoryTest {
    private final MpaRepository mpaRepository;

    @Test
    void getMpaByIdTest() {
        Mpa mpa1 = mpaRepository.getMpaById(1).orElseThrow();
        Mpa mpa2 = mpaRepository.getMpaById(2).orElseThrow();
        Mpa mpa3 = mpaRepository.getMpaById(3).orElseThrow();

        assertEquals("G", mpa1.getName());
        assertEquals("PG", mpa2.getName());
        assertEquals("PG-13", mpa3.getName());
    }

    @Test
    void getAllMpaTest() {
        List<Mpa> mpa = mpaRepository.getAllMpa();

        assertEquals(5, mpa.size());
    }
}
