package com.booster.dao;

import com.booster.model.Language;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LanguageDao {

    private final JdbcTemplate jdbcTemplate;

    public List<Language> findAll() {
        return jdbcTemplate.query("select * from Language", (rs, i) -> Language.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .build());
    }

    public boolean existsWithId(long id) {
        Integer count = jdbcTemplate.queryForObject("select count(*) from language l " +
                "where l.id = ?", Integer.class, id);

        return count > 0;
    }

    public Optional<Language> findByName(String name) {
        try {
            Language language = jdbcTemplate.queryForObject("select * from language l " +
                            "where l.name = ?", (rs, i) -> Language.builder()
                            .id(rs.getLong("id"))
                            .name(rs.getString("name"))
                            .build(),
                    name);
            return Optional.ofNullable(language);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

}
