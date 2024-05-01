package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.validate.ValidateServiceImpl;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    private int id = 0;

    private final ValidateServiceImpl validate = new ValidateServiceImpl();

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {

        validate.validateCreate(user);

        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }

        user.setId(generateId());
        users.put(user.getId(), user);
        log.trace("Создан пользователь {}", user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            if (newUser.getName() != null) {
                oldUser.setName(newUser.getName());
            }
            if (newUser.getBirthday() != null) {
                validate.validateUpdate(newUser);
                oldUser.setBirthday(newUser.getBirthday());
            }
            if (newUser.getLogin() != null) {
                validate.validateUpdate(newUser);
                checkLogin(newUser);
                oldUser.setLogin(newUser.getLogin());
            }
            if (newUser.getEmail() != null) {
                validate.validateUpdate(newUser);
                checkEmail(newUser);
                oldUser.setEmail(newUser.getEmail());
            }
            log.trace("Данные пользователя обновлены");
            return oldUser;
        }
        log.debug("Пользователь не найден");
        throw new NotFoundException("Пользователь с Id = " + newUser.getId() + " не найден");
    }

    private int generateId() {
        return ++id;
    }

    private void checkLogin(User newUser) {
        for (Integer id : users.keySet()) {
            if (newUser.getLogin().equals(users.get(id).getLogin())) {
                log.debug("Попытка присводить занятый логин");
                throw new DuplicatedDataException("Логин не может повторяться");
            }
        }
    }

    private void checkEmail(User newUser) {
        for (Integer id : users.keySet()) {
            if (newUser.getEmail().equals(users.get(id).getEmail())) {
                log.debug("Попытка присвоить занятый имейл");
                throw new DuplicatedDataException("Имейл не может повторяться");
            }
        }
    }
}
