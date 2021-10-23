package com.booster.dao;

import com.booster.dao.params.AddVocabularyDaoParams;
import com.booster.model.Vocabulary;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.util.List;

@Component
@RequiredArgsConstructor
public class VocabularyDao {

    public static final RowMapper<Vocabulary> rs2Vocabulary = (rs, i) -> Vocabulary.builder()
            .id(rs.getLong("id"))
            .createdAt(rs.getTimestamp("created_at"))
            .name(rs.getString("name"))
            .languageName(rs.getString("l_name"))
            .languageBeingLearnedId(rs.getLong("lbl_id"))
            .build();

    private final JdbcTemplate jdbcTemplate;

    public List<Vocabulary> findAll() {
        return jdbcTemplate.query(
                "select v.id, v.name, v.created_at, lbl.id as lbl_id, l.name as l_name from vocabulary v " +
                        "join language_being_learned lbl " +
                        "on v.language_being_learned_id = lbl.id " +
                        "join language l " +
                        "on lbl.language_id = l.id", rs2Vocabulary);
    }

    public void delete(long id) {
        jdbcTemplate.update("delete from vocabulary " +
                "where id = ?", id);
    }

    public long add(AddVocabularyDaoParams params) {
        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "insert into vocabulary " +
                            "(name, language_being_learned_id) " +
                            "values (?, ?)", new String[]{"id"});
            preparedStatement.setString(1, params.getName());
            preparedStatement.setLong(2, params.getLanguageBeingLearnedId());
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
                (rs, i) -> rs.getLong("id"), id);
    }

    public Vocabulary findById(Long id) {
        return jdbcTemplate.queryForObject(
                "select v.id, v.name, v.created_at, lbl.id as lbl_id, l.name as l_name from vocabulary v " +
                        "join language_being_learned lbl " +
                        "on v.language_being_learned_id = lbl.id " +
                        "join language l " +
                        "on lbl.language_id = l.id " +
                        "where v.id = ?", rs2Vocabulary, id);
    }

    public Vocabulary findByNameAndLanguageBeingLearnedId(String name, long id) {
        return jdbcTemplate.queryForObject(
                "select v.id, v.name, v.created_at, lbl.id as lbl_id, l.name as l_name from vocabulary v " +
                        "join language_being_learned lbl " +
                        "on v.language_being_learned_id = lbl.id " +
                        "join language l " +
                        "on lbl.language_id = l.id " +
                        "where v.name = ? " +
                        "and lbl.id = ?", rs2Vocabulary, name, id);
    }

}
