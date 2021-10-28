package com.booster.dao;

import com.booster.dao.params.AddVocabularyEntryDaoParams;
import com.booster.model.VocabularyEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
public class VocabularyEntryDao {

    public static final RowMapper<VocabularyEntry> rs2VocabularyEntry = (rs, i) -> VocabularyEntry.builder()
            .id(rs.getLong("ve_id"))
            .createdAt(rs.getTimestamp("created_at"))
            .correctAnswersCount(rs.getInt("cac"))
            .name(rs.getString("w_name"))
            .definition(rs.getString("definition"))
            .vocabularyName(rs.getString("v_name"))
            .build();

    private final JdbcTemplate jdbcTemplate;

    // todo: one sql query?
    // todo: when pagination is ready, implement DRY
    public List<VocabularyEntry> findAll() {
        List<VocabularyEntry> vocabularyEntries = jdbcTemplate.query(
                "select ve.id as ve_id, ve.created_at, ve.correct_answers_count as cac, ve.definition as definition, " +
                        "w.name as w_name, " +
                        "v.name as v_name " +
                        "from vocabulary_entry ve " +
                        "join word w " +
                        "on ve.word_id = w.id " +
                        "join vocabulary v " +
                        "on ve.vocabulary_id = v.id", rs2VocabularyEntry);

        var veId2Synonyms = jdbcTemplate.query(
                "select ve.id as ve_id, w.name as synonym " +
                        "from vocabulary_entry__synonym__jt ves " +
                        "join word w " +
                        "on ves.word_id = w.id " +
                        "join vocabulary_entry ve " +
                        "on ves.vocabulary_entry_id = ve.id",
                createResultSetExtractor("synonym"));

        var veId2Antonyms = jdbcTemplate.query(
                "select ve.id as ve_id, w.name as antonym " +
                        "from vocabulary_entry__antonym__jt vea " +
                        "join word w " +
                        "on vea.word_id = w.id " +
                        "join vocabulary_entry ve " +
                        "on vea.vocabulary_entry_id = ve.id",
                createResultSetExtractor("antonym"));

        return vocabularyEntries.stream()
                .map(withSynonymsAndAntonyms(veId2Synonyms, veId2Antonyms))
                .collect(toList());
    }

    // todo: FunctionalInterface
    private Function<VocabularyEntry, VocabularyEntry> withSynonymsAndAntonyms(Map<Long, Set<String>> veId2Synonyms, Map<Long, Set<String>> veId2Antonyms) {
        return ve -> {
            var builder = ve.toBuilder();
            if (veId2Synonyms.containsKey(ve.getId())) {
                builder = builder.synonyms(veId2Synonyms.get(ve.getId()));
            }
            if (veId2Antonyms.containsKey(ve.getId())) {
                builder = builder.antonyms(veId2Antonyms.get(ve.getId()));
            }
            return builder.build();
        };
    }

    public void delete(long id) {
        jdbcTemplate.update(
                "delete from vocabulary_entry " +
                        "where id = ?", id);
    }

