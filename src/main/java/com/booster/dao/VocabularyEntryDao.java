package com.booster.dao;

import com.booster.model.VocabularyEntry;
import com.booster.model.Word;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class VocabularyEntryDao {

    private final JdbcTemplate jdbcTemplate;

    // todo: one sql query?
    public List<VocabularyEntry> findAll() {
        return jdbcTemplate.query(
                "select ve.id as ve_id, ve.created_at as ve_created_at, ve.correct_answers_count as ve_cac, " +
                        "w.id as w_id, w.name as w_name, " +
                        "v.name as v_name " +
                        "from vocabulary_entry ve " +
                        "join word w " +
                        "on ve.word_id = w.id " +
                        "join vocabulary v " +
                        "on ve.vocabulary_id = v.id",
                (rs, i) -> VocabularyEntry.builder()
                        .id(rs.getLong("ve_id"))
                        .createdAt(rs.getTimestamp("ve_created_at"))
                        .correctAnswersCount(rs.getInt("ve_cac"))
                        .vocabularyName(rs.getString("v_name"))
                        .word(Word.builder()
                                .id(rs.getLong("w_id"))
                                .name(rs.getString("w_name"))
                                .build())
                        .build());
    }

    public void delete(long id) {
        jdbcTemplate.update("delete from vocabulary_entry " +
                "where id = ?", id);
    }

    public void add(long wordId, long vocabularyId, List<Long> synonymIds) {
        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "insert into vocabulary_entry " +
                            "(word_id, vocabulary_id) " +
                            "values (?, ?)", new String[]{"id"});
            preparedStatement.setLong(1, wordId);
            preparedStatement.setLong(2, vocabularyId);
            return preparedStatement;
        }, keyHolder);
        jdbcTemplate.batchUpdate(
                "insert into vocabulary_entry__synonym " +
                        "(vocabulary_entry_id, word_id) " +
                        "values (?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Long wordId = synonymIds.get(i);
                        ps.setLong(1, keyHolder.getKey().longValue());
                        ps.setLong(2, wordId);
                    }

                    @Override
                    public int getBatchSize() {
                        return synonymIds.size();
                    }
                });
    }

}
