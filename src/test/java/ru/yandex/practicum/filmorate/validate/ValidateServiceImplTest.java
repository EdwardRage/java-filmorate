package ru.yandex.practicum.filmorate.validate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.validate.ValidateServiceImpl;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

public class ValidateServiceImplTest {
    private Film film;
    private User user;
    private final ValidateServiceImpl validate = new ValidateServiceImpl();

    @BeforeEach
    public void createFilmsForTests() {
        film = new Film();

        user = new User();

    }

    @Test
    public void validateFilmCreateWithoutName() {
        film.setDescription("description film");
        film.setReleaseDate(LocalDate.of(2024, Month.APRIL, 12));
        film.setDuration(180);

        ConditionsNotMetException exception = Assertions
                .assertThrows(ConditionsNotMetException.class, () -> {
                    validate.validateCreate(film);
                });
        Assertions.assertEquals("Название фильма должно быть указано", exception.getMessage());
    }

    @Test
    public void validateDescriptionMoreThanLimitCharacters() {
        film.setName("test film");
        film.setDescription("test description, test description, test description, test description, test description," +
                "test description, test description, test description, test description, test description, " +
                "test description,test description,test description,test description,test description," +
                "test description,test description,test description,test description,test description");
        film.setReleaseDate(LocalDate.of(2024, Month.APRIL, 12));
        film.setDuration(180);

        ConditionsNotMetException exception = Assertions
                .assertThrows(ConditionsNotMetException.class, () -> {
                    validate.validateCreate(film);
                });
        Assertions.assertEquals("Описание фильма не должно превышать 200 символов", exception.getMessage());
    }

    @Test
    public void validateCreateReleaseDateAfterBirthdayFilm() {
        film.setName("test film");
        film.setDescription("description film");
        film.setReleaseDate(LocalDate.of(1600, Month.DECEMBER, 27));
        film.setDuration(180);

        ConditionsNotMetException exception = Assertions
                .assertThrows(ConditionsNotMetException.class, () -> {
                    validate.validateCreate(film);
                });
        Assertions.assertEquals("Дата релиза фильма не можеть быть раньше 28 декабря 1895", exception.getMessage());
    }

    @Test
    public void validateDurationMovieIsNegative() {
        film.setName("test film");
        film.setDescription("description film");
        film.setReleaseDate(LocalDate.of(2024, Month.APRIL, 12));
        film.setDuration(-180);

        ConditionsNotMetException exception = Assertions
                .assertThrows(ConditionsNotMetException.class, () -> {
                    validate.validateCreate(film);
                });
        Assertions.assertEquals("Продолжительность фильма может быть только положительным числом", exception.getMessage());
    }

    @Test
    public void validateCreateUserWithoutEmail() {

        user.setLogin("TestLogin");
        user.setName("NoName");
        user.setBirthday(LocalDate.of(2000, Month.JANUARY, 1));

        ConditionsNotMetException exception = Assertions
                .assertThrows(ConditionsNotMetException.class, () -> {
                    validate.validateCreate(user);
                });
        Assertions.assertEquals("Электронная почта не может быть пустой и должна содержать символ @", exception.getMessage());
    }

    @Test
    public void validateCreateUserWithoutDogInEmail() {
        user.setEmail("testTest.ru");
        user.setLogin("TestLogin");
        user.setName("NoName");
        user.setBirthday(LocalDate.of(2000, Month.JANUARY, 1));

        ConditionsNotMetException exception = Assertions
                .assertThrows(ConditionsNotMetException.class, () -> {
                    validate.validateCreate(user);
                });
        Assertions.assertEquals("Электронная почта не может быть пустой и должна содержать символ @", exception.getMessage());
    }

    @Test
    public void validateCreateUserWithoutLogin() {
        user.setEmail("test@Test.ru");
        user.setLogin("");
        user.setName("NoName");
        user.setBirthday(LocalDate.of(2000, Month.JANUARY, 1));

        ConditionsNotMetException exception = Assertions
                .assertThrows(ConditionsNotMetException.class, () -> {
                    validate.validateCreate(user);
                });
        Assertions.assertEquals("Логин не должен быть пустым и содержать пробелы", exception.getMessage());
    }

