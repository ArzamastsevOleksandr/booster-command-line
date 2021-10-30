package com.booster.dao;

import com.booster.model.Language;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

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

}
