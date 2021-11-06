package com.booster.dao;

import com.booster.dao.params.AddVocabularyEntryDaoParams;
import com.booster.dao.params.UpdateVocabularyEntryDaoParams;
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
            .wordId(rs.getLong("w_id"))
            .definition(rs.getString("definition"))
            .languageId(rs.getLong("l_id"))
            .languageName(rs.getString("l_name"))
            .build();

    private final JdbcTemplate jdbcTemplate;

    // todo: one sql query?
    // todo: when pagination is ready, implement DRY
    public List<VocabularyEntry> findAll() {
        List<VocabularyEntry> vocabularyEntries = jdbcTemplate.query(
                "select ve.id as ve_id, ve.created_at, ve.correct_answers_count as cac, ve.definition as definition, " +
                        "w.name as w_name, w.id as w_id, " +
                        "l.name as l_name, l.id as l_id " +
                        "from vocabulary_entry ve " +
                        "join word w " +
                        "on ve.word_id = w.id " +
                        "join language l " +
                        "on ve.language_id = l.id", rs2VocabularyEntry);

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
                builder = builder.synonyms(veId2Synonyms.getOrDefault(ve.getId(), Set.of()));
            }
            if (veId2Antonyms.containsKey(ve.getId())) {
                builder = builder.antonyms(veId2Antonyms.getOrDefault(ve.getId(), Set.of()));
            }
            return builder.build();
        };
    }

    public void delete(long id) {
        jdbcTemplate.update("delete from vocabulary_entry__synonym__jt sjt " +
                "where sjt.vocabulary_entry_id = ?", id);
        jdbcTemplate.update("delete from vocabulary_entry__antonym__jt ajt " +
                "where ajt.vocabulary_entry_id = ?", id);
        jdbcTemplate.update("delete from vocabulary_entry " +
                "where id = ?", id);
    }

    public void addWithDefaultValues(AddVocabularyEntryDaoParams params) {
        add(params, connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "insert into vocabulary_entry " +
                            "(word_id, language_id, definition) " +
                            "values (?, ?, ?)", new String[]{"id"});
            ps.setLong(1, params.getWordId());
            ps.setLong(2, params.getLanguageId());
            ps.setString(3, params.getDefinition());
            return ps;
        });
    }

    public void addWithAllValues(AddVocabularyEntryDaoParams params) {
        add(params, connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "insert into vocabulary_entry " +
                            "(word_id, language_id, definition, created_at, correct_answers_count) " +
                            "values (?, ?, ?, ?, ?)", new String[]{"id"});
            ps.setLong(1, params.getWordId());
            ps.setLong(2, params.getLanguageId());
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
                createBatchPreparedStatementSetter(new ArrayList<>(params.getSynonymIds()), vocabularyEntryId));
        jdbcTemplate.batchUpdate(
                "insert into vocabulary_entry__antonym__jt " +
                        "(vocabulary_entry_id, word_id) " +
                        "values (?, ?)",
                createBatchPreparedStatementSetter(new ArrayList<>(params.getAntonymIds()), vocabularyEntryId));
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

    public VocabularyEntry findById(long id) {
        VocabularyEntry vocabularyEntry = jdbcTemplate.queryForObject(
                "select ve.id as ve_id, ve.created_at, ve.correct_answers_count as cac, ve.definition, " +
                        "w.name as w_name, w.id as w_id, " +
                        "l.name as l_name, l.id as l_id " +
                        "from vocabulary_entry ve " +
                        "join word w " +
                        "on ve.word_id = w.id " +
                        "join language l " +
                        "on l.id = ve.language_id " +
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
                .synonyms(veId2Synonyms.getOrDefault(id, Set.of()))
                .antonyms(veId2Antonyms.getOrDefault(id, Set.of()))
                .build();
    }

    public List<VocabularyEntry> findAllWithSynonyms() {
        List<VocabularyEntry> vocabularyEntries = jdbcTemplate.query(
                "select ve.id as ve_id, ve.created_at, ve.correct_answers_count as cac, ve.definition, " +
                        "w.name as w_name, w.id as w_id, " +
                        "l.name as l_name, l.id as l_id " +
                        "from vocabulary_entry ve " +
                        "join word w " +
                        "on ve.word_id = w.id " +
                        "join language l " +
                        "on l.id = ve.language_id " +
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
                        .synonyms(veId2Synonyms.getOrDefault(ve.getId(), Set.of()))
                        .build())
                .collect(toList());
    }

    public List<VocabularyEntry> findAllWithAntonyms() {
        List<VocabularyEntry> vocabularyEntries = jdbcTemplate.query(
                "select ve.id as ve_id, ve.created_at, ve.correct_answers_count as cac, ve.definition, " +
                        "w.name as w_name,  w.id as w_id, " +
                        "l.name as l_name, l.id as l_id " +
                        "from vocabulary_entry ve " +
                        "join word w " +
                        "on ve.word_id = w.id " +
                        "join language l " +
                        "on ve.language_id = l.id " +
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
                        .antonyms(veId2Antonyms.getOrDefault(ve.getId(), Set.of()))
                        .build())
                .collect(toList());
    }

    public List<VocabularyEntry> findAllForLanguageId(long id) {
        List<VocabularyEntry> vocabularyEntries = jdbcTemplate.query(
                "select ve.id as ve_id, ve.created_at, ve.correct_answers_count as cac, ve.definition, " +
                        "w.name as w_name, w.id as w_id, " +
                        "l.name as l_name, l.id as l_id " +
                        "from vocabulary_entry ve " +
                        "join word w " +
                        "on ve.word_id = w.id " +
                        "join language l " +
                        "on ve.language_id = l.id " +
                        "where l.id = ?", rs2VocabularyEntry, id);
        var veId2Synonyms = jdbcTemplate.query(
                "select ve.id as ve_id, w.name as synonym " +
                        "from vocabulary_entry__synonym__jt ves " +
                        "join word w " +
                        "on ves.word_id = w.id " +
                        "join vocabulary_entry ve " +
                        "on ves.vocabulary_entry_id = ve.id " +
                        "join language l " +
                        "on l.id = ve.language_id " +
                        "where l.id = ?",
                createResultSetExtractor("synonym"), id);

        var veId2Antonyms = jdbcTemplate.query(
                "select ve.id as ve_id, w.name as antonym " +
                        "from vocabulary_entry__antonym__jt vea " +
                        "join word w " +
                        "on vea.word_id = w.id " +
                        "join vocabulary_entry ve on " +
                        "vea.vocabulary_entry_id = ve.id " +
                        "join language l " +
                        "on l.id = ve.language_id " +
                        "where l.id = ?",
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

    public int countWithWordIdAndLanguageId(long wordId, long languageId) {
        return jdbcTemplate.queryForObject(
                "select count(*) " +
                        "from vocabulary_entry ve " +
                        "where ve.word_id = ? " +
                        "and ve.language_id = ?", Integer.class, wordId, languageId);
    }

    public void markDifficult(long id, boolean isDifficult) {
        jdbcTemplate.update(
                "update vocabulary_entry " +
                        "set is_difficult = ? " +
                        "where id = ?", isDifficult, id);
    }

    public int countWithLanguageId(Long id) {
        return jdbcTemplate.queryForObject(
                "select count(*) " +
                        "from vocabulary_entry " +
                        "where language_id = ?", Integer.class, id);
    }

    public void update(UpdateVocabularyEntryDaoParams params) {
        jdbcTemplate.update(
                "update vocabulary_entry " +
                        "set word_id = ?, definition = ?, correct_answers_count = ? " +
                        "where id = ?",
                params.getWordId(),
                params.getDefinition(),
                params.getCorrectAnswersCount(),
                params.getId()
        );
        jdbcTemplate.update(
                "delete from vocabulary_entry__synonym__jt " +
                        "where vocabulary_entry_id = ?",
                params.getId()
        );
        jdbcTemplate.update(
                "delete from vocabulary_entry__antonym__jt " +
                        "where vocabulary_entry_id = ?",
                params.getId()
        );
        jdbcTemplate.batchUpdate(
                "insert into vocabulary_entry__synonym__jt " +
                        "(vocabulary_entry_id, word_id) " +
                        "values (?, ?)",
                createBatchPreparedStatementSetter(new ArrayList<>(params.getSynonymIds()), params.getId()));
        jdbcTemplate.batchUpdate(
                "insert into vocabulary_entry__antonym__jt " +
                        "(vocabulary_entry_id, word_id) " +
                        "values (?, ?)",
                createBatchPreparedStatementSetter(new ArrayList<>(params.getAntonymIds()), params.getId()));
    }

    public List<VocabularyEntry> findAllInRange(int startInclusive, int endInclusive) {
        List<VocabularyEntry> vocabularyEntries = jdbcTemplate.query(
                "select ve.id as ve_id, ve.created_at, ve.correct_answers_count as cac, ve.definition as definition, " +
                        "w.name as w_name, w.id as w_id, " +
                        "l.name as l_name, l.id as l_id " +
                        "from (select row_number() over () as row_num, * from vocabulary_entry) ve " +
                        "join word w " +
                        "on ve.word_id = w.id " +
                        "join language l " +
                        "on ve.language_id = l.id " +
                        "where ve.row_num >= ? " +
                        "and ve.row_num <= ?", rs2VocabularyEntry, startInclusive, endInclusive);

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

    public List<VocabularyEntry> findAllWithSubstring(String substring) {
        List<VocabularyEntry> vocabularyEntries = jdbcTemplate.query(
                "select ve.id as ve_id, ve.created_at, ve.correct_answers_count as cac, ve.definition as definition, " +
                        "w.name as w_name, w.id as w_id, " +
                        "l.name as l_name, l.id as l_id " +
                        "from vocabulary_entry ve " +
                        "join word w " +
                        "on ve.word_id = w.id " +
                        "join language l " +
                        "on ve.language_id = l.id " +
                        "where w.name like '%?%'", rs2VocabularyEntry, substring);

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

    public List<VocabularyEntry> findAllInRangeWithSubstring(int startInclusive, int endInclusive, String substring) {
        var likeParameter = "%" + substring + "%";
        List<VocabularyEntry> vocabularyEntries = jdbcTemplate.query(
                "select ve_id, created_at, cac, definition, " +
                        "w_name, w_id, " +
                        "l.name as l_name, l.id as l_id " +
                        "from (select row_number() over () as row_num, " +
                        "ve.id as ve_id, ve.created_at, ve.correct_answers_count as cac, ve.definition as definition, ve.language_id as v_l_id, " +
                        "ww.name as w_name, ww.id as w_id " +
                        "from vocabulary_entry ve " +
                        "join word ww " +
                        "on ve.word_id = ww.id " +
                        "where ww.name like ?) as ve " +
                        "join language l " +
                        "on v_l_id = l.id " +
                        "where ve.row_num >= ? " +
                        "and ve.row_num <= ?", rs2VocabularyEntry, likeParameter, startInclusive, endInclusive);

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

    public int countTotal() {
        return jdbcTemplate.queryForObject("select count(*) from vocabulary_entry", Integer.class);
    }

    public int countWithSubstring(String substring) {
        var likeParameter = "%" + substring + "%";
        return jdbcTemplate.queryForObject("select count(*) " +
                "from vocabulary_entry ve " +
                "join word w " +
                "on w.id = ve.word_id " +
                "where w.name like ?", Integer.class, likeParameter);
    }

    public int countAny() {
        return jdbcTemplate.queryForObject("select count(*) from vocabulary_entry", Integer.class);
    }

}
