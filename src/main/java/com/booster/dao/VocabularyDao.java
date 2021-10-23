package com.booster.dao;

import com.booster.model.Vocabulary;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class VocabularyDao {

    private final JdbcTemplate jdbcTemplate;

    public List<Vocabulary> findAll() {
        return jdbcTemplate.query(
                "select v.id, v.name, v.created_at, lbl.id as lbl_id, l.name as l_name from vocabulary v " +
                        "join language_being_learned lbl " +
                        "on v.language_being_learned_id = lbl.id " +
                        "join language l " +
                        "on lbl.language_id = l.id",
                (rs, i) -> Vocabulary.builder()
                        .id(rs.getLong("id"))
                        .createdAt(rs.getTimestamp("created_at"))
                        .name(rs.getString("name"))
                        .languageName(rs.getString("l_name"))
                        .languageBeingLearnedId(rs.getLong("lbl_id"))
                        .build());
    }

    public void delete(long id) {
        jdbcTemplate.update("delete from vocabulary " +
                "where id = ?", id);
    }

    public long add(String name, long id) {
        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "insert into vocabulary " +
                            "(name, language_being_learned_id) " +
                            "values (?, ?)", new String[]{"id"});
            preparedStatement.setString(1, name);
            preparedStatement.setLong(2, id);
            return preparedStatement;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public boolean existsWithNameForLanguageBeingLearned(String name, long id) {
        Integer count = jdbcTemplate.queryForObject("select count(*) from (" +
                "select * from vocabulary v " +
                "join language_being_learned lbl " +
                "on v.language_being_learned_id = lbl.id " +
                "and v.name = ? " +
                "and lbl.id = ?) v_count", Integer.class, name, id);
        return count > 0;
    }

    public boolean existsWithId(long id) {
        Integer count = jdbcTemplate.queryForObject("select count(*) from vocabulary v " +
                "where v.id = ? ", Integer.class, id);
        return count > 0;
    }

    public List<Long> findAllIdsForLanguageBeingLearnedId(long id) {
        return jdbcTemplate.query("select v.id from vocabulary v " +
                        "where v.language_being_learned_id = ?",
                (rs, i) -> rs.getLong("id"),
                id
        );
    }

    public Optional<Vocabulary> findById(Long id) {
        try {
            Vocabulary vocabulary = jdbcTemplate.queryForObject(
                    "select v.id, v.name, v.created_at, lbl.id as lbl_id, l.name as l_name from vocabulary v " +
                            "join language_being_learned lbl " +
                            "on v.language_being_learned_id = lbl.id " +
                            "join language l " +
                            "on lbl.language_id = l.id " +
                            "where v.id = ?",
                    (rs, i) -> Vocabulary.builder()
                            .id(rs.getLong("id"))
                            .createdAt(rs.getTimestamp("created_at"))
                            .name(rs.getString("name"))
                            .languageName(rs.getString("l_name"))
                            .languageBeingLearnedId(rs.getLong("lbl_id"))
                            .build(),
                    id);

            return Optional.ofNullable(vocabulary);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Vocabulary> findByNameAndLanguageBeingLearnedId(String name, Long id) {
        try {
            Vocabulary vocabulary = jdbcTemplate.queryForObject(
                    "select v.id, v.name, v.created_at, lbl.id as lbl_id, l.name as l_name from vocabulary v " +
                            "join language_being_learned lbl " +
                            "on v.language_being_learned_id = lbl.id " +
                            "join language l " +
                            "on lbl.language_id = l.id " +
                            "where v.name = ? " +
                            "and lbl.id = ?",
                    (rs, i) -> Vocabulary.builder()
                            .id(rs.getLong("id"))
                            .createdAt(rs.getTimestamp("created_at"))
                            .name(rs.getString("name"))
                            .languageName(rs.getString("l_name"))
                            .languageBeingLearnedId(rs.getLong("lbl_id"))
                            .build(),
                    name, id);

            return Optional.ofNullable(vocabulary);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

}
