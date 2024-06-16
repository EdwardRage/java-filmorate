package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@RequiredArgsConstructor
@Repository
public class JdbcUserRepository implements UserRepository {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public Collection<User> get() {
        String sql = "SELECT * FROM USERS";

        return jdbc.query(sql, this::makeUser);
    }

    @Override
    public Optional<User> getUserById(long userId) {

        String sql = "SELECT * FROM USERS WHERE USER_ID = :userId";
        SqlParameterSource params = new MapSqlParameterSource()
                .addValues(Map.of("userId", userId));

        try {
            User user = jdbc.queryForObject(sql, params, this::makeUser);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public User create(User user) {

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY) " +
                "VALUES ( :EMAIL, :LOGIN, :NAME,  :BIRTHDAY)";

        SqlParameterSource params = new MapSqlParameterSource()
                .addValues(Map.of("EMAIL", user.getEmail()))
                .addValues(Map.of("LOGIN", user.getLogin()))
                .addValues(Map.of("NAME", user.getName()))
                .addValues(Map.of("BIRTHDAY", user.getBirthday()));

        jdbc.update(sql, params, keyHolder, new String[]{"USER_ID"});

        user.setId(keyHolder.getKeyAs(Long.class));
        return user;
    }

    @Override
    public User update(User user) {

        String sql = "UPDATE USERS SET EMAIL = :EMAIL, LOGIN = :LOGIN, NAME = :NAME, BIRTHDAY = :BIRTHDAY " +
        "WHERE USER_ID = :userId";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValues(Map.of("userId", user.getId()))
                .addValues(Map.of("EMAIL", user.getEmail()))
                .addValues(Map.of("LOGIN", user.getLogin()))
                .addValues(Map.of("NAME", user.getName()))
                .addValues(Map.of("BIRTHDAY", user.getBirthday()));

        jdbc.update(sql, params);

        return user;
    }

    @Override
    public void addFriend(long userId, long friendId) {
        String sql = "INSERT INTO FRIENDS (USER_ID, FRIEND_ID) VALUES ( :userId, :friendId)";
        jdbc.update(sql, Map.of("userId", userId,"friendId", friendId));
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        String sql = "DELETE FROM FRIENDS WHERE USER_ID = :userId AND FRIEND_ID = :friendId";
        jdbc.update(sql, Map.of("userId", userId, "friendId", friendId));
    }

    public List<User> getFriends(long userId) {
        String sql = "SELECT u.* FROM FRIENDS AS f JOIN USERS AS u ON f.FRIEND_ID = u.USER_ID WHERE f.USER_ID = :userId";

        return jdbc.query(sql, Map.of("userId", userId), this::makeUser);
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {

        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());

        return user;
    }
}
