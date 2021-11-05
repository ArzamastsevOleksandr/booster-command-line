package com.booster.dao;

import com.booster.model.Language;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LanguageDao {

    public static final RowMapper<Language> rs2Language = (rs, i) -> Language.builder()
            .id(rs.getLong("id"))
            .name(rs.getString("name"))
            .build();

    private final JdbcTemplate jdbcTemplate;

    public List<Language> findAll() {
        return jdbcTemplate.query(
                "select * " +
                        "from language", rs2Language);
    }

    public int countWithId(long id) {
        return jdbcTemplate.queryForObject(
                "select count(*) " +
                        "from language l " +
                        "where l.id = ?", Integer.class, id);
    }

    public Language findByName(String name) {
        return jdbcTemplate.queryForObject(
                "select * " +
                        "from language l " +
                        "where l.name = ?", rs2Language, name);
    }

    public int countWithName(String name) {
        return jdbcTemplate.queryForObject(
                "select count(*) " +
                        "from language l " +
                        "where l.name = ?", Integer.class, name);
    }

    public long add(String name) {
        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "insert into language " +
                            "(name) " +
                            "values (?)", new String[]{"id"});
            ps.setString(1, name);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public void delete(Long id) {
        jdbcTemplate.update(
                "delete from language " +
                        "where id = ?", id);
    }

}
