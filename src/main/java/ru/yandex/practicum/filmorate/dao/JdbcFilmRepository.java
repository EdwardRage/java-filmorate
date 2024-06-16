package ru.yandex.practicum.filmorate.dao;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class JdbcFilmRepository implements FilmRepository {

    private final NamedParameterJdbcOperations jdbc;

    @Override
    public Collection<Film> get() {
        String sql = "SELECT * FROM FILMS AS f "
                + "LEFT JOIN MPA AS m ON m.MPA_ID = f.MPA_ID";

        return jdbc.query(sql, this::makeFilm);
    }

    @Override
    public Optional<Film> getFilmById(long filmId) {

        String sql = "SELECT * FROM FILMS f WHERE FILM_ID = :filmId";
        SqlParameterSource params = new MapSqlParameterSource()
                .addValues(Map.of("filmId", filmId));

        try {
            Film film = jdbc.queryForObject(sql, params, this::makeFilm);
            return Optional.ofNullable(film);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Film create(Film film) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        String sql = "INSERT INTO FILMS (NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID) " +
        "VALUES (:name, :description, :releaseDate, :duration, :mpaId)";
        String sqlGenre = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES ( :filmId, :genreId)";

        SqlParameterSource params = new MapSqlParameterSource()
                .addValues(Map.of("name", film.getName()))
                .addValues(Map.of("description", film.getDescription()))
                .addValues(Map.of("releaseDate", film.getReleaseDate()))
                .addValues(Map.of("duration", film.getDuration()))
                .addValues(Map.of("mpaId", film.getMpa().getId()));
        try {
            jdbc.update(sql, params, keyHolder, new String[]{"FILM_ID"});


            Long filmId = keyHolder.getKeyAs(Long.class);
            film.setId(filmId);

            if (film.getGenres() != null) {
                List<SqlParameterSource> filmGenreBatch = film.getGenres().stream()
                        .map(genre -> new MapSqlParameterSource()
                                .addValue("filmId", filmId)
                                .addValue("genreId", genre.getId()))
                        .collect(Collectors.toList());
                jdbc.batchUpdate(sqlGenre, filmGenreBatch.toArray(new SqlParameterSource[0]));
            }
            return film;

        } catch (DataIntegrityViolationException e) {
            throw new ConditionsNotMetException("Введен неверный параметр");
        }
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE FILMS SET NAME = :NAME, DESCRIPTION = :DESCRIPTION, " +
                "RELEASE_DATE = :RELEASE_DATE, DURATION = :DURATION, MPA_ID = :MPA_ID";

        String sqlGenre = "UPDATE FILM_GENRE SET FILM_ID = :filmId, GENRE_ID = :genreID";

        SqlParameterSource params = new MapSqlParameterSource()
                .addValues(Map.of("MPA_ID", film.getId()))
                .addValues(Map.of("NAME", film.getName()))
                .addValues(Map.of("DESCRIPTION", film.getDescription()))
                .addValues(Map.of("RELEASE_DATE", film.getReleaseDate()))
                .addValues(Map.of("DURATION", film.getDuration()))
                .addValues(Map.of("MPA_ID", film.getMpa().getId()));

        jdbc.update(sql, params);
        Long filmId = film.getId();


        if (film.getGenres() != null) {
            List<SqlParameterSource> filmGenreBatch = film.getGenres().stream()
                    .map(genre -> new MapSqlParameterSource()
                            .addValue("filmId", filmId)
                            .addValue("genreId", genre.getId()))
                    .collect(Collectors.toList());
            jdbc.batchUpdate(sqlGenre, filmGenreBatch.toArray(new SqlParameterSource[0]));
        }
        return film;
    }

    @Override
    public void addLike(long filmId, long userId) {
        String sql = "INSERT INTO LIKES (FILM_ID, USER_ID) VALUES (:filmId, :userId)";
        SqlParameterSource params = new MapSqlParameterSource()
                .addValues(Map.of("filmId", filmId))
                .addValues(Map.of("userId", userId));
        jdbc.update(sql, params);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        String sql = "DELETE FROM LIKES WHERE FILM_ID = :filmId AND USER_ID = :userId";
        SqlParameterSource params = new MapSqlParameterSource()
                .addValues(Map.of("filmId", filmId))
                .addValues(Map.of("userId", userId));
        jdbc.update(sql, params);
    }

    @Override
    public Optional<Film> getFilmWithGenre(long filmId) {

        String sql = "SELECT f.*, m.MPA_NAME, g.* " +
                "FROM FILMS AS f " +
                "left join MPA AS m on f.MPA_ID = m.MPA_ID " +
                "left join FILM_GENRE AS fg on f.FILM_ID = fg.FILM_ID " +
                "left join GENRES g on g.GENRE_ID = fg.GENRE_ID " +
                "where f.FILM_ID = :filmId";

        SqlParameterSource params = new MapSqlParameterSource()
                .addValues(Map.of("filmId", filmId));
        try {

            Film film = jdbc.query(sql, params, new ResultSetExtractor<Film>() {
                @Override
                public Film extractData(@NonNull ResultSet rs) throws SQLException, DataAccessException {
                    Film resultFilm = new Film();
                    LinkedHashSet<Genre> genreSet = new LinkedHashSet<>();
                    while (rs.next()) {
                        resultFilm.setId(rs.getLong("film_id"));
                        resultFilm.setName(rs.getString("name"));
                        resultFilm.setDescription(rs.getString("description"));
                        resultFilm.setReleaseDate(rs.getDate("release_date").toLocalDate());
                        resultFilm.setDuration(rs.getInt("duration"));
                        resultFilm.setMpa(new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")));

                        Genre genre = Genre.builder()
                                .id(rs.getInt("genre_id"))
                                .name(rs.getString("genre_name"))
                                .build();
                        if (genre.getId() > 0) {
                            genreSet.add(genre);
                            resultFilm.setGenres(genreSet);
                        }
                    }
                    return resultFilm;
                }
            });
            return Optional.ofNullable(film);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Integer> getLikes(long filmId) {
        String sql = "SELECT COUNT(l.USER_ID) FROM LIKES AS l WHERE l.FILM_ID = :filmId";
        SqlParameterSource params = new MapSqlParameterSource()
                .addValues(Map.of("filmId", filmId));

        return jdbc.queryForList(sql,  params, Integer.class);

    }

    @Override
    public List<Film> getTopPopular(int count) {
        String sql = "SELECT f.*, COUNT(l.USER_ID) as likes FROM FILMS AS f " +
        "LEFT JOIN LIKES AS l ON f.film_id = l.film_id " +
        "GROUP BY f.FILM_ID " +
        "ORDER BY COUNT(l.USER_ID) DESC " +
        "LIMIT :count";
        return jdbc.query(sql, Map.of("count", count), this::makeFilm);
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        return film;
    }
}
