package booster.dao;

import booster.dao.params.AddVocabularyEntryDaoParams;
import booster.dao.params.UpdateVocabularyEntryDaoParams;
import booster.model.VocabularyEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

@Component
@RequiredArgsConstructor
public class VocabularyEntryDao {

    public static final RowMapper<VocabularyEntry> RS_2_VOCABULARY_ENTRY = (rs, i) -> VocabularyEntry.builder()
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

    public List<VocabularyEntry> findAll() {
        return jdbcTemplate.query(
                "select ve.id as ve_id, ve.created_at, ve.correct_answers_count as cac, ve.definition as definition, " +
                        "w.name as w_name, w.id as w_id, " +
                        "l.name as l_name, l.id as l_id " +
                        "from vocabulary_entry ve " +
                        "join word w " +
                        "on ve.word_id = w.id " +
                        "join language l " +
                        "on ve.language_id = l.id " +
                        "order by cac",
                RS_2_VOCABULARY_ENTRY);
    }

    public Map<Long, Set<String>> getId2SynonymsMap() {
        // todo: simplify, ve_id is already in the join table
        return jdbcTemplate.query(
                "select ve.id as ve_id, w.name as synonym " +
                        "from vocabulary_entry__synonym__jt ves " +
                        "join word w " +
                        "on ves.word_id = w.id " +
                        "join vocabulary_entry ve " +
                        "on ves.vocabulary_entry_id = ve.id",
                equivalentsResultSetExtractor("synonym"));
    }

    public Map<Long, Set<String>> getId2AntonymsMap() {
        return jdbcTemplate.query(
                "select ve.id as ve_id, w.name as antonym " +
                        "from vocabulary_entry__antonym__jt vea " +
                        "join word w " +
                        "on vea.word_id = w.id " +
                        "join vocabulary_entry ve " +
                        "on vea.vocabulary_entry_id = ve.id",
                equivalentsResultSetExtractor("antonym"));
    }

    // todo: heavy lifting on db side - return an aggregated result? (id, tags)
    public Map<Long, Set<String>> getId2TagsMap() {
        return jdbcTemplate.query(
                "select * " +
                        "from vocabulary_entry__tag__jt",
                id2ValueResultSetExtractor("tag"));
    }

    public Map<Long, Set<String>> getId2ContextsMap() {
        return jdbcTemplate.query(
                "select * " +
                        "from vocabulary_entry__context__jt",
                id2ValueResultSetExtractor("context"));
    }

    // todo: return 1 entry instead of a Map to avoid type confusion
    public Map<Long, Set<String>> getTagsByIdMap(Long id) {
        return jdbcTemplate.query(
                "select * " +
                        "from vocabulary_entry__tag__jt " +
                        "where vocabulary_entry_id = ?",
                id2ValueResultSetExtractor("tag"),
                id
        );
    }

    public Map<Long, Set<String>> getContextsByIdMap(long id) {
        return jdbcTemplate.query(
                "select * " +
                        "from vocabulary_entry__context__jt " +
                        "where vocabulary_entry_id = ?",
                id2ValueResultSetExtractor("context"),
                id
        );
    }

    private ResultSetExtractor<Map<Long, Set<String>>> id2ValueResultSetExtractor(String column) {
        return rs -> {
            Map<Long, Set<String>> id2Tags = new HashMap<>();
            while (rs.next()) {
                id2Tags.computeIfAbsent(rs.getLong("vocabulary_entry_id"), k -> new HashSet<>())
                        .add(rs.getString(column));
            }
            return id2Tags;
        };
    }

