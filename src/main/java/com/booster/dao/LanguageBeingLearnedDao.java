package com.booster.dao;

import com.booster.model.LanguageBeingLearned;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LanguageBeingLearnedDao {

    public static final RowMapper<LanguageBeingLearned> rs2LanguageBeingLearned = (rs, i) -> LanguageBeingLearned.builder()
            .id(rs.getLong("id"))
            .createdAt(rs.getTimestamp("created_at"))
            .languageName(rs.getString("name"))
            .build();

    private final JdbcTemplate jdbcTemplate;

    public List<LanguageBeingLearned> findAll() {
        return jdbcTemplate.query(
                "select lbl.id as id, l.name as name, lbl.created_at " +
                        "from language_being_learned lbl " +
                        "inner join language l " +
                        "on l.id = lbl.language_id", rs2LanguageBeingLearned);
    }

    public long add(long languageId) {
        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "insert into language_being_learned " +
                            "(language_id) " +
                            "values " +
                            "(?)", new String[]{"id"});
            preparedStatement.setLong(1, languageId);
            return preparedStatement;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public void delete(long id) {
        jdbcTemplate.update(
                "delete from language_being_learned " +
                        "where id = ?", id);
    }

    public boolean existsWithId(long id) {
        Integer count = jdbcTemplate.queryForObject(
                "select count(*) from language_being_learned lbl " +
                        "where lbl.id = ?", Integer.class, id);
        return count > 0;
    }

    public boolean existsWithLanguageId(long id) {
        Integer count = jdbcTemplate.queryForObject(
                "select count(*) from language_being_learned lbl " +
                        "where lbl.language_id = ?", Integer.class, id);
        return count > 0;
    }

    public LanguageBeingLearned findById(long id) {
        return jdbcTemplate.queryForObject(
                "select lbl.id as id, l.name as name, lbl.created_at " +
                        "from language_being_learned lbl " +
                        "inner join language l " +
                        "on l.id = lbl.language_id " +
                        "where lbl.id = ?", rs2LanguageBeingLearned, id);
    }

    public LanguageBeingLearned findByLanguageId(long id) {
        return jdbcTemplate.queryForObject(
                "select lbl.id as id, l.name as name, lbl.created_at " +
                        "from language_being_learned lbl " +
                        "inner join language l " +
                        "on l.id = lbl.language_id " +
                        "where l.id = ?", rs2LanguageBeingLearned, id);
    }

}
