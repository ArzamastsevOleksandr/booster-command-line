package com.booster.dao;

import com.booster.model.Vocabulary;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

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

}
