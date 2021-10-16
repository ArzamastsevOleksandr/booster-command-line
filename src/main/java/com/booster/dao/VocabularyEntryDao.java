package com.booster.dao;

import com.booster.model.VocabularyEntry;
import com.booster.model.Word;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class VocabularyEntryDao {

    private final JdbcTemplate jdbcTemplate;

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

}