    public long addWithDefaultValues(AddVocabularyEntryDaoParams params) {
        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "insert into vocabulary_entry " +
                            "(word_id, language_id, definition) " +
                            "values (?, ?, ?)", new String[]{"id"});
            ps.setLong(1, params.getWordId());
            ps.setLong(2, params.getLanguageId());
            ps.setString(3, params.getDefinition());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public long addWithAllValues(AddVocabularyEntryDaoParams params) {
        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
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
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public void addSynonyms(List<Long> synonymIds, long id) {
        jdbcTemplate.batchUpdate(
                "insert into vocabulary_entry__synonym__jt " +
                        "(vocabulary_entry_id, word_id) " +
                        "values (?, ?)",
                batchPreparedStatementSetterForWordIds(synonymIds, id));
    }

    public void addAntonyms(List<Long> antonymIds, long id) {
        jdbcTemplate.batchUpdate(
                "insert into vocabulary_entry__antonym__jt " +
                        "(vocabulary_entry_id, word_id) " +
                        "values (?, ?)",
                batchPreparedStatementSetterForWordIds(antonymIds, id));
    }

    public void addContexts(List<String> contexts, long id) {
        jdbcTemplate.batchUpdate(
                "insert into vocabulary_entry__context__jt " +
                        "(vocabulary_entry_id, context) " +
                        "values (?, ?)",
                batchPreparedStatementSetterForStringValues(contexts, id));
    }

    public void addTags(List<String> tags, long id) {
        jdbcTemplate.batchUpdate(
                "insert into vocabulary_entry__tag__jt " +
                        "(vocabulary_entry_id, tag) " +
                        "values (?, ?)",
                batchPreparedStatementSetterForStringValues(tags, id));
    }

    private ResultSetExtractor<Map<Long, Set<String>>> equivalentsResultSetExtractor(String columnName) {
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

    private BatchPreparedStatementSetter batchPreparedStatementSetterForWordIds(List<Long> ids, long vocabularyEntryId) {
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

    // todo: separate component
    private BatchPreparedStatementSetter batchPreparedStatementSetterForStringValues(List<String> contexts, long id) {
        return new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                String context = contexts.get(i);
                ps.setLong(1, id);
                ps.setString(2, context);
            }

            @Override
            public int getBatchSize() {
                return contexts.size();
            }
        };
    }

    public void updateCorrectAnswersCount(long id, int cacUpdated) {
        jdbcTemplate.update(
                "update vocabulary_entry ve " +
                        "set correct_answers_count = ? " +
                        "where ve.id = ?",
                cacUpdated, id);
    }

    public VocabularyEntry findById(long id) {
        return jdbcTemplate.queryForObject(
                "select ve.id as ve_id, ve.created_at, ve.correct_answers_count as cac, ve.definition, " +
                        "w.name as w_name, w.id as w_id, " +
                        "l.name as l_name, l.id as l_id " +
                        "from vocabulary_entry ve " +
                        "join word w " +
                        "on ve.word_id = w.id " +
                        "join language l " +
                        "on l.id = ve.language_id " +
                        "where ve.id = ?",
                RS_2_VOCABULARY_ENTRY,
                id);
    }

    public Map<Long, Set<String>> getAntonymsById(long id) {
        return jdbcTemplate.query(
                "select ve.id as ve_id, w.name as antonym " +
                        "from vocabulary_entry__antonym__jt vea " +
                        "join word w " +
                        "on vea.word_id = w.id " +
                        "join vocabulary_entry ve " +
                        "on vea.vocabulary_entry_id = ve.id " +
                        "where ve.id = ?", new Object[]{id}, new int[]{Types.BIGINT},
                equivalentsResultSetExtractor("antonym"));
    }

    public Map<Long, Set<String>> getSynonymsById(long id) {
        return jdbcTemplate.query(
                "select ve.id as ve_id, w.name as synonym " +
                        "from vocabulary_entry__synonym__jt ves " +
                        "join word w " +
                        "on ves.word_id = w.id " +
                        "join vocabulary_entry ve " +
                        "on ves.vocabulary_entry_id = ve.id " +
                        "where ve.id = ?", new Object[]{id}, new int[]{Types.BIGINT},
                equivalentsResultSetExtractor("synonym"));
    }

    public List<VocabularyEntry> findAllWithSynonyms(int limit) {
        return jdbcTemplate.query(
                "select ve.id as ve_id, ve.created_at, ve.correct_answers_count as cac, ve.definition, " +
                        "w.name as w_name, w.id as w_id, " +
                        "l.name as l_name, l.id as l_id " +
                        "from vocabulary_entry ve " +
                        "join word w " +
                        "on ve.word_id = w.id " +
                        "join language l " +
                        "on l.id = ve.language_id " +
                        "where ve.id in (select distinct vocabulary_entry_id from vocabulary_entry__synonym__jt) " +
                        "order by cac " +
                        "limit ?",
                RS_2_VOCABULARY_ENTRY,
                limit);
    }

    public List<VocabularyEntry> findAllWithAntonyms(int limit) {
        return jdbcTemplate.query(
                "select ve.id as ve_id, ve.created_at, ve.correct_answers_count as cac, ve.definition, " +
                        "w.name as w_name,  w.id as w_id, " +
                        "l.name as l_name, l.id as l_id " +
                        "from vocabulary_entry ve " +
                        "join word w " +
                        "on ve.word_id = w.id " +
                        "join language l " +
                        "on ve.language_id = l.id " +
                        "where ve.id in (select distinct vocabulary_entry_id from vocabulary_entry__antonym__jt) " +
                        "order by cac " +
                        "limit ?",
                RS_2_VOCABULARY_ENTRY,
                limit);
    }

    public List<VocabularyEntry> findAllWithAntonymsAndSynonyms(int limit) {
        return jdbcTemplate.query(
                "select ve.id as ve_id, ve.created_at, ve.correct_answers_count as cac, ve.definition, " +
                        "w.name as w_name,  w.id as w_id, " +
                        "l.name as l_name, l.id as l_id " +
                        "from vocabulary_entry ve " +
                        "join word w " +
                        "on ve.word_id = w.id " +
                        "join language l " +
                        "on ve.language_id = l.id " +
                        "where ve.id in " +
                        "(select distinct vocabulary_entry_id from vocabulary_entry__synonym__jt " +
                        "intersect " +
                        "select distinct vocabulary_entry_id from vocabulary_entry__antonym__jt) " +
                        "order by cac " +
                        "limit ?",
                RS_2_VOCABULARY_ENTRY,
                limit);
    }

    public List<VocabularyEntry> findAllForLanguageId(long id) {
        return jdbcTemplate.query(
                "select ve.id as ve_id, ve.created_at, ve.correct_answers_count as cac, ve.definition, " +
                        "w.name as w_name, w.id as w_id, " +
                        "l.name as l_name, l.id as l_id " +
                        "from vocabulary_entry ve " +
                        "join word w " +
                        "on ve.word_id = w.id " +
                        "join language l " +
                        "on ve.language_id = l.id " +
                        "where l.id = ?",
                RS_2_VOCABULARY_ENTRY,
                id);
    }

    public Map<Long, Set<String>> getId2TagsMapForLanguageId(long id) {
        return jdbcTemplate.query(
                "select * " +
                        "from vocabulary_entry__tag__jt vetjt " +
                        "join vocabulary_entry ve " +
                        "on vetjt.vocabulary_entry_id = ve.id " +
                        "join language l " +
                        "on l.id = ve.language_id " +
                        "where l.id = ?",
                id2ValueResultSetExtractor("tag"),
                id);
    }

    public Map<Long, Set<String>> getId2ContextsMapForLanguageId(long id) {
        return jdbcTemplate.query(
                "select * " +
                        "from vocabulary_entry__context__jt vecjt " +
                        "join vocabulary_entry ve " +
                        "on vecjt.vocabulary_entry_id = ve.id " +
                        "join language l " +
                        "on l.id = ve.language_id " +
                        "where l.id = ?",
                id2ValueResultSetExtractor("context"),
                id);
    }

    public Map<Long, Set<String>> getId2AntonymsMapForLanguageId(long id) {
        return jdbcTemplate.query(
                "select ve.id as ve_id, w.name as antonym " +
                        "from vocabulary_entry__antonym__jt vea " +
                        "join word w " +
                        "on vea.word_id = w.id " +
                        "join vocabulary_entry ve on " +
                        "vea.vocabulary_entry_id = ve.id " +
                        "join language l " +
                        "on l.id = ve.language_id " +
                        "where l.id = ?",
                equivalentsResultSetExtractor("antonym"),
                id);
    }

    public Map<Long, Set<String>> getId2SynonymsMapForLanguageId(long id) {
        return jdbcTemplate.query(
                "select ve.id as ve_id, w.name as synonym " +
                        "from vocabulary_entry__synonym__jt ves " +
                        "join word w " +
                        "on ves.word_id = w.id " +
                        "join vocabulary_entry ve " +
                        "on ves.vocabulary_entry_id = ve.id " +
                        "join language l " +
                        "on l.id = ve.language_id " +
                        "where l.id = ?",
                equivalentsResultSetExtractor("synonym"),
                id);
    }

    public Integer countWithId(long id) {
        return jdbcTemplate.queryForObject(
                "select count(*) " +
                        "from vocabulary_entry " +
                        "where id = ?",
                Integer.class,
                id);
    }

    public Integer countWithWordIdAndLanguageId(long wordId, long languageId) {
        return jdbcTemplate.queryForObject(
                "select count(*) " +
                        "from vocabulary_entry " +
                        "where word_id = ? " +
                        "and language_id = ?",
                Integer.class,
                wordId, languageId);
    }

    public void markDifficult(long id, boolean isDifficult) {
        jdbcTemplate.update(
                "update vocabulary_entry " +
                        "set is_difficult = ? " +
                        "where id = ?",
                isDifficult, id);
    }

    public Integer countWithLanguageId(Long id) {
        return jdbcTemplate.queryForObject(
                "select count(*) " +
                        "from vocabulary_entry " +
                        "where language_id = ?",
                Integer.class,
                id);
    }

    public void deleteAntonymsById(long id) {
        jdbcTemplate.update(
                "delete from vocabulary_entry__antonym__jt " +
                        "where vocabulary_entry_id = ?",
                id);
    }

    public void deleteSynonymsById(long id) {
        jdbcTemplate.update(
                "delete from vocabulary_entry__synonym__jt " +
                        "where vocabulary_entry_id = ?",
                id);
    }

    public void updateVocabularyEntry(UpdateVocabularyEntryDaoParams params) {
        jdbcTemplate.update(
                "update vocabulary_entry " +
                        "set word_id = ?, " +
                        "definition = ?, " +
                        "correct_answers_count = ? " +
                        "where id = ?",
                params.getWordId(), params.getDefinition(), params.getCorrectAnswersCount(), params.getId());
    }

    public List<VocabularyEntry> findAllInRange(int startInclusive, int endInclusive) {
        return jdbcTemplate.query(
                "select ve.id as ve_id, ve.created_at, ve.correct_answers_count as cac, ve.definition as definition, " +
                        "w.name as w_name, w.id as w_id, " +
                        "l.name as l_name, l.id as l_id " +
                        "from (select row_number() over (order by correct_answers_count) as row_num, * from vocabulary_entry) ve " +
                        "join word w " +
                        "on ve.word_id = w.id " +
                        "join language l " +
                        "on ve.language_id = l.id " +
                        "where ve.row_num >= ? " +
                        "and ve.row_num <= ? ",
                RS_2_VOCABULARY_ENTRY,
                startInclusive, endInclusive);
    }

    public List<VocabularyEntry> findAllWithSubstring(String substring) {
        return jdbcTemplate.query(
                "select ve.id as ve_id, ve.created_at, ve.correct_answers_count as cac, ve.definition as definition, " +
                        "w.name as w_name, w.id as w_id, " +
                        "l.name as l_name, l.id as l_id " +
                        "from vocabulary_entry ve " +
                        "join word w " +
                        "on ve.word_id = w.id " +
                        "join language l " +
                        "on ve.language_id = l.id " +
                        "where w.name like '%?%'",
                RS_2_VOCABULARY_ENTRY,
                substring);
    }

    public List<VocabularyEntry> findAllInRangeWithSubstring(int startInclusive, int endInclusive, String substring) {
        var likeParameter = "%" + substring + "%";
        return jdbcTemplate.query(
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
                        "and ve.row_num <= ?",
                RS_2_VOCABULARY_ENTRY,
                likeParameter, startInclusive, endInclusive);
    }

    public Integer countTotal() {
        return jdbcTemplate.queryForObject("select count(*) from vocabulary_entry", Integer.class);
    }

    public Integer countWithSubstring(String substring) {
        var likeParameter = "%" + substring + "%";
        return jdbcTemplate.queryForObject(
                "select count(*) " +
                        "from vocabulary_entry ve " +
                        "join word w " +
                        "on w.id = ve.word_id " +
                        "where w.name like ?",
                Integer.class,
                likeParameter);
    }

    public Integer countAny() {
        return jdbcTemplate.queryForObject(
                "select count(*) " +
                        "from vocabulary_entry",
                Integer.class);
    }

    public Integer countWithSynonyms() {
        return jdbcTemplate.queryForObject(
                "select count(distinct vocabulary_entry_id) " +
                        "from vocabulary_entry__synonym__jt",
                Integer.class);
    }

    public Integer countWithAntonyms() {
        return jdbcTemplate.queryForObject(
                "select count(distinct vocabulary_entry_id) " +
                        "from vocabulary_entry__antonym__jt",
                Integer.class);
    }

    public Integer countWithAntonymsAndSynonyms() {
        return jdbcTemplate.queryForObject(
                "select distinct vocabulary_entry_id " +
                        "from vocabulary_entry__synonym__jt " +
                        "intersect " +
                        "select distinct vocabulary_entry_id " +
                        "from vocabulary_entry__antonym__jt",
                Integer.class);
    }

    public void deleteSynonyms(long id) {
        jdbcTemplate.update(
                "delete from vocabulary_entry__synonym__jt sjt " +
                        "where sjt.vocabulary_entry_id = ?",
                id);
    }

    public void deleteAntonyms(long id) {
        jdbcTemplate.update(
                "delete from vocabulary_entry__antonym__jt ajt " +
                        "where ajt.vocabulary_entry_id = ?",
                id);
    }

    public void deleteContexts(long id) {
        jdbcTemplate.update(
                "delete from vocabulary_entry__context__jt cjt " +
                        "where cjt.vocabulary_entry_id = ?",
                id);
    }

    public void delete(long id) {
        jdbcTemplate.update(
                "delete from vocabulary_entry " +
                        "where id = ?",
                id);
    }

    public void addTag(String tag, long id) {
        jdbcTemplate.update(
                "insert into vocabulary_entry__tag__jt " +
                        "(vocabulary_entry_id, tag) " +
                        "values (?, ?)",
                id, tag);
    }

}
