package com.booster.dao;

import com.booster.model.LanguageBeingLearned;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LanguageBeingLearnedDao {

    private final JdbcTemplate jdbcTemplate;

    public List<LanguageBeingLearned> findAll() {
        return jdbcTemplate.query(
                "select lbl.id as id, l.name as name, lbl.created_at " +
                        "from language_being_learned lbl " +
                        "inner join language l " +
                        "on l.id = lbl.language_id",
                (rs, i) -> LanguageBeingLearned.builder()
                        .id(rs.getLong("id"))
                        .createdAt(rs.getTimestamp("created_at"))
                        .languageName(rs.getString("name"))
                        .build());
    }

    public void add(long languageId) {
        jdbcTemplate.update("insert into language_being_learned " +
                "(language_id) " +
                "values " +
                "(?)", languageId);
    }

    public void delete(long id) {
        jdbcTemplate.update("delete from language_being_learned " +
                "where id = ?", id);
    }

    public boolean existsWithId(long id) {
        Integer count = jdbcTemplate.queryForObject("select count(*) from language_being_learned lbl " +
                "where lbl.id = ?", Integer.class, id);
        return count > 0;
    }

    public boolean existsWithLanguageId(long id) {
        Integer count = jdbcTemplate.queryForObject("select count(*) from language_being_learned lbl " +
                "where lbl.language_id = ?", Integer.class, id);
        return count > 0;
    }

    public Optional<LanguageBeingLearned> findById(Long id) {
        try {
            var languageBeingLearned = jdbcTemplate.queryForObject(
                    "select lbl.id as id, l.name as name, lbl.created_at " +
                            "from language_being_learned lbl " +
                            "inner join language l " +
                            "on l.id = lbl.language_id " +
                            "where lbl.id = ?",
                    (rs, i) -> LanguageBeingLearned.builder()
                            .id(rs.getLong("id"))
                            .createdAt(rs.getTimestamp("created_at"))
                            .languageName(rs.getString("name"))
                            .build(),
                    id);

            return Optional.ofNullable(languageBeingLearned);
        } catch (DataAccessException e) {
            // todo: exception handling
            return Optional.empty();
        }
    }

}
