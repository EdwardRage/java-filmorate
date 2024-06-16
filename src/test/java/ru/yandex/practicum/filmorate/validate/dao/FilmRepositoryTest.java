package ru.yandex.practicum.filmorate.validate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.FilmRepository;
import ru.yandex.practicum.filmorate.dao.UserRepository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmRepositoryTest {
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;

    Film film1 = new Film();
    Film film2 = new Film();
    Film film3 = new Film();
    User user1 = new User();
    User user2 = new User();

    @BeforeEach
    void createFilmForTest() {
        film1.setName("film1");
        film1.setDescription("description film1");
        film1.setDuration(120);
        film1.setReleaseDate(LocalDate.of(1999, 12,12));
        film1.setMpa(new Mpa(1, "G"));

        film2.setName("film2");
        film2.setDescription("description film2");
        film2.setDuration(120);
        film2.setReleaseDate(LocalDate.of(1999, 12,12));
        film2.setMpa(new Mpa(2, "PG"));

        film3.setName("film3");
        film3.setDescription("description film3");
        film3.setDuration(120);
        film3.setReleaseDate(LocalDate.of(1999, 12,12));
        film3.setMpa(new Mpa(1, "PG-13"));

        user1.setName("user1");
        user1.setEmail("user1@mail.ru");
        user1.setLogin("user1login");
        user1.setBirthday(LocalDate.of(2000, 12,12));

        user2.setName("user2");
        user2.setEmail("user2@mail.ru");
        user2.setLogin("user2login");
        user2.setBirthday(LocalDate.of(2000, 12,12));

        filmRepository.create(film1);
        filmRepository.create(film2);
        filmRepository.create(film3);

        userRepository.create(user1);
        userRepository.create(user2);
    }

    @Test
    void getFilmByIdTest() {
        Film film = filmRepository.getFilmById(1).orElseThrow();

        assertEquals(film1.getName(), film.getName());
    }

    @Test
    void updateFilmTest() {
        film2.setName("Green elephant");
        film2.setDescription("update description film2");

        filmRepository.update(film2);

        assertEquals(film2.getDescription(), filmRepository.getFilmById(2).orElseThrow().getDescription());
    }

    @Test
    void addLikeTest() {
        filmRepository.addLike(film1.getId(), user1.getId());

        List<Integer> likes = filmRepository.getLikes(film1.getId());

        assertEquals(1, likes.size());
    }

    @Test
    void deleteLikeTest() {
        filmRepository.addLike(film1.getId(), user1.getId());
        filmRepository.addLike(film1.getId(), user2.getId());
        filmRepository.deleteLike(film1.getId(), user1.getId());

        List<Integer> likes = filmRepository.getLikes(film1.getId());

        assertEquals(1, likes.size());
    }

    @Test
    void getTopPopularTest() {
        filmRepository.addLike(film1.getId(), user1.getId());
        filmRepository.addLike(film1.getId(), user2.getId());

        filmRepository.addLike(film2.getId(), user2.getId());

        //filmRepository.getTopPopular(5);

        assertEquals(3, filmRepository.getTopPopular(3).size());
    }
}
