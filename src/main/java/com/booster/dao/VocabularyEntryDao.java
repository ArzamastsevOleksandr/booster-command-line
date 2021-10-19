package com.booster.dao;

import com.booster.model.VocabularyEntry;
import com.booster.model.Word;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class VocabularyEntryDao {

    private final JdbcTemplate jdbcTemplate;

    // todo: one sql query?
    public List<VocabularyEntry> findAll() {
        List<VocabularyEntry> vocabularyEntries = jdbcTemplate.query(
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
        var veId2Synonyms = jdbcTemplate.query(
                "select ve.id as ve_id, w.name as synonym from vocabulary_entry__synonym__jt ves " +
                        "join word w on ves.word_id = w.id " +
                        "join vocabulary_entry ve on ves.vocabulary_entry_id = ve.id",
                createResultSetExtractor("synonym"));

        var veId2Antonyms = jdbcTemplate.query(
                "select ve.id as ve_id, w.name as antonym from vocabulary_entry__antonym__jt vea " +
                        "join word w on vea.word_id = w.id " +
                        "join vocabulary_entry ve on vea.vocabulary_entry_id = ve.id",
                createResultSetExtractor("antonym"));

        for (var ve : vocabularyEntries) {
            ve.setSynonyms(veId2Synonyms.get(ve.getId()));
            ve.setAntonyms(veId2Antonyms.get(ve.getId()));
        }
        return vocabularyEntries;
    }

    public void delete(long id) {
        jdbcTemplate.update("delete from vocabulary_entry " +
                "where id = ?", id);
    }

    public void add(long wordId, long vocabularyId, List<Long> synonymIds, List<Long> antonymIds) {
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
        long vocabularyEntryId = keyHolder.getKey().longValue();
        jdbcTemplate.batchUpdate(
                "insert into vocabulary_entry__synonym__jt " +
                        "(vocabulary_entry_id, word_id) " +
                        "values (?, ?)",
                createBatchPreparedStatementSetter(synonymIds, vocabularyEntryId));
        jdbcTemplate.batchUpdate(
                "insert into vocabulary_entry__antonym__jt " +
                        "(vocabulary_entry_id, word_id) " +
                        "values (?, ?)",
                createBatchPreparedStatementSetter(antonymIds, vocabularyEntryId));
    }

    private ResultSetExtractor<Map<Long, Set<String>>> createResultSetExtractor(String columnName) {
        return rs -> {
            Map<Long, Set<String>> veId2Values = new HashMap<>();
            while (rs.next()) {
                veId2Values.computeIfAbsent(
                        rs.getLong("ve_id"),
                        k -> new HashSet<>()).add(rs.getString(columnName)
                );
            }
            return veId2Values;
        };
    }

    private BatchPreparedStatementSetter createBatchPreparedStatementSetter(List<Long> ids, Long vocabularyEntryId) {
        return new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Long wordId = ids.get(i);
                ps.setLong(1, vocabularyEntryId);
                ps.setLong(2, wordId);
            }

            @Override
            public int getBatchSize() {
                return ids.size();
            }
        };
    }

    public void updateCorrectAnswersCount(long id, int cacUpdated) {
        jdbcTemplate.update("update vocabulary_entry ve " +
                "set correct_answers_count = ? " +
                "where ve.id = ?", cacUpdated, id);
    }

    public boolean existsWithId(long id) {
        Integer count = jdbcTemplate.queryForObject("select count(*) from vocabulary_entry ve " +
                "where ve.id = ?", Integer.class, id);
        return count > 0;
    }

}