    public void addWithDefaultValues(AddVocabularyEntryDaoParams params) {
        add(params, connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "insert into vocabulary_entry " +
                            "(word_id, vocabulary_id, definition) " +
                            "values (?, ?, ?)", new String[]{"id"});
            ps.setLong(1, params.getWordId());
            ps.setLong(2, params.getVocabularyId());
            ps.setString(3, params.getDefinition());
            return ps;
        });
    }

    public void addWithAllValues(AddVocabularyEntryDaoParams params) {
        add(params, connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "insert into vocabulary_entry " +
                            "(word_id, vocabulary_id, definition, created_at, correct_answers_count) " +
                            "values (?, ?, ?, ?, ?)", new String[]{"id"});
            ps.setLong(1, params.getWordId());
            ps.setLong(2, params.getVocabularyId());
            ps.setString(3, params.getDefinition());
            ps.setTimestamp(4, params.getCreatedAt());
            ps.setInt(5, params.getCorrectAnswersCount());
            return ps;
        });
    }

    private void add(AddVocabularyEntryDaoParams params, PreparedStatementCreator preparedStatementCreator) {
        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(preparedStatementCreator, keyHolder);

        long vocabularyEntryId = keyHolder.getKey().longValue();
        jdbcTemplate.batchUpdate(
                "insert into vocabulary_entry__synonym__jt " +
                        "(vocabulary_entry_id, word_id) " +
                        "values (?, ?)",
                createBatchPreparedStatementSetter(params.getSynonymIds(), vocabularyEntryId));
        jdbcTemplate.batchUpdate(
                "insert into vocabulary_entry__antonym__jt " +
                        "(vocabulary_entry_id, word_id) " +
                        "values (?, ?)",
                createBatchPreparedStatementSetter(params.getAntonymIds(), vocabularyEntryId));
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

    private BatchPreparedStatementSetter createBatchPreparedStatementSetter(List<Long> ids, long vocabularyEntryId) {
        return new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                long wordId = ids.get(i);
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
        jdbcTemplate.update(
                "update vocabulary_entry ve " +
                        "set correct_answers_count = ? " +
                        "where ve.id = ?", cacUpdated, id);
    }

    // todo: execute all updates in transaction
    public void deleteAllForVocabularyId(long id) {
        jdbcTemplate.update(
                "delete from vocabulary_entry__synonym__jt sjt " +
                        "where sjt.vocabulary_entry_id = ?", id);
        jdbcTemplate.update(
                "delete from vocabulary_entry__antonym__jt ajt " +
                        "where ajt.vocabulary_entry_id = ?", id);
        jdbcTemplate.update(
                "delete from vocabulary_entry ve " +
                        "where ve.vocabulary_id = ?", id);
    }

    public VocabularyEntry findById(long id) {
        VocabularyEntry vocabularyEntry = jdbcTemplate.queryForObject(
                "select ve.id as ve_id, ve.created_at, ve.correct_answers_count as cac, ve.definition, " +
                        "w.name as w_name, " +
                        "v.name as v_name " +
                        "from vocabulary_entry ve " +
                        "join word w " +
                        "on ve.word_id = w.id " +
                        "join vocabulary v " +
                        "on ve.vocabulary_id = v.id " +
                        "where ve.id = ?", rs2VocabularyEntry, id);

        var veId2Synonyms = jdbcTemplate.query(
                "select ve.id as ve_id, w.name as synonym " +
                        "from vocabulary_entry__synonym__jt ves " +
                        "join word w " +
                        "on ves.word_id = w.id " +
                        "join vocabulary_entry ve " +
                        "on ves.vocabulary_entry_id = ve.id " +
                        "where ve.id = ?", new Object[]{id}, new int[]{Types.BIGINT},
                createResultSetExtractor("synonym"));

        var veId2Antonyms = jdbcTemplate.query(
                "select ve.id as ve_id, w.name as antonym " +
                        "from vocabulary_entry__antonym__jt vea " +
                        "join word w " +
                        "on vea.word_id = w.id " +
                        "join vocabulary_entry ve " +
                        "on vea.vocabulary_entry_id = ve.id " +
                        "where ve.id = ?", new Object[]{id}, new int[]{Types.BIGINT},
                createResultSetExtractor("antonym"));

        return vocabularyEntry.toBuilder()
                .synonyms(veId2Synonyms.get(id))
                .antonyms(veId2Antonyms.get(id))
                .build();
    }

    public List<VocabularyEntry> findAllWithSynonyms() {
        List<VocabularyEntry> vocabularyEntries = jdbcTemplate.query(
                "select ve.id as ve_id, ve.created_at, ve.correct_answers_count as cac, ve.definition, " +
                        "w.name as w_name, " +
                        "v.name as v_name " +
                        "from vocabulary_entry ve " +
                        "join word w " +
                        "on ve.word_id = w.id " +
                        "join vocabulary v " +
                        "on ve.vocabulary_id = v.id " +
                        "where exists (select * from vocabulary_entry__synonym__jt where vocabulary_entry_id = ve.id)",
                rs2VocabularyEntry);

        var veId2Synonyms = jdbcTemplate.query(
                "select ve.id as ve_id, w.name as synonym " +
                        "from vocabulary_entry__synonym__jt ves " +
                        "join word w " +
                        "on ves.word_id = w.id " +
                        "join vocabulary_entry ve " +
                        "on ves.vocabulary_entry_id = ve.id",
                createResultSetExtractor("synonym"));

        return vocabularyEntries.stream()
                .map(ve -> ve.toBuilder()
                        .synonyms(veId2Synonyms.get(ve.getId()))
                        .build())
                .collect(toList());
    }

    public List<VocabularyEntry> findAllWithAntonyms() {
        List<VocabularyEntry> vocabularyEntries = jdbcTemplate.query(
                "select ve.id as ve_id, ve.created_at, ve.correct_answers_count as cac, ve.definition, " +
                        "w.name as w_name, " +
                        "v.name as v_name " +
                        "from vocabulary_entry ve " +
                        "join word w " +
                        "on ve.word_id = w.id " +
                        "join vocabulary v " +
                        "on ve.vocabulary_id = v.id " +
                        "where exists (select * from vocabulary_entry__antonym__jt where vocabulary_entry_id = ve.id)",
                rs2VocabularyEntry);

        var veId2Antonyms = jdbcTemplate.query(
                "select ve.id as ve_id, w.name as antonym " +
                        "from vocabulary_entry__antonym__jt vea " +
                        "join word w on vea.word_id = w.id " +
                        "join vocabulary_entry ve on vea.vocabulary_entry_id = ve.id",
                createResultSetExtractor("antonym"));

        return vocabularyEntries.stream()
                .map(ve -> ve.toBuilder()
                        .antonyms(veId2Antonyms.get(ve.getId()))
                        .build())
                .collect(toList());
    }

    public List<VocabularyEntry> findAllForLanguageBeingLearned(long id) {
        List<VocabularyEntry> vocabularyEntries = jdbcTemplate.query(
                "select ve.id as ve_id, ve.created_at, ve.correct_answers_count as cac, ve.definition, " +
                        "w.name as w_name, " +
                        "v.name as v_name " +
                        "from vocabulary_entry ve " +
                        "join word w " +
                        "on ve.word_id = w.id " +
                        "join vocabulary v " +
                        "on ve.vocabulary_id = v.id " +
                        "where v.language_being_learned_id = ?", rs2VocabularyEntry, id);
        var veId2Synonyms = jdbcTemplate.query(
                "select ve.id as ve_id, w.name as synonym " +
                        "from vocabulary_entry__synonym__jt ves " +
                        "join word w " +
                        "on ves.word_id = w.id " +
                        "join vocabulary_entry ve " +
                        "on ves.vocabulary_entry_id = ve.id " +
                        "join vocabulary v " +
                        "on v.id = ve.vocabulary_id " +
                        "where v.language_being_learned_id = ?",
                createResultSetExtractor("synonym"), id);

        var veId2Antonyms = jdbcTemplate.query(
                "select ve.id as ve_id, w.name as antonym " +
                        "from vocabulary_entry__antonym__jt vea " +
                        "join word w " +
                        "on vea.word_id = w.id " +
                        "join vocabulary_entry ve on " +
                        "vea.vocabulary_entry_id = ve.id " +
                        "join vocabulary v " +
                        "on v.id = ve.vocabulary_id " +
                        "where v.language_being_learned_id = ?",
                createResultSetExtractor("antonym"), id);

        return vocabularyEntries.stream()
                .map(withSynonymsAndAntonyms(veId2Synonyms, veId2Antonyms))
                .collect(toList());
    }

    public int countWithId(long id) {
        return jdbcTemplate.queryForObject(
                "select count(*) " +
                        "from vocabulary_entry ve " +
                        "where ve.id = ?", Integer.class, id);
    }

    public int countWithWordIdAndVocabularyId(long wordId, long vocabularyId) {
        return jdbcTemplate.queryForObject(
                "select count(*) " +
                        "from vocabulary_entry ve " +
                        "where ve.word_id = ? " +
                        "and ve.vocabulary_id = ?", Integer.class, wordId, vocabularyId);
    }

}