    @Test
    public void validateCreateUserWithSpaceInLogin() {
        user.setEmail("test@Test.ru");
        user.setLogin("login test");
        user.setName("NoName");
        user.setBirthday(LocalDate.of(2000, Month.JANUARY, 1));

        ConditionsNotMetException exception = Assertions
                .assertThrows(ConditionsNotMetException.class, () -> {
                    validate.validateCreate(user);
                });
        Assertions.assertEquals("Логин не должен быть пустым и содержать пробелы", exception.getMessage());
    }

    @Test
    public void validateCreateBirthdayInFuture() {
        user.setEmail("test@Test.ru");
        user.setLogin("login");
        user.setName("NoName");
        user.setBirthday(LocalDate.of(2025, Month.JANUARY, 1));
        ConditionsNotMetException exception = Assertions
                .assertThrows(ConditionsNotMetException.class, () -> {
                    validate.validateCreate(user);
                });
        Assertions.assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
    }

    @Test
    public void validateFilmUpdateDescriptionMoreThanLimitSymbols() {
        film.setName("test film");
        film.setDescription("description film");
        film.setReleaseDate(LocalDate.of(2024, Month.APRIL, 12));
        film.setDuration(180);

        film.setDescription("test description, test description, test description, test description, test description," +
                "test description, test description, test description, test description, test description, " +
                "test description,test description,test description,test description,test description," +
                "test description,test description,test description,test description,test description");

        ConditionsNotMetException exception = Assertions
                .assertThrows(ConditionsNotMetException.class, () -> {
                    validate.validateUpdate(film);
                });
        Assertions.assertEquals("Описание фильма не должно превышать 200 символов", exception.getMessage());
    }

    @Test
    public void validateUpdateReleaseDateAfterBirthdayFilm() {
        film.setName("test film");
        film.setDescription("description film");
        film.setReleaseDate(LocalDate.of(1785, Month.DECEMBER, 27));
        film.setDuration(180);

        ConditionsNotMetException exception = Assertions
                .assertThrows(ConditionsNotMetException.class, () -> {
                    validate.validateUpdate(film);
                });
        Assertions.assertEquals("Дата релиза фильма не можеть быть раньше 28 декабря 1895", exception.getMessage());
    }

    @Test
    public void validateUpdateDurationMovieIsNegative() {
        film.setName("test film");
        film.setDescription("description film");
        film.setReleaseDate(LocalDate.of(2024, Month.APRIL, 12));
        film.setDuration(-180);

        ConditionsNotMetException exception = Assertions
                .assertThrows(ConditionsNotMetException.class, () -> {
                    validate.validateUpdate(film);
                });
        Assertions.assertEquals("Продолжительность фильма может быть только положительным числом", exception.getMessage());
    }

    @Test
    public void validateUpdateCreateUserWithoutDogInEmail() {
        user.setEmail("testTest.ru");
        user.setLogin("TestLogin");
        user.setName("NoName");
        user.setBirthday(LocalDate.of(2000, Month.JANUARY, 1));

        ConditionsNotMetException exception = Assertions
                .assertThrows(ConditionsNotMetException.class, () -> {
                    validate.validateUpdate(user);
                });
        Assertions.assertEquals("Электронная почта должна содержать символ @", exception.getMessage());
    }

    @Test
    public void validateUpdateCreateUserWithSpaceInLogin() {
        user.setEmail("test@Test.ru");
        user.setLogin("login test");
        user.setName("NoName");
        user.setBirthday(LocalDate.of(2000, Month.JANUARY, 1));

        ConditionsNotMetException exception = Assertions
                .assertThrows(ConditionsNotMetException.class, () -> {
                    validate.validateUpdate(user);
                });
        Assertions.assertEquals("Логин не должен содержать пробелы", exception.getMessage());
    }

    @Test
    public void validateUpdateCreateBirthdayInFuture() {
        user.setEmail("test@Test.ru");
        user.setLogin("login");
        user.setName("NoName");
        user.setBirthday(LocalDate.of(2025, Month.JANUARY, 1));
        ConditionsNotMetException exception = Assertions
                .assertThrows(ConditionsNotMetException.class, () -> {
                    validate.validateUpdate(user);
                });
        Assertions.assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
    }
}
